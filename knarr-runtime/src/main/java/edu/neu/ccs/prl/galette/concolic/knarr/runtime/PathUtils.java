package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.concolic.knarr.green.GaletteGreenBridge;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.HashSet;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Galette-based path constraint utilities.
 *
 * This class replaces the original Phosphor-based PathUtils with
 * Galette-compatible APIs for managing symbolic execution constraints.
 *
 */
public class PathUtils {

    /**
     * Constants for bit vector operations.
     */
    private static IntConstant O000FFFF;

    public static BVConstant BV0_32;

    /**
     * Current path condition wrapper.
     */
    private static PathConditionWrapper curPC;

    /**
     * Configuration flags.
     */
    public static final boolean IGNORE_SHIFTS = true;

    public static final String INTERNAL_NAME = "edu/neu/ccs/prl/galette/concolic/knarr/runtime/PathUtils";

    /**
     * JPF initialization flag.
     */
    static boolean JPFInited = false;

    /**
     * Set of used labels to prevent duplicates.
     */
    static HashSet<String> usedLabels = new HashSet<String>();

    /**
     * Get the current path condition.
     *
     * @return Current path condition wrapper
     */
    public static PathConditionWrapper getCurPC() {
        if (curPC == null) {
            curPC = new PathConditionWrapper();
        }
        return curPC;
    }

    /**
     * Reset the PathUtils state for testing.
     */
    public static void reset() {
        usedLabels.clear();
        JPFInited = false;
        curPC = null;
    }

    /**
     * Check label validity and initialize JPF if needed.
     *
     * @param label The label to check
     */
    public static void checkLabelAndInitJPF(String label) {
        if (label == null || usedLabels.contains(label)) {
            throw new IllegalArgumentException("Invalid (dup?) label: \"" + label + "\"");
        }
        if (label.contains(" ")) {
            throw new IllegalArgumentException("label has spaces, but must not: \"" + label + "\"");
        }
        // Note: In Galette, we don't have the same taint checking as Phosphor
        // TODO: Add equivalent Galette tag checking if needed
        usedLabels.add(label);
        if (!JPFInited) {
            initJPF();
        }
    }

    /**
     * Initialize JPF (Java PathFinder) compatibility.
     */
    private static void initJPF() {
        // TODO: Implement JPF initialization for Galette
        JPFInited = true;
    }

    /**
     * Perform a long operation with symbolic tracking.
     *
     * @param lTag Tag for left operand
     * @param lVal Left operand value
     * @param rTag Tag for right operand
     * @param rVal Right operand value
     * @param op Operation code
     * @param result Result container
     * @return Result with updated tag
     */
    public static GaletteTaintedLong performLongOp(
            Tag lTag, long lVal, Tag rTag, long rVal, int op, GaletteTaintedLong result) {
        if (lTag == null && rTag == null) {
            result.tag = null;
            result.value = performConcreteLongOp(lVal, rVal, op);
        } else {
            Expression l = (lTag != null)
                    ? GaletteGreenBridge.tagToGreenExpression(lTag, lVal)
                    : new IntConstant((int) lVal); // Truncate for Green solver

            Expression r =
                    (rTag != null) ? GaletteGreenBridge.tagToGreenExpression(rTag, rVal) : new IntConstant((int) rVal);

            Operator operator = getOperatorForOpcode(op);
            if (operator != null) {
                Expression expr = GaletteGreenBridge.createBinaryOp(l, operator, r);
                result.tag = createTagFromExpression(expr);
                result.value = performConcreteLongOp(lVal, rVal, op);

                // Add constraint if this creates a new symbolic relationship
                getCurPC().addConstraint(expr);
            } else {
                // Unsupported operation, treat as concrete
                result.tag = null;
                result.value = performConcreteLongOp(lVal, rVal, op);
            }
        }
        return result;
    }

