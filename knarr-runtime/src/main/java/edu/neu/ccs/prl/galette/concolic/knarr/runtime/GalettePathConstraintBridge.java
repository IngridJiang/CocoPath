package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import za.ac.sun.cs.green.expr.*;

/**
 * Bridge between Galette's automatic comparison interception and knarr-runtime.
 *
 * Uses reflection to access Galette's internal PathUtils class to avoid direct
 * dependencies between modules. Converts raw Constraint objects (value1, value2,
 * operation, result) into Green Expression objects for the constraint solver.
 *
 * Supports both integer and double symbolic values via registries populated by
 * GaletteSymbolicator when creating symbolic values.
 */
public class GalettePathConstraintBridge {

    private static final boolean DEBUG = Boolean.getBoolean("galette.concolic.interception.debug");

    private static Class<?> galettePathUtilsClass;
    private static Method flushMethod;

    /**
     * Registry mapping concrete integer values to their symbolic variable names.
     * Populated by GaletteSymbolicator when creating symbolic ints.
     */
    private static final ConcurrentHashMap<Integer, String> symbolicIntRegistry = new ConcurrentHashMap<>();

    /**
     * Registry mapping concrete double values to their symbolic variable names.
     */
    private static final ConcurrentHashMap<Double, String> symbolicDoubleRegistry = new ConcurrentHashMap<>();

    static {
        try {
            galettePathUtilsClass = Class.forName("edu.neu.ccs.prl.galette.PathConstraintAPI");
            flushMethod = galettePathUtilsClass.getMethod("flushConstraints");
            if (DEBUG) {
                System.out.println("[GalettePathConstraintBridge] Initialized successfully via PathConstraintAPI");
            }
        } catch (Exception e) {
            System.err.println("[GalettePathConstraintBridge] WARNING: PathConstraintAPI not available: "
                    + e.getMessage()
                    + ". Native bytecode interception constraints will NOT be collected.");
            galettePathUtilsClass = null;
        }
    }

    public static boolean isAvailable() {
        return galettePathUtilsClass != null;
    }

    /**
     * Register a concrete integer value as symbolic with the given variable name.
     */
    public static void registerSymbolicInt(String variableName, int concreteValue) {
        symbolicIntRegistry.put(concreteValue, variableName);
        if (DEBUG) {
            System.out.println(
                    "[GalettePathConstraintBridge] Registered symbolic int: " + variableName + " = " + concreteValue);
        }
    }

    /**
     * Register a concrete double value as symbolic with the given variable name.
     */
    public static void registerSymbolicDouble(String variableName, double concreteValue) {
        symbolicDoubleRegistry.put(concreteValue, variableName);
    }

    /**
     * Clear all symbolic registries (called at start of each exploration iteration).
     */
    public static void clearSymbolicRegistries() {
        symbolicIntRegistry.clear();
        symbolicDoubleRegistry.clear();
    }

