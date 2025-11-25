package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.concolic.knarr.green.GaletteGreenBridge;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Galette-compatible array symbolic execution tracker.
 *
 * Migrated from Knarr's TaintListener.java to use Galette Tags instead of Phosphor Taints.
 * Handles symbolic execution for array operations including reads, writes, and indexing.
 *
 * Key capabilities:
 * - Symbolic array indexing: array[symbolic_index]
 * - Array constraint generation for solver
 * - Bounds checking with symbolic indices
 * - Multi-dimensional array support
 * @purpose Specialized tracking for symbolic arrays
 * @feature Array load/store with symbolic indices
 * @feature Array length constraints
 * @feature Multi-dimensional array support
 *
 */
public class ArraySymbolicTracker {

    // Array variable tracking for constraint generation
    public static IdentityHashMap<Object, LinkedList<ArrayVariable>> arrayNames = new IdentityHashMap<>();

    // Performance thresholds to avoid constraint explosion
    public static int IGNORE_CONCRETE_ARRAY_INITIAL_CONTENTS = 1000;
    public static int IGNORE_LARGE_ARRAY_SIZE = 20000;
    public static int IGNORE_LARGE_ARRAY_INDEX = 500;

    // Track arrays that have been symbolized for constraint collection
    public static ConcurrentLinkedQueue<Tag[]> symbolizedArrays = new ConcurrentLinkedQueue<>();

    /**
     * Get or initialize array variable tracking for constraint generation.
     */
    private LinkedList<ArrayVariable> getOrInitArray(Object arr) {
        LinkedList<ArrayVariable> ret = arrayNames.get(arr);
        if (ret != null) return ret;

        Class<?> t =
                arr.getClass().getComponentType().isPrimitive() ? arr.getClass().getComponentType() : Object.class;
        LinkedList<ArrayVariable> ll = new LinkedList<>();
        ArrayVariable var = new ArrayVariable("const_array_" + arrayNames.size(), t);
        ll.add(var);
        arrayNames.put(arr, ll);
        ret = ll;

        // Initialize concrete array contents as constraints (for small arrays)
        if (Array.getLength(arr) < IGNORE_CONCRETE_ARRAY_INITIAL_CONTENTS) {
            ArrayVariable arrVar = new ArrayVariable(var.getName() + "_" + ret.size(), var.getType());

            for (int i = 0; i < Array.getLength(arr); i++) {
                Operation select = new BinaryOperation(Operator.SELECT, arrVar, new BVConstant(i, 32));
                Constant val = createConstantFromArrayElement(arr, i);
                PathUtils.getCurPC()._addDet(Operator.EQ, select, val);
            }
        }

        return ret;
    }

    /**
     * Create Green expression constant from array element based on type.
     */
    private Constant createConstantFromArrayElement(Object arr, int index) {
        Class<?> componentType = arr.getClass().getComponentType();

        if (componentType.isPrimitive()) {
            switch (componentType.getName()) {
                case "boolean":
                    return new BoolConstant(((boolean[]) arr)[index]);
                case "byte":
                    return new BVConstant(((byte[]) arr)[index], 32);
                case "char":
                    return new BVConstant(((char[]) arr)[index], 32);
                case "short":
                    return new BVConstant(((short[]) arr)[index], 32);
                case "int":
                    return new BVConstant(((int[]) arr)[index], 32);
                case "long":
                    return new BVConstant(((long[]) arr)[index], 64);
                case "float":
                    return new RealConstant(((float[]) arr)[index]);
                case "double":
                    return new RealConstant(((double[]) arr)[index]);
                default:
                    throw new Error("Unsupported primitive array type: " + componentType.getName());
            }
        } else {
            // Handle object arrays
            Object element = Array.get(arr, index);
            if (element == null) {
                return new StringConstant("null");
            } else if (element instanceof String) {
                return new StringConstant((String) element);
            } else {
                // For other object types, use toString representation
                return new StringConstant(element.toString());
            }
        }
    }

    /**
     * Get current array variable for constraint generation.
     */
    private Expression getArrayVar(Object arr) {
        synchronized (arrayNames) {
            LinkedList<ArrayVariable> ret = getOrInitArray(arr);
            return new ArrayVariable(
                    ret.getLast().getName() + "_" + ret.size(), ret.getLast().getType());
        }
    }

    /**
     * Set array variable and generate store constraint.
     */
    private Expression setArrayVar(Object arr, Expression idx, Expression val) {
        synchronized (arrayNames) {
            LinkedList<ArrayVariable> ret = getOrInitArray(arr);

            ArrayVariable var = ret.getLast();
            ArrayVariable oldVar = new ArrayVariable(var.getName() + "_" + ret.size(), var.getType());
            ArrayVariable newVar = new ArrayVariable(var.getName() + "_" + (ret.size() + 1), var.getType());
            ret.addLast(var);

            // Generate store constraint: newArray = store(oldArray, index, value)
            Operation store = new NaryOperation(Operator.STORE, oldVar, idx, val);
            PathUtils.getCurPC()._addDet(Operator.EQ, store, newVar);
            return newVar;
        }
    }

