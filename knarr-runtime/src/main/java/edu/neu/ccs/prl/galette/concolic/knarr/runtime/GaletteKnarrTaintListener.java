package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.concolic.knarr.green.GaletteGreenBridge;
import edu.neu.ccs.prl.galette.concolic.knarr.listener.ConcolicTaintListener;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Galette-based taint listener for concolic execution.
 *
 * This class migrates Knarr's Phosphor-based TaintListener to use Galette APIs.
 * It handles taint propagation events and converts them to path constraints.
 * @purpose Knarr-specific taint listener
 * @feature Integrates Knarr symbolic execution with Galette
 *
 */
public class GaletteKnarrTaintListener implements ConcolicTaintListener {

    /**
     * Singleton instance.
     */
    private static final GaletteKnarrTaintListener INSTANCE = new GaletteKnarrTaintListener();

    /**
     * Array name tracking for symbolic arrays.
     */
    public static IdentityHashMap<Object, LinkedList<ArrayVariable>> arrayNames = new IdentityHashMap<>();

    /**
     * Configuration constants.
     */
    public static final boolean DEBUG = Boolean.valueOf(System.getProperty("DEBUG", "false"));

    public static int IGNORE_CONCRETE_ARRAY_INITIAL_CONTENTS = 1000;

    public static int IGNORE_LARGE_ARRAY_SIZE = 20000;
    public static int IGNORE_LARGE_ARRAY_INDEX = 500;

    /**
     * Queue of symbolized arrays.
     */
    public static ConcurrentLinkedQueue<Tag[]> symbolizedArrays = new ConcurrentLinkedQueue<>();

    /**
     * Get the singleton instance.
     */
    public static GaletteKnarrTaintListener getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor for singleton.
     */
    private GaletteKnarrTaintListener() {
        // Initialize constants
        try {
            initializeConstants();
        } catch (Exception e) {
            System.err.println("Failed to initialize GaletteKnarrTaintListener constants: " + e.getMessage());
        }
    }

    /**
     * Initialize Green solver constants.
     */
    private void initializeConstants() {
        // Initialize bit vector constants if needed
        // TODO: Implement if BV operations are needed
    }

    @Override
    public void onBranch(Tag condition, boolean taken) {
        if (condition == null || condition.isEmpty()) {
            return; // No symbolic information
        }

        try {
            // Convert tag to Green expression
            Expression condExpr = GaletteGreenBridge.tagToGreenExpression(condition, taken);

            if (condExpr != null) {
                // Create constraint based on branch direction
                Expression constraint;
                if (taken) {
                    // Branch was taken, constraint is the condition itself
                    constraint = condExpr;
                } else {
                    // Branch was not taken, constraint is negation of condition
                    constraint = GaletteGreenBridge.createUnaryOp(Operator.NOT, condExpr);
                }

                // Add constraint to current path condition
                PathUtils.getCurPC().addConstraint(constraint);

                // Update control stack
                ConcolicControlStack.pushControl(condition);
            }
        } catch (Exception e) {
            System.err.println("Error in onBranch: " + e.getMessage());
        }
    }

    @Override
    public void onArithmetic(Tag operand1, Tag operand2, Tag result) {
        if (result == null || result.isEmpty()) {
            return; // No symbolic result
        }

        try {
            // Create arithmetic constraint relating operands to result
            if (operand1 != null && operand2 != null) {
                Expression left = GaletteGreenBridge.tagToGreenExpression(operand1, 0);
                Expression right = GaletteGreenBridge.tagToGreenExpression(operand2, 0);
                Expression resultExpr = GaletteGreenBridge.tagToGreenExpression(result, 0);

                if (left != null && right != null && resultExpr != null) {
                    // Create constraint: result = operand1 + operand2 (example for addition)
                    Expression addExpr = GaletteGreenBridge.createBinaryOp(left, Operator.ADD, right);
                    Expression constraint = GaletteGreenBridge.createBinaryOp(resultExpr, Operator.EQ, addExpr);

                    PathUtils.getCurPC().addConstraint(constraint);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in onArithmetic: " + e.getMessage());
        }
    }

    @Override
    public void onComparison(Tag left, Tag right, int opcode, boolean result) {
        if (left == null && right == null) {
            return; // No symbolic operands
        }

        try {
            Expression leftExpr =
                    (left != null) ? GaletteGreenBridge.tagToGreenExpression(left, 0) : new IntConstant(0);
            Expression rightExpr =
                    (right != null) ? GaletteGreenBridge.tagToGreenExpression(right, 0) : new IntConstant(0);

            if (leftExpr != null && rightExpr != null) {
                Operator op = getComparisonOperator(opcode);
                if (op != null) {
                    Expression constraint = GaletteGreenBridge.createBinaryOp(leftExpr, op, rightExpr);

                    // If the comparison result is false, negate the constraint
                    if (!result) {
                        constraint = GaletteGreenBridge.createUnaryOp(Operator.NOT, constraint);
                    }

                    PathUtils.getCurPC().addConstraint(constraint);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in onComparison: " + e.getMessage());
        }
    }

    @Override
    public void onPathConstraint(Object constraint) {
        if (constraint instanceof Expression) {
            PathUtils.getCurPC().addConstraint((Expression) constraint);
        } else {
            System.err.println("Unknown constraint type: " + (constraint != null ? constraint.getClass() : "null"));
        }
    }

    @Override
    public void onMethodCall(String methodName, Tag[] arguments, Tag result) {
        if (DEBUG) {
            System.out.println("Method call: " + methodName + " with " + (arguments != null ? arguments.length : 0)
                    + " arguments");
        }

        // For now, just log method calls with symbolic arguments
        // TODO: Implement method-specific constraint generation
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] != null && !arguments[i].isEmpty()) {
                    Expression argExpr = GaletteGreenBridge.tagToGreenExpression(arguments[i], i);
                    if (argExpr != null) {
                        // Create constraint relating argument to method behavior
                        // This is a placeholder - real implementation would be method-specific
                        PathUtils.getCurPC().addConstraint(argExpr);
                    }
                }
            }
        }
    }