    /**
     * Flush and convert constraints from Galette's native interception into Green Expressions.
     * Returns only constraints that involve at least one symbolic variable.
     */
    public static List<Expression> flushGaletteConstraints() {
        if (!isAvailable()) return new ArrayList<>();

        try {
            @SuppressWarnings("unchecked")
            List<Object> rawConstraints = (List<Object>) flushMethod.invoke(null);
            if (DEBUG) {
                System.out.println(
                        "[GalettePathConstraintBridge] Flushed " + rawConstraints.size() + " raw constraints");
            }
            return convertToGreenExpressions(rawConstraints);
        } catch (Exception e) {
            System.err.println(
                    "[GalettePathConstraintBridge] ERROR: Failed to flush native constraints: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Reset (discard) all collected native constraints without converting them.
     */
    public static void resetGaletteConstraints() {
        if (!isAvailable()) return;
        try {
            flushMethod.invoke(null); // flush = get + clear; discard result
        } catch (Exception e) {
            // ignore
        }
    }

    private static List<Expression> convertToGreenExpressions(List<Object> rawConstraints) {
        List<Expression> expressions = new ArrayList<>();
        int skippedConcrete = 0;

        for (Object constraint : rawConstraints) {
            try {
                Expression expr = convertSingleConstraint(constraint);
                if (expr != null && containsSymbolicVariable(expr)) {
                    expressions.add(expr);
                } else {
                    skippedConcrete++;
                }
            } catch (Exception e) {
                System.err.println(
                        "[GalettePathConstraintBridge] WARNING: Skipping invalid constraint: " + e.getMessage());
            }
        }

        if (DEBUG && skippedConcrete > 0) {
            System.out.println(
                    "[GalettePathConstraintBridge] Filtered out " + skippedConcrete + " concrete-only constraints");
        }

        return expressions;
    }

    private static boolean containsSymbolicVariable(Expression expr) {
        if (expr instanceof IntVariable || expr instanceof RealVariable) {
            return true;
        }
        if (expr instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) expr;
            return containsSymbolicVariable(binOp.left) || containsSymbolicVariable(binOp.right);
        }
        if (expr instanceof UnaryOperation) {
            return containsSymbolicVariable(((UnaryOperation) expr).operand);
        }
        return false;
    }

    private static Expression convertSingleConstraint(Object constraint) throws Exception {
        Class<?> constraintClass = constraint.getClass();
        Object value1 = constraintClass.getField("value1").get(constraint);
        Object value2 = constraintClass.getField("value2").get(constraint);
        String operation = (String) constraintClass.getField("operation").get(constraint);
        int result = (Integer) constraintClass.getField("result").get(constraint);

        // Check both operands for symbolic status
        Expression leftExpr = toExpr(value1);
        Expression rightExpr = toExpr(value2);

        if (leftExpr == null || rightExpr == null) {
            System.err.println("[GalettePathConstraintBridge] WARNING: Could not convert operands to expressions: "
                    + "value1=" + value1 + " (type="
                    + (value1 != null ? value1.getClass().getSimpleName() : "null")
                    + "), value2=" + value2 + " (type="
                    + (value2 != null ? value2.getClass().getSimpleName() : "null")
                    + ")");
            return null;
        }

        return createGreenOperation(leftExpr, rightExpr, operation, result);
    }

    private static boolean isSymbolicValue(Object value) {
        if (value instanceof Integer) {
            // Check direct registry first (original symbolic inputs)
            if (symbolicIntRegistry.containsKey((Integer) value)) {
                return true;
            }
            // Also check GaletteSymbolicator's valueToTag for compound expressions
            // created by SymbolicExpressionPropagator (e.g., x+5 → tag with ADD expression)
            Tag tag = GaletteSymbolicator.getTagForValue(value);
            if (tag != null) {
                Expression expr = GaletteSymbolicator.getExpressionForTag(tag);
                if (expr != null) {
                    return true;
                }
            }
            return false;
        }
        if (value instanceof Double || value instanceof Float) {
            double d = value instanceof Float ? (Float) value : (Double) value;
            return symbolicDoubleRegistry.containsKey(d);
        }
        return false;
    }

    private static Expression toSymbolicExpr(Object value) {
        if (value instanceof Integer) {
            // First try tag-based expression lookup (supports compound expressions from
            // SymbolicExpressionPropagator, e.g. ADD(var(x), const(5)))
            Tag tag = GaletteSymbolicator.getTagForValue(value);
            if (tag != null) {
                Expression expr = GaletteSymbolicator.getExpressionForTag(tag);
                if (expr != null) {
                    if (DEBUG) {
                        System.out.println(
                                "[GalettePathConstraintBridge] Tag-based expression for " + value + ": " + expr);
                    }
                    return expr;
                }
            }
            // Fall back to symbolic int registry (direct symbolic input variables)
            String name = symbolicIntRegistry.get((Integer) value);
            if (name != null) {
                if (DEBUG) {
                    System.out.println(
                            "[GalettePathConstraintBridge] Registry-based symbolic int: " + name + " = " + value);
                }
                return new IntVariable(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }
            System.err.println("[GalettePathConstraintBridge] WARNING: Value " + value
                    + " was identified as symbolic but no expression found in tag store or registry");
        }
        if (value instanceof Double || value instanceof Float) {
            double d = value instanceof Float ? (Float) value : (Double) value;
            String name = symbolicDoubleRegistry.get(d);
            if (name != null) {
                return new RealVariable(name, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            }
        }
        return toConstantExpr(value);
    }

    private static Expression toConstantExpr(Object value) {
        if (value instanceof Integer) {
            return new IntConstant((Integer) value);
        }
        if (value instanceof Long) {
            return new IntConstant(((Long) value).intValue());
        }
        if (value instanceof Double) {
            return new RealConstant((Double) value);
        }
        if (value instanceof Float) {
            return new RealConstant((Float) value);
        }
        return null;
    }

    /**
     * Convert a value to an expression, checking the symbolic registry.
     */
    private static Expression toExpr(Object value) {
        if (isSymbolicValue(value)) {
            return toSymbolicExpr(value);
        }
        return toConstantExpr(value);
    }

    private static Expression createGreenOperation(Expression left, Expression right, String operation, int result) {
        switch (operation) {
            case "EQ":
                return new BinaryOperation(result == 1 ? Operation.Operator.EQ : Operation.Operator.NE, left, right);
            case "NE":
                return new BinaryOperation(result == 1 ? Operation.Operator.NE : Operation.Operator.EQ, left, right);
            case "LT":
                return new BinaryOperation(result == 1 ? Operation.Operator.LT : Operation.Operator.GE, left, right);
            case "GE":
                return new BinaryOperation(result == 1 ? Operation.Operator.GE : Operation.Operator.LT, left, right);
            case "GT":
                return new BinaryOperation(result == 1 ? Operation.Operator.GT : Operation.Operator.LE, left, right);
            case "LE":
                return new BinaryOperation(result == 1 ? Operation.Operator.LE : Operation.Operator.GT, left, right);
            case "LCMP":
            case "FCMPL":
            case "FCMPG":
            case "DCMPL":
            case "DCMPG":
                if (result < 0) {
                    return new BinaryOperation(Operation.Operator.LT, left, right);
                } else if (result > 0) {
                    return new BinaryOperation(Operation.Operator.GT, left, right);
                } else {
                    return new BinaryOperation(Operation.Operator.EQ, left, right);
                }
            default:
                return null;
        }
    }
}