    /**
     * Perform a concrete long operation.
     *
     * @param lVal Left operand
     * @param rVal Right operand
     * @param op Operation code
     * @return Result of the operation
     */
    private static long performConcreteLongOp(long lVal, long rVal, int op) {
        switch (op) {
            case 97: // LADD
                return lVal + rVal;
            case 101: // LSUB
                return lVal - rVal;
            case 105: // LMUL
                return lVal * rVal;
            case 109: // LDIV
                return rVal != 0 ? lVal / rVal : 0; // Avoid division by zero
            case 113: // LREM
                return rVal != 0 ? lVal % rVal : 0;
            case 121: // LSHL
                return lVal << rVal;
            case 123: // LSHR
                return lVal >> rVal;
            case 125: // LUSHR
                return lVal >>> rVal;
            case 127: // LAND
                return lVal & rVal;
            case 129: // LOR
                return lVal | rVal;
            case 131: // LXOR
                return lVal ^ rVal;
            default:
                return lVal; // Unknown operation, return left operand
        }
    }

    /**
     * Convert bytecode operation to Green operator.
     *
     * @param opcode The bytecode operation
     * @return Corresponding Green operator, or null if not supported
     */
    private static Operator getOperatorForOpcode(int opcode) {
        switch (opcode) {
            case 96: // IADD
            case 97: // LADD
            case 98: // FADD
            case 99: // DADD
                return Operator.ADD;
            case 100: // ISUB
            case 101: // LSUB
            case 102: // FSUB
            case 103: // DSUB
                return Operator.SUB;
            case 104: // IMUL
            case 105: // LMUL
            case 106: // FMUL
            case 107: // DMUL
                return Operator.MUL;
            case 108: // IDIV
            case 109: // LDIV
            case 110: // FDIV
            case 111: // DDIV
                return Operator.DIV;
            case 112: // IREM
            case 113: // LREM
            case 114: // FREM
            case 115: // DREM
                return Operator.MOD;
            case 126: // IAND
            case 127: // LAND
                return Operator.BIT_AND;
            case 128: // IOR
            case 129: // LOR
                return Operator.BIT_OR;
            case 130: // IXOR
            case 131: // LXOR
                return Operator.BIT_XOR;
            default:
                return null; // Unsupported operation
        }
    }

    /**
     * Create a Galette tag from a Green expression.
     *
     * @param expr The Green expression
     * @return Galette tag representing the expression
     */
    private static Tag createTagFromExpression(Expression expr) {
        // For now, create a tag with the expression string as label
        // TODO: Implement more sophisticated tag creation
        return Tag.of("expr_" + expr.toString().hashCode());
    }

    /**
     * Simple container for tagged long values.
     */
    public static class GaletteTaintedLong {
        public Tag tag;
        public long value;

        public GaletteTaintedLong() {
            this.tag = null;
            this.value = 0L;
        }

        public GaletteTaintedLong(long value, Tag tag) {
            this.value = value;
            this.tag = tag;
        }
    }

    /**
     * Reset the current path condition.
     */
    public static void resetPC() {
        curPC = new PathConditionWrapper();
    }

    /**
     * Add a domain constraint for an integer variable.
     * This constrains the variable to a specific range: min <= var < max
     *
     * @param varName Variable name for the symbolic integer
     * @param min Minimum value (inclusive)
     * @param max Maximum value (exclusive)
     */
    public static void addIntDomainConstraint(String varName, int min, int max) {
        try {
            IntVariable var = new IntVariable(varName, Integer.MIN_VALUE, Integer.MAX_VALUE);
            IntConstant minConst = new IntConstant(min);
            IntConstant maxConst = new IntConstant(max);

            // min <= var
            Expression lowerBound = new BinaryOperation(Operator.LE, minConst, var);
            // var < max
            Expression upperBound = new BinaryOperation(Operator.LT, var, maxConst);
            // min <= var AND var < max
            Expression domain = new BinaryOperation(Operator.AND, lowerBound, upperBound);

            getCurPC().addConstraint(domain);

            if (GaletteSymbolicator.DEBUG) {
                System.out.println("[PathUtils:addIntDomainConstraint] Added domain constraint: " + min + " <= "
                        + varName + " < " + max);
            }
        } catch (Exception e) {
            System.err.println("[PathUtils:addIntDomainConstraint] Failed to add domain constraint: " + e.getMessage());
        }
    }

