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
 * Galette-based concolic execution listener.
 *
 * This class replaces the original Phosphor-based TaintListener with
 * Galette-compatible APIs while maintaining the same concolic execution
 * functionality for constraint generation and symbolic execution.
 * @purpose Event listener for taint propagation event
 * @feature Notified when symbolic values are created
 * @feature Tracks constraint collection
 *
 * @author [Anne Koziolek](https://github.com/AnneKoziolek)
 */
public class GaletteTaintListener implements ConcolicTaintListener {

    /**
     * Map arrays to their symbolic variable representations.
     */
    public static IdentityHashMap<Object, LinkedList<ArrayVariable>> arrayNames = new IdentityHashMap<>();

    /**
     * Configuration constants for array handling.
     */
    public static int IGNORE_CONCRETE_ARRAY_INITIAL_CONTENTS = 1000;

    public static int IGNORE_LARGE_ARRAY_SIZE = 20000;
    public static int IGNORE_LARGE_ARRAY_INDEX = 500;

    /**
     * Queue of symbolized arrays for processing.
     */
    public static ConcurrentLinkedQueue<Tag[]> symbolizedArrays = new ConcurrentLinkedQueue<>();

    /**
     * Get or initialize the symbolic representation of an array.
     *
     * @param arr The array object
     * @return List of ArrayVariables representing the array
     */
    private LinkedList<ArrayVariable> getOrInitArray(Object arr) {
        LinkedList<ArrayVariable> ret = arrayNames.get(arr);
        if (ret != null) {
            return ret;
        }

        Class<?> componentType = arr.getClass().getComponentType();
        Class<?> t = componentType.isPrimitive() ? componentType : Object.class;
        LinkedList<ArrayVariable> ll = new LinkedList<>();

        // Create array variable based on type
        int length = Array.getLength(arr);
        String baseName = "array_" + System.identityHashCode(arr);

        for (int i = 0; i < Math.min(length, IGNORE_LARGE_ARRAY_SIZE); i++) {
            ArrayVariable av = new ArrayVariable(baseName + "[" + i + "]", t);
            ll.add(av);
        }

        arrayNames.put(arr, ll);
        return ll;
    }

    @Override
    public void onBranch(Tag condition, boolean taken) {
        if (condition == null || condition.isEmpty()) {
            return;
        }

        // Convert tag to Green expression
        Expression condExpr = GaletteGreenBridge.tagToGreenExpression(condition, taken);
        if (condExpr != null) {
            // Add branch condition to path constraints
            Expression constraint = taken ? condExpr : GaletteGreenBridge.createUnaryOp(Operator.NOT, condExpr);

            onPathConstraint(constraint);
        }

        // Update control flow stack
        ConcolicControlStack.pushControl(condition);
    }

    @Override
    public void onArithmetic(Tag operand1, Tag operand2, Tag result) {
        if ((operand1 == null || operand1.isEmpty()) && (operand2 == null || operand2.isEmpty())) {
            return;
        }

        // TODO: Implement arithmetic constraint generation
        // This would involve creating expressions for operand1 op operand2 = result
    }

    @Override
    public void onComparison(Tag left, Tag right, int opcode, boolean result) {
        if ((left == null || left.isEmpty()) && (right == null || right.isEmpty())) {
            return;
        }

        // Convert tags to Green expressions
        Expression leftExpr = GaletteGreenBridge.tagToGreenExpression(left, null);
        Expression rightExpr = GaletteGreenBridge.tagToGreenExpression(right, null);

        if (leftExpr != null || rightExpr != null) {
            Operator op = bytecodeToGreenOperator(opcode);
            if (op != null) {
                Expression compExpr = GaletteGreenBridge.createBinaryOp(
                        leftExpr != null ? leftExpr : new IntConstant(0),
                        op,
                        rightExpr != null ? rightExpr : new IntConstant(0));

                // Add comparison result as constraint
                Expression constraint = result ? compExpr : GaletteGreenBridge.createUnaryOp(Operator.NOT, compExpr);

                onPathConstraint(constraint);
            }
        }
    }

    @Override
    public void onPathConstraint(Object constraint) {
        // Add constraint to the current path condition
        if (constraint instanceof Expression) {
            PathUtils.getCurPC().addConstraint((Expression) constraint);
        }
    }

    @Override
    public void onArrayAccess(Tag arrayTag, Tag indexTag, Tag valueTag, boolean isWrite) {
        if (arrayTag == null || arrayTag.isEmpty()) {
            return;
        }

        // TODO: Implement array access constraint generation
        // This would involve creating constraints for array[index] operations
    }

    @Override
    public void onMethodCall(String methodName, Tag[] parameterTags, Tag returnTag) {
        // TODO: Implement method call tracking
        // This could be useful for library method modeling
    }

    /**
     * Convert bytecode comparison opcodes to Green operators.
     *
     * @param opcode The bytecode opcode
     * @return Corresponding Green operator, or null if not supported
     */
    private Operator bytecodeToGreenOperator(int opcode) {
        switch (opcode) {
            case 153: // IFEQ
            case 198: // IFNULL
                return Operator.EQ;
            case 154: // IFNE
            case 199: // IFNONNULL
                return Operator.NE;
            case 155: // IFLT
                return Operator.LT;
            case 156: // IFGE
                return Operator.GE;
            case 157: // IFGT
                return Operator.GT;
            case 158: // IFLE
                return Operator.LE;
            case 159: // IF_ICMPEQ
            case 165: // IF_ACMPEQ
                return Operator.EQ;
            case 160: // IF_ICMPNE
            case 166: // IF_ACMPNE
                return Operator.NE;
            case 161: // IF_ICMPLT
                return Operator.LT;
            case 162: // IF_ICMPGE
                return Operator.GE;
            case 163: // IF_ICMPGT
                return Operator.GT;
            case 164: // IF_ICMPLE
                return Operator.LE;
            default:
                return null;
        }
    }

    /**
     * Static instance for global access.
     */
    private static final GaletteTaintListener INSTANCE = new GaletteTaintListener();

    /**
     * Get the singleton instance.
     *
     * @return The global taint listener instance
     */
    public static GaletteTaintListener getInstance() {
        return INSTANCE;
    }
}
