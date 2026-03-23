package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.concurrent.atomic.AtomicInteger;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Knarr-style symbolic expression propagator for arithmetic operations.
 *
 * <p>When Galette's TagPropagator encounters an arithmetic instruction on symbolic values,
 * it calls methods in this class instead of plain Tag.union(). Each method:
 * <ol>
 *   <li>Creates a fresh result tag (unique identity for the computed value)</li>
 *   <li>Looks up operand expressions from GaletteSymbolicator's tag→expression map</li>
 *   <li>Builds a compound Green expression (e.g., ADD(var(sym), const(5)))</li>
 *   <li>Associates the fresh tag with the compound expression</li>
 *   <li>Registers the concrete result value for value-based lookup at branches</li>
 * </ol>
 *
 * <p>This enables branch constraint recording to recover full arithmetic predicates
 * like {@code GT(ADD(var(a), var(b)), const(10))} instead of just dependency tags.
 */
public class SymbolicExpressionPropagator {

    /** Internal name for bytecode instrumentation references. */
    public static final String INTERNAL_NAME =
            "edu/neu/ccs/prl/galette/concolic/knarr/runtime/SymbolicExpressionPropagator";

    /** Counter for generating unique fresh tag labels. */
    private static final AtomicInteger exprCounter = new AtomicInteger(0);

    /**
     * Propagate a binary integer operation symbolically.
     *
     * <p>Called from instrumented bytecode for IADD, ISUB, IMUL, IDIV, IREM,
     * IAND, IOR, IXOR, ISHL, ISHR, IUSHR.
     *
     * @param v1     concrete value of left operand
     * @param v2     concrete value of right operand
     * @param t1     shadow tag of left operand (may be null/empty)
     * @param t2     shadow tag of right operand (may be null/empty)
     * @param opcode the JVM arithmetic opcode
     * @return result tag: fresh tag with compound expression if symbolic, else Tag.union result
     */
    public static Tag binaryIntOp(int v1, int v2, Tag t1, Tag t2, int opcode) {
        boolean leftSymbolic = isSymbolic(t1);
        boolean rightSymbolic = isSymbolic(t2);

        if (!leftSymbolic && !rightSymbolic) {
            // Neither operand is symbolic — return union (which will be empty)
            return Tag.union(t1, t2);
        }

        // At least one operand is symbolic — build compound expression
        Expression leftExpr = lookupOrConstant(t1, v1);
        Expression rightExpr = lookupOrConstant(t2, v2);

        Operator op = opcodeToOperator(opcode);
        if (op == null) {
            System.err.println("[SymbolicExpressionPropagator:binaryIntOp] WARNING: Unsupported opcode " + opcode
                    + " — falling back to Tag.union (dependency-only). Symbolic expression LOST.");
            return Tag.union(t1, t2);
        }

        Expression resultExpr = new BinaryOperation(op, leftExpr, rightExpr);

        // Create fresh tag and associate with expression
        Tag resultTag = freshTag();
        GaletteSymbolicator.associateTagWithExpression(resultTag, resultExpr);

        // Register concrete result for value-based lookup (legacy/fallback path)
        int concreteResult = computeIntResult(v1, v2, opcode);
        registerResultValue(concreteResult, resultTag);

        if (GaletteSymbolicator.DEBUG) {
            System.out.println("[SymbolicExpressionPropagator:binaryIntOp] " + leftExpr + " " + op + " " + rightExpr
                    + " → " + resultExpr);
        }

        return resultTag;
    }