    // Note: addIntDomainConstraintAuto() removed - automatic switch instrumentation disabled.
    // Use addIntDomainConstraint(String varName, int min, int max) for manual collection.

    /**
     * Add a switch/case constraint for current path.
     * This records that the variable equals the specific value taken in this execution.
     *
     * @param varName Variable name for the symbolic integer
     * @param concreteValue The concrete value taken in this execution path
     */
    public static void addSwitchConstraint(String varName, int concreteValue) {
        try {
            IntVariable var = new IntVariable(varName, Integer.MIN_VALUE, Integer.MAX_VALUE);
            IntConstant valueConst = new IntConstant(concreteValue);

            // var == concreteValue
            Expression constraint = new BinaryOperation(Operator.EQ, var, valueConst);

            getCurPC().addConstraint(constraint);

            if (GaletteSymbolicator.DEBUG) {
                System.out.println("[PathUtils] Added switch constraint: " + varName + " == " + concreteValue);
            }
        } catch (Exception e) {
            System.err.println("[PathUtils] Failed to add switch constraint: " + e.getMessage());
        }
    }

    /**
     * Add a switch constraint using a Tag (similar to old Knarr-Phosphor version).
     * This extracts the symbolic expression from the tag and records the constraint.
     *
     * @param tag The symbolic tag associated with the switched value
     * @param concreteValue The concrete value taken in this execution
     */
    public static void addSwitchConstraint(Tag tag, int concreteValue) {
        if (tag == null || tag.isEmpty()) {
            // Fallback to string-based constraint with default name
            addSwitchConstraint("switch_value", concreteValue);
            return;
        }

        try {
            // Extract symbolic expression from tag
            Expression varExpr = GaletteGreenBridge.tagToExpression(tag);
            if (varExpr == null) {
                // Create a new symbolic variable based on tag
                String varName = "tagged_var_" + tag.toString().hashCode();
                varExpr = new IntVariable(varName, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }

            // Create constraint: varExpr == concreteValue
            IntConstant valueConst = new IntConstant(concreteValue);
            Expression constraint = new BinaryOperation(Operator.EQ, varExpr, valueConst);

            getCurPC().addConstraint(constraint);

            if (GaletteSymbolicator.DEBUG) {
                System.out.println("[PathUtils] Added tagged switch constraint: " + varExpr + " == " + concreteValue);
            }
        } catch (Exception e) {
            System.err.println("[PathUtils] Failed to add tagged switch constraint: " + e.getMessage());
            // Fallback to string-based
            addSwitchConstraint("switch_value", concreteValue);
        }
    }

    /**
     * Add a switch constraint with all possible case values (similar to old Knarr-Phosphor).
     * This method records the taken value and can optionally record NOT constraints for other cases.
     *
     * @param tag The symbolic tag
     * @param concreteValue The value that was taken
     * @param allCases All possible case values in the switch
     */
    public static void addSwitchConstraint(Tag tag, int concreteValue, int[] allCases) {
        if (tag == null || tag.isEmpty()) {
            // Fallback to simple constraint
            addSwitchConstraint("switch_value", concreteValue);
            return;
        }

        try {
            Expression varExpr = GaletteGreenBridge.tagToExpression(tag);
            if (varExpr == null) {
                String varName = "tagged_switch_" + tag.toString().hashCode();
                varExpr = new IntVariable(varName, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }

            // Add domain constraint based on all cases
            if (allCases != null && allCases.length > 0) {
                int minCase = allCases[0];
                int maxCase = allCases[0];
                for (int c : allCases) {
                    minCase = Math.min(minCase, c);
                    maxCase = Math.max(maxCase, c);
                }

                // Domain constraint: min <= var <= max
                Expression lowerBound = new BinaryOperation(Operator.GE, varExpr, new IntConstant(minCase));
                Expression upperBound = new BinaryOperation(Operator.LE, varExpr, new IntConstant(maxCase));
                Expression domainConstraint = new BinaryOperation(Operator.AND, lowerBound, upperBound);
                getCurPC().addConstraint(domainConstraint);
            }

            // Add the equality constraint for the taken value
            Expression takenConstraint = new BinaryOperation(Operator.EQ, varExpr, new IntConstant(concreteValue));
            getCurPC().addConstraint(takenConstraint);

            // Optionally add NOT constraints for other cases (for better constraint propagation)
            if (allCases != null) {
                for (int caseValue : allCases) {
                    if (caseValue != concreteValue) {
                        // This branch was NOT taken
                        Expression notTakenConstraint =
                                new BinaryOperation(Operator.NE, varExpr, new IntConstant(caseValue));
                        // Note: We might not want to add all NOT constraints as it could overconstrain
                        // Only add if specifically needed for the solver
                    }
                }
            }

            if (GaletteSymbolicator.DEBUG) {
                System.out.println("[PathUtils] Added switch constraint with cases: " + varExpr + " == " + concreteValue
                        + " (cases: " + java.util.Arrays.toString(allCases) + ")");
            }
        } catch (Exception e) {
            System.err.println("[PathUtils] Failed to add switch constraint with cases: " + e.getMessage());
            addSwitchConstraint("switch_value", concreteValue);
        }
    }

    /**
     * Automatically record a branch constraint when a conditional jump is taken/not taken.
     * This method is called by bytecode instrumentation at branch points.
     *
     * @param tag The symbolic tag associated with the branch condition value
     * @param opcode The bytecode opcode of the branch instruction (IFEQ, IFNE, IFLT, etc.)
     * @param branchTaken True if the branch was taken, false otherwise
     */
    public static void recordBranchConstraint(Tag tag, int opcode, boolean branchTaken) {
        if (tag == null) {
            return; // No symbolic tag, skip constraint collection
        }

        try {
            Expression expr = GaletteGreenBridge.tagToGreenExpression(tag, 0);
            if (expr == null) {
                return;
            }

            // Convert opcode to Green operator
            Operator op = getOperatorForBranchOpcode(opcode, branchTaken);
            if (op == null) {
                return;
            }

            // Create constraint: expr op 0
            IntConstant zero = new IntConstant(0);
            Expression constraint = new BinaryOperation(op, expr, zero);

            getCurPC().addConstraint(constraint);

            if (GaletteSymbolicator.DEBUG) {
                System.out.println("[PathUtils] Recorded branch constraint: " + constraint + " (opcode=" + opcode
                        + ", taken=" + branchTaken + ")");
            }
        } catch (Exception e) {
            System.err.println("[PathUtils] Failed to record branch constraint: " + e.getMessage());
        }
    }

    // Note: recordSwitchConstraintAuto() removed - automatic switch instrumentation disabled.
    // Use addSwitchConstraint(String varName, int value) for manual collection.

    /**
     * Convert a branch opcode to a Green operator, accounting for whether branch was taken.
     *
     * @param opcode Branch bytecode opcode (IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE)
     * @param branchTaken Whether the branch was taken
     * @return Corresponding Green operator, or null if not supported
     */
    private static Operator getOperatorForBranchOpcode(int opcode, boolean branchTaken) {
        // When branch is taken, use the opcode's operator
        // When branch is NOT taken, use the negated operator

        switch (opcode) {
            case 153: // IFEQ
                return branchTaken ? Operator.EQ : Operator.NE;
            case 154: // IFNE
                return branchTaken ? Operator.NE : Operator.EQ;
            case 155: // IFLT
                return branchTaken ? Operator.LT : Operator.GE;
            case 156: // IFGE
                return branchTaken ? Operator.GE : Operator.LT;
            case 157: // IFGT
                return branchTaken ? Operator.GT : Operator.LE;
            case 158: // IFLE
                return branchTaken ? Operator.LE : Operator.GT;
            default:
                return null;
        }
    }
}