    /**
     * Check if array read should be ignored for performance.
     */
    private boolean shouldIgnoreRead(Object arr, Tag indexTag, int index, Tag[] arrayTags) {
        boolean taintedArray = (arrayTags != null && arrayTags[index] != null && !arrayTags[index].isEmpty());
        boolean taintedIndex = (indexTag != null && !indexTag.isEmpty());

        if (!taintedArray && !taintedIndex) return true;

        return (Array.getLength(arr) > IGNORE_LARGE_ARRAY_SIZE && index > IGNORE_LARGE_ARRAY_INDEX);
    }

    /**
     * Check if array write should be ignored for performance.
     */
    private boolean shouldIgnoreWrite(Object arr, Tag indexTag, int index, Tag valueTag, Tag[] arrayTags) {
        boolean taintedArray = (arrayTags != null && arrayTags[index] != null && !arrayTags[index].isEmpty());
        boolean taintedIndex = (indexTag != null && !indexTag.isEmpty());
        boolean taintedVal = (valueTag != null && !valueTag.isEmpty());

        if (!taintedArray && !taintedIndex && !taintedVal) {
            return true; // Everything concrete
        }

        return (Array.getLength(arr) > IGNORE_LARGE_ARRAY_SIZE && index > IGNORE_LARGE_ARRAY_INDEX);
    }

    /**
     * Handle symbolic array read operation.
     *
     * @param arr The array being read from
     * @param indexTag Tag for the index (may be symbolic)
     * @param index Concrete index value
     * @param arrayTags Array of tags for array elements
     * @param concreteValue The concrete value at this index
     * @return Tag representing the symbolic value read, or null if concrete
     */
    public Tag handleArrayRead(Object arr, Tag indexTag, int index, Tag[] arrayTags, Object concreteValue) {
        if (shouldIgnoreRead(arr, indexTag, index, arrayTags)) {
            return null;
        }

        boolean taintedArray = (arrayTags != null && arrayTags[index] != null && !arrayTags[index].isEmpty());
        boolean taintedIndex = (indexTag != null && !indexTag.isEmpty());

        if (taintedIndex && !taintedArray) {
            // Symbolic read of concrete array position

            // Initialize array tags if needed
            if (arrayTags == null) {
                arrayTags = new Tag[Array.getLength(arr)];
            }

            // Create symbolic expression for array[symbolic_index]
            Expression var = getArrayVar(arr);
            Expression indexExpr = GaletteGreenBridge.tagToGreenExpression(indexTag);
            Operation select = new BinaryOperation(Operator.SELECT, var, indexExpr);

            // Create tag for the symbolic result
            Tag resultTag = GaletteGreenBridge.greenExpressionToTag(
                    select, "array_read_" + System.identityHashCode(arr) + "_" + index);
            arrayTags[index] = resultTag;

            // Add constraint: concrete_value == array[symbolic_index]
            Constant concreteConst = createConstantFromValue(concreteValue);
            PathUtils.getCurPC()._addDet(Operator.EQ, concreteConst, select);

            // Add bounds constraints for symbolic index
            addBoundsConstraints(indexTag, Array.getLength(arr));

            symbolizedArrays.add(arrayTags);
            return resultTag;

        } else if (taintedArray && !taintedIndex) {
            // Concrete read of symbolic array position
            return arrayTags[index];

        } else if (taintedArray && taintedIndex) {
            // Symbolic read of symbolic array position
            Expression var = getArrayVar(arr);
            Expression indexExpr = GaletteGreenBridge.tagToGreenExpression(indexTag);
            Operation select = new BinaryOperation(Operator.SELECT, var, indexExpr);

            // Connect array element tag to symbolic read
            Expression arrayElementExpr = GaletteGreenBridge.tagToGreenExpression(arrayTags[index]);
            PathUtils.getCurPC()._addDet(Operator.EQ, arrayElementExpr, select);

            // Add bounds constraints
            addBoundsConstraints(indexTag, Array.getLength(arr));

            return arrayTags[index];

        } else {
            // Concrete read of concrete array position - no symbolic tracking needed
            return null;
        }
    }