    @Override
    public void onArrayAccess(Tag arrayTag, Tag indexTag, Tag valueTag, boolean isWrite) {
        try {
            if (arrayTag != null && indexTag != null) {
                Expression arrayExpr = GaletteGreenBridge.tagToGreenExpression(arrayTag, 0);
                Expression indexExpr = GaletteGreenBridge.tagToGreenExpression(indexTag, 0);

                if (arrayExpr != null && indexExpr != null) {
                    // Create array access constraint
                    // For now, just add the index constraint
                    PathUtils.getCurPC().addConstraint(indexExpr);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in onArrayAccess: " + e.getMessage());
        }
    }

    /**
     * Convert comparison bytecode to Green operator.
     *
     * @param opcode The comparison bytecode
     * @return Corresponding Green operator, or null if not supported
     */
    private Operator getComparisonOperator(int opcode) {
        switch (opcode) {
            case 153: // IFEQ
            case 159: // IF_ICMPEQ
            case 165: // IF_ACMPEQ
                return Operator.EQ;
            case 154: // IFNE
            case 160: // IF_ICMPNE
            case 166: // IF_ACMPNE
                return Operator.NE;
            case 155: // IFLT
            case 161: // IF_ICMPLT
                return Operator.LT;
            case 156: // IFGE
            case 162: // IF_ICMPGE
                return Operator.GE;
            case 157: // IFGT
            case 163: // IF_ICMPGT
                return Operator.GT;
            case 158: // IFLE
            case 164: // IF_ICMPLE
                return Operator.LE;
            default:
                return null;
        }
    }

    /**
     * Handle array element access for symbolic arrays.
     *
     * @param array The array object
     * @param index The access index
     * @param indexTag Tag for the index
     * @return Array variable for the access, or null
     */
    public ArrayVariable handleArrayAccess(Object array, int index, Tag indexTag) {
        if (array == null) {
            return null;
        }

        try {
            LinkedList<ArrayVariable> arrayVars = getOrInitArray(array);
            if (arrayVars == null || arrayVars.isEmpty()) {
                return null;
            }

            // For now, return the first array variable
            // TODO: Implement proper index-based array access
            return arrayVars.getFirst();
        } catch (Exception e) {
            System.err.println("Error in handleArrayAccess: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get or initialize array variables for an array object.
     *
     * @param array The array object
     * @return List of array variables, or null if array is too large
     */
    private LinkedList<ArrayVariable> getOrInitArray(Object array) {
        LinkedList<ArrayVariable> ret = arrayNames.get(array);
        if (ret != null) {
            return ret;
        }

        if (!array.getClass().isArray()) {
            return null;
        }

        int length = Array.getLength(array);
        if (length > IGNORE_LARGE_ARRAY_SIZE) {
            return null; // Array too large to track
        }

        Class<?> componentType = array.getClass().getComponentType().isPrimitive()
                ? array.getClass().getComponentType()
                : Object.class;

        LinkedList<ArrayVariable> arrayVars = new LinkedList<>();

        // Create array variables for each element (simplified approach)
        String baseName = "array_" + System.identityHashCode(array);
        for (int i = 0; i < Math.min(length, IGNORE_LARGE_ARRAY_INDEX); i++) {
            ArrayVariable arrayVar = new ArrayVariable(baseName + "_" + i, componentType);
            arrayVars.add(arrayVar);
        }

        arrayNames.put(array, arrayVars);
        return arrayVars;
    }

    /**
     * Reset the listener state.
     */
    public static void reset() {
        arrayNames.clear();
        symbolizedArrays.clear();
        PathUtils.reset();
        ConcolicControlStack.clearControl();
    }

    /**
     * Get statistics about the current symbolic execution state.
     *
     * @return Statistics string
     */
    public static String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("GaletteKnarrTaintListener Statistics:\n");
        sb.append("  Tracked arrays: ").append(arrayNames.size()).append("\n");
        sb.append("  Symbolized arrays: ").append(symbolizedArrays.size()).append("\n");
        sb.append("  Path constraints: ").append(PathUtils.getCurPC().size()).append("\n");
        sb.append("  Control depth: ")
                .append(ConcolicControlStack.getControlDepth())
                .append("\n");
        return sb.toString();
    }
}