    /**
     * Propagate a unary integer operation symbolically.
     *
     * <p>Called from instrumented bytecode for INEG.
     *
     * @param v      concrete value of operand
     * @param t      shadow tag of operand (may be null/empty)
     * @param opcode the JVM unary opcode
     * @return result tag: fresh tag with NEG expression if symbolic, else original tag
     */
    public static Tag unaryIntOp(int v, Tag t, int opcode) {
        if (!isSymbolic(t)) {
            return t; // Not symbolic, return as-is
        }

        Expression operandExpr = lookupOrConstant(t, v);

        Operator op = unaryOpcodeToOperator(opcode);
        if (op == null) {
            System.err.println("[SymbolicExpressionPropagator:unaryIntOp] WARNING: Unsupported unary opcode " + opcode
                    + " — keeping original tag. Symbolic expression NOT propagated.");
            return t;
        }

        Expression resultExpr = new UnaryOperation(op, operandExpr);

        Tag resultTag = freshTag();
        GaletteSymbolicator.associateTagWithExpression(resultTag, resultExpr);

        int concreteResult = computeUnaryIntResult(v, opcode);
        registerResultValue(concreteResult, resultTag);

        if (GaletteSymbolicator.DEBUG) {
            System.out.println(
                    "[SymbolicExpressionPropagator:unaryIntOp] " + op + "(" + operandExpr + ") → " + resultExpr);
        }

        return resultTag;
    }

    /**
     * Reset state for a new exploration iteration.
     */
    public static void reset() {
        exprCounter.set(0);
    }

    // ===== Internal helpers =====

    private static boolean isSymbolic(Tag tag) {
        if (tag == null || tag.isEmpty()) {
            return false;
        }
        // Check if the tag has an associated expression
        return GaletteSymbolicator.getExpressionForTag(tag) != null;
    }

    private static Expression lookupOrConstant(Tag tag, int value) {
        if (tag != null && !tag.isEmpty()) {
            Expression expr = GaletteSymbolicator.getExpressionForTag(tag);
            if (expr != null) {
                return expr;
            }
            System.err.println("[SymbolicExpressionPropagator:lookupOrConstant] WARNING: Tag " + tag
                    + " exists but has no expression. Falling back to IntConstant(" + value + "). "
                    + "Symbolic expression LOST for this operand.");
        }
        return new IntConstant(value);
    }

    private static Tag freshTag() {
        return Tag.of("_sexpr_" + exprCounter.getAndIncrement());
    }

    private static void registerResultValue(int result, Tag tag) {
        // Register in GaletteSymbolicator's valueToTag for legacy value-based lookup
        GaletteSymbolicator.registerValueTag(Integer.valueOf(result), tag);
    }

    /**
     * Map JVM binary arithmetic opcode to Green operator.
     */
    private static Operator opcodeToOperator(int opcode) {
        switch (opcode) {
            case 96:
                return Operator.ADD; // IADD
            case 100:
                return Operator.SUB; // ISUB
            case 104:
                return Operator.MUL; // IMUL
            case 108:
                return Operator.DIV; // IDIV
            case 112:
                return Operator.MOD; // IREM
            case 126:
                return Operator.BIT_AND; // IAND
            case 128:
                return Operator.BIT_OR; // IOR
            case 130:
                return Operator.BIT_XOR; // IXOR
            case 120:
                return Operator.SHIFTL; // ISHL
            case 122:
                return Operator.SHIFTR; // ISHR
            case 124:
                return Operator.SHIFTUR; // IUSHR
            default:
                return null;
        }
    }

    /**
     * Map JVM unary opcode to Green operator.
     */
    private static Operator unaryOpcodeToOperator(int opcode) {
        switch (opcode) {
            case 116:
                return Operator.NEG; // INEG
            default:
                return null;
        }
    }

    /**
     * Compute the concrete result of a binary int operation.
     */
    private static int computeIntResult(int v1, int v2, int opcode) {
        switch (opcode) {
            case 96:
                return v1 + v2; // IADD
            case 100:
                return v1 - v2; // ISUB
            case 104:
                return v1 * v2; // IMUL
            case 108:
                return v2 != 0 ? v1 / v2 : 0; // IDIV
            case 112:
                return v2 != 0 ? v1 % v2 : 0; // IREM
            case 126:
                return v1 & v2; // IAND
            case 128:
                return v1 | v2; // IOR
            case 130:
                return v1 ^ v2; // IXOR
            case 120:
                return v1 << v2; // ISHL
            case 122:
                return v1 >> v2; // ISHR
            case 124:
                return v1 >>> v2; // IUSHR
            default:
                return v1;
        }
    }

    /**
     * Compute the concrete result of a unary int operation.
     */
    private static int computeUnaryIntResult(int v, int opcode) {
        switch (opcode) {
            case 116:
                return -v; // INEG
            default:
                return v;
        }
    }
}