    /**
     * Handle symbolic array write operation.
     *
     * @param arr The array being written to
     * @param indexTag Tag for the index (may be symbolic)
     * @param index Concrete index value
     * @param valueTag Tag for the value being written (may be symbolic)
     * @param arrayTags Array of tags for array elements
     * @param concreteValue The concrete value being written
     * @return Tag for the array element after write, or null if concrete
     */
    public Tag handleArrayWrite(
            Object arr, Tag indexTag, int index, Tag valueTag, Tag[] arrayTags, Object concreteValue) {
        if (shouldIgnoreWrite(arr, indexTag, index, valueTag, arrayTags)) {
            return null;
        }

        boolean taintedArray = (arrayTags != null && arrayTags[index] != null && !arrayTags[index].isEmpty());
        boolean taintedIndex = (indexTag != null && !indexTag.isEmpty());
        boolean taintedVal = (valueTag != null && !valueTag.isEmpty());

        if (!taintedArray && !taintedIndex && !taintedVal) {
            // Everything concrete - no symbolic tracking
            return null;

        } else if (taintedArray && !taintedIndex && !taintedVal) {
            // Symbolic position overwritten by concrete value
            Expression newArray = setArrayVar(arr, new BVConstant(index, 32), createConstantFromValue(concreteValue));
            return null; // Now concrete

        } else if (!taintedArray && taintedIndex && !taintedVal) {
            // Symbolic index on concrete values
            Expression indexExpr = GaletteGreenBridge.tagToGreenExpression(indexTag);
            Expression newArray = setArrayVar(arr, indexExpr, createConstantFromValue(concreteValue));

            // Add bounds constraints
            addBoundsConstraints(indexTag, Array.getLength(arr));
            return null; // Now concrete

        } else if (!taintedArray && !taintedIndex && taintedVal) {
            // Concrete index on concrete array with symbolic new value
            return valueTag; // Array position becomes symbolic

        } else if (!taintedArray && taintedIndex && taintedVal) {
            // Concrete array with symbolic index and symbolic value
            addBoundsConstraints(indexTag, Array.getLength(arr));
            return valueTag;

        } else if (taintedArray && !taintedIndex && taintedVal) {
            // Symbolic array position and symbolic value with concrete index
            Expression valueExpr = GaletteGreenBridge.tagToGreenExpression(valueTag);
            Expression newArray = setArrayVar(arr, new BVConstant(index, 32), valueExpr);
            return valueTag;

        } else if (taintedArray && taintedIndex && !taintedVal) {
            // Symbolic array position being overwritten by concrete value with symbolic index
            Expression indexExpr = GaletteGreenBridge.tagToGreenExpression(indexTag);
            Expression newArray = setArrayVar(arr, indexExpr, createConstantFromValue(concreteValue));

            addBoundsConstraints(indexTag, Array.getLength(arr));
            return null; // Now concrete

        } else if (taintedArray && taintedIndex && taintedVal) {
            // Everything symbolic
            Expression indexExpr = GaletteGreenBridge.tagToGreenExpression(indexTag);
            Expression valueExpr = GaletteGreenBridge.tagToGreenExpression(valueTag);
            Expression newArray = setArrayVar(arr, indexExpr, valueExpr);

            addBoundsConstraints(indexTag, Array.getLength(arr));
            return valueTag;
        }

        return null; // Should not reach here
    }

    /**
     * Add bounds constraints for symbolic array index.
     */
    private void addBoundsConstraints(Tag indexTag, int arrayLength) {
        if (indexTag == null || indexTag.isEmpty()) {
            return;
        }

        Expression indexExpr = GaletteGreenBridge.tagToGreenExpression(indexTag);

        // index >= 0
        PathUtils.getCurPC()._addDet(Operator.GE, indexExpr, new BVConstant(0, 32));

        // index < array.length
        PathUtils.getCurPC()._addDet(Operator.LT, indexExpr, new BVConstant(arrayLength, 32));
    }

    /**
     * Create Green expression constant from runtime value.
     */
    private Constant createConstantFromValue(Object value) {
        if (value == null) {
            return new StringConstant("null");
        } else if (value instanceof Boolean) {
            return new BoolConstant((Boolean) value);
        } else if (value instanceof Byte) {
            return new BVConstant((Byte) value, 32);
        } else if (value instanceof Character) {
            return new BVConstant((Character) value, 32);
        } else if (value instanceof Short) {
            return new BVConstant((Short) value, 32);
        } else if (value instanceof Integer) {
            return new BVConstant((Integer) value, 32);
        } else if (value instanceof Long) {
            return new BVConstant((Long) value, 64);
        } else if (value instanceof Float) {
            return new RealConstant((Float) value);
        } else if (value instanceof Double) {
            return new RealConstant((Double) value);
        } else if (value instanceof String) {
            return new StringConstant((String) value);
        } else {
            // For other object types, use toString representation
            return new StringConstant(value.toString());
        }
    }

    /**
     * Reset array tracking state.
     */
    public static void reset() {
        arrayNames.clear();
        symbolizedArrays.clear();
    }

    /**
     * Get statistics about array symbolic execution.
     */
    public static String getStatistics() {
        return String.format(
                "Array Symbolic Execution Statistics:\n" + "  - Arrays tracked: %d\n" + "  - Symbolized arrays: %d",
                arrayNames.size(), symbolizedArrays.size());
    }
}
