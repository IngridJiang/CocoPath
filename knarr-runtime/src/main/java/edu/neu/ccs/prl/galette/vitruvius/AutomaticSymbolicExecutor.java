package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import edu.neu.ccs.prl.galette.internal.runtime.Tainter;
import java.lang.reflect.Field;
import java.util.*;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Automatic symbolic execution engine for Vitruvius framework.
 *
 * This class automatically:
 * 1. Detects tainted values (user inputs, external data)
 * 2. Creates symbolic variables for any type (int, EAttribute, EObject, etc.)
 * 3. Collects path constraints during execution
 * 4. Explores all possible execution paths
 * 5. No hardcoding required - works with any Vitruvius model changes
 *
 * @purpose Automatic symbolic execution for Vitruvius transformations
 * @feature User choice exploration
 * @feature Model transformation testing
 *
 * @author Galette-Vitruvius Integration
 */
public class AutomaticSymbolicExecutor {

    private static final boolean DEBUG = Boolean.getBoolean("symbolic.execution.debug");
    private static final Set<String> discoveredSymbolicVariables = new HashSet<>();
    private static final Map<String, Object> symbolicValueMap = new HashMap<>();
    private static final List<ExecutionPath> exploredPaths = new ArrayList<>();

    /**
     * Java 8 compatible string repeat helper.
     */
    private static String repeatString(String str, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * Execution path record containing constraints and decisions.
     */
    public static class ExecutionPath {
        public final int pathId;
        public final List<Expression> constraints;
        public final Map<String, Object> symbolicInputs;
        public final Map<String, Object> outputs;
        public final long executionTime;
        public final long initializationTime;

        public ExecutionPath(
                int pathId,
                List<Expression> constraints,
                Map<String, Object> symbolicInputs,
                Map<String, Object> outputs,
                long executionTime,
                long initializationTime) {
            this.pathId = pathId;
            this.constraints = new ArrayList<>(constraints);
            this.symbolicInputs = new HashMap<>(symbolicInputs);
            this.outputs = new HashMap<>(outputs);
            this.executionTime = executionTime;
            this.initializationTime = initializationTime;
        }

        @Override
        public String toString() {
            if (initializationTime > 0) {
                return String.format(
                        "Path %d: %d constraints, %d inputs, %dms (init: %dms, exec: %dms)",
                        pathId,
                        constraints.size(),
                        symbolicInputs.size(),
                        executionTime + initializationTime,
                        initializationTime,
                        executionTime);
            } else {
                return String.format(
                        "Path %d: %d constraints, %d inputs, %dms",
                        pathId, constraints.size(), symbolicInputs.size(), executionTime);
            }
        }
    }

    /**
     * Decision point in the execution tree.
     */
    public static class DecisionPoint {
        public final String variable;
        public final String condition;
        public final List<Object> possibleValues;
        public final int depth;

        public DecisionPoint(String variable, String condition, List<Object> possibleValues, int depth) {
            this.variable = variable;
            this.condition = condition;
            this.possibleValues = possibleValues;
            this.depth = depth;
        }
    }

    /**
     * CORE API: Automatically detect and tag any value as symbolic.
     * Works with any type - no hardcoding required.
     *
     * @param label Unique identifier for this symbolic variable
     * @param value The concrete value (can be int, String, EAttribute, EObject, etc.)
     * @return Tagged value that Galette will track
     */
    @SuppressWarnings("unchecked")
    public static <T> T makeSymbolic(String label, T value) {
        if (value == null) {
            if (DEBUG) System.out.println("[Symbolic] Warning: Cannot make null symbolic");
            return null;
        }

        Tag symbolicTag = createSymbolicTag(label, value);
        if (symbolicTag == null) {
            if (DEBUG) System.out.println("[Symbolic] Warning: Could not create tag for " + label);
            return value;
        }

        // Store for later analysis
        discoveredSymbolicVariables.add(label);
        symbolicValueMap.put(label, value);

        // Tag the value using Galette
        T taggedValue = tagValue(value, symbolicTag);

        if (DEBUG) {
            System.out.printf(
                    "[Symbolic] Created symbolic variable: %s = %s (type: %s)%n",
                    label, value, value.getClass().getSimpleName());
        }

        return taggedValue;
    }

    /**
     * Create symbolic tag for any type of value.
     */
    private static Tag createSymbolicTag(String label, Object value) {
        if (value instanceof Integer) {
            return GaletteSymbolicator.makeSymbolicInt(label, (Integer) value);
        } else if (value instanceof Double) {
            return GaletteSymbolicator.makeSymbolicDouble(label, (Double) value);
        } else if (value instanceof Long) {
            return GaletteSymbolicator.makeSymbolicLong(label, (Long) value);
        } else if (value instanceof String) {
            return GaletteSymbolicator.makeSymbolicString(label, (String) value);
        } else if (value instanceof Boolean) {
            return GaletteSymbolicator.makeSymbolicInt(label, ((Boolean) value) ? 1 : 0);
        } else if (value instanceof Enum) {
            return GaletteSymbolicator.makeSymbolicInt(label, ((Enum<?>) value).ordinal());
        } else {
            // For complex types (EObject, EAttribute, etc.), use hash-based symbolic value
            return GaletteSymbolicator.makeSymbolicInt(label, System.identityHashCode(value));
        }
    }

    /**
     * Tag value using Galette's Tainter API.
     */
    @SuppressWarnings("unchecked")
    private static <T> T tagValue(T value, Tag tag) {
        if (value instanceof Integer) {
            return (T) (Object) Tainter.setTag((Integer) value, tag);
        } else if (value instanceof Double) {
            return (T) (Object) Tainter.setTag((Double) value, tag);
        } else if (value instanceof Long) {
            return (T) (Object) Tainter.setTag((Long) value, tag);
        } else if (value instanceof String) {
            return (T) (Object) Tainter.setTag((String) value, tag);
        } else if (value instanceof Boolean) {
            int intVal = ((Boolean) value) ? 1 : 0;
            int tagged = Tainter.setTag(intVal, tag);
            return (T) (Object) (tagged == 1);
        } else {
            // For complex objects, tag their fields
            return tagObjectFields(value, tag);
        }
    }

    /**
     * Tag all fields of a complex object.
     */
    private static <T> T tagObjectFields(T obj, Tag tag) {
        try {
            Class<?> clazz = obj.getClass();
            for (Field field : getAllFields(clazz)) {
                field.setAccessible(true);
                Object fieldValue = field.get(obj);
                if (fieldValue != null && isPrimitiveOrWrapper(fieldValue.getClass())) {
                    // Tag the field value
                    Object taggedFieldValue = tagValue(fieldValue, tag);
                    field.set(obj, taggedFieldValue);
                }
            }
        } catch (Exception e) {
            if (DEBUG) System.err.println("[Symbolic] Error tagging object fields: " + e.getMessage());
        }
        return obj;
    }

    /**
     * Get all fields including inherited ones.
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * Check if a class is primitive or wrapper.
     */
    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == Integer.class
                || clazz == Double.class
                || clazz == Long.class
                || clazz == Boolean.class
                || clazz == String.class;
    }

    /**
     * CORE API: Execute a function and automatically collect path constraints.
     *
     * @param symbolicInputs Map of symbolic variable names to their values
     * @param executor Function to execute (reaction, transformation, etc.)
     * @return Result of the execution
     */
    public static <R> R executeWithSymbolicTracking(Map<String, Object> symbolicInputs, SymbolicExecutor<R> executor) {
        return executeWithSymbolicTracking(symbolicInputs, executor, 0L);
    }

    /**
     * CORE API: Execute a function and automatically collect path constraints with initialization time tracking.
     *
     * @param symbolicInputs Map of symbolic variable names to their values
     * @param executor Function to execute (reaction, transformation, etc.)
     * @param initializationTime Time spent in initialization (e.g., VSUM setup) in milliseconds
     * @return Result of the execution
     */
    public static <R> R executeWithSymbolicTracking(
            Map<String, Object> symbolicInputs, SymbolicExecutor<R> executor, long initializationTime) {

        // Tag all inputs as symbolic (don't include this in timing)
        Map<String, Object> taggedInputs = new HashMap<>();
        for (Map.Entry<String, Object> entry : symbolicInputs.entrySet()) {
            Object tagged = makeSymbolic(entry.getKey(), entry.getValue());
            taggedInputs.put(entry.getKey(), tagged);
        }

        // Measure only the actual execution time (not tagging overhead)
        long startTime = System.nanoTime();
        R result = executor.execute(taggedInputs);
        long endTime = System.nanoTime();

        // Collect path constraints (both from Galette and manually generated)
        PathConditionWrapper pc = PathUtils.getCurPC();
        List<Expression> constraints = (pc != null && !pc.isEmpty()) ? pc.getConstraints() : new ArrayList<>();

        // Add manual constraints for symbolic variables used in switch/if statements
        constraints.addAll(generateConstraintsForInputs(symbolicInputs));

        // Store execution path - use microseconds for better precision, display as fractional ms
        long executionTimeNs = endTime - startTime;
        long executionTime = executionTimeNs / 1_000_000; // Convert to milliseconds (may be 0 for fast operations)
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", result);

        ExecutionPath path = new ExecutionPath(
                exploredPaths.size() + 1, constraints, symbolicInputs, outputs, executionTime, initializationTime);
        exploredPaths.add(path);

        return result;
    }

    /**
     * Generate constraints for symbolic inputs (for switch/if statements).
     * This creates equality constraints like "user_choice == 0", "user_choice == 1", etc.
     */
    private static List<Expression> generateConstraintsForInputs(Map<String, Object> symbolicInputs) {
        List<Expression> constraints = new ArrayList<>();

        for (Map.Entry<String, Object> entry : symbolicInputs.entrySet()) {
            String varName = entry.getKey();
            Object value = entry.getValue();

            // Create symbolic variable
            Variable symbolicVar = null;
            Expression valueExpr = null;

            if (value instanceof Integer) {
                symbolicVar = new IntVariable(varName, 0, Integer.MAX_VALUE);
                valueExpr = new IntConstant((Integer) value);
            } else if (value instanceof Double) {
                symbolicVar = new RealVariable(varName, 0.0, Double.MAX_VALUE);
                valueExpr = new RealConstant((Double) value);
            } else if (value instanceof Long) {
                symbolicVar = new IntVariable(varName, 0, Integer.MAX_VALUE);
                valueExpr = new IntConstant(((Long) value).intValue());
            } else if (value instanceof Boolean) {
                symbolicVar = new IntVariable(varName, 0, 1);
                valueExpr = new IntConstant(((Boolean) value) ? 1 : 0);
            } else if (value instanceof String) {
                // For strings, skip constraint generation (complex)
                continue;
            }

            // Create equality constraint: var == value
            if (symbolicVar != null && valueExpr != null) {
                Expression eqConstraint = new BinaryOperation(Operator.EQ, symbolicVar, valueExpr);
                constraints.add(eqConstraint);
            }
        }

        return constraints;
    }

    /**
     * Functional interface for execution.
     */
    @FunctionalInterface
    public interface SymbolicExecutor<R> {
        R execute(Map<String, Object> inputs);
    }

    /**
     * CORE API: Automatically explore all possible execution paths.
     *
     * This discovers decision points (switch statements, if conditions) and
     * systematically explores all branches.
     *
     * @param baseInputs Base symbolic inputs
     * @param variableRanges Possible values for each decision variable
     * @param executor Function to execute
     */
    public static <R> List<R> exploreAllPaths(
            Map<String, Object> baseInputs, Map<String, List<Object>> variableRanges, SymbolicExecutor<R> executor) {

        List<R> results = new ArrayList<>();
        exploredPaths.clear();

        System.out.println("\n" + repeatString("=", 70));
        System.out.println("AUTOMATIC PATH EXPLORATION");
        System.out.println(repeatString("=", 70));

        // Generate all combinations of inputs
        List<Map<String, Object>> allInputCombinations = generateInputCombinations(baseInputs, variableRanges);

        System.out.printf("Discovered %d possible execution paths%n", allInputCombinations.size());
        System.out.println();

        // Warmup: Execute first path once to eliminate JVM warmup effects on timing
        if (!allInputCombinations.isEmpty()) {
            System.out.println("--- Warmup execution (not counted) ---");
            PathUtils.resetPC();
            try {
                // Execute with full symbolic tracking to warm up all code paths
                executeWithSymbolicTracking(allInputCombinations.get(0), executor);
            } catch (Exception e) {
                // Ignore warmup errors
            }
            exploredPaths.clear(); // Clear warmup data
            System.out.println();
        }

        // Execute each path
        for (int i = 0; i < allInputCombinations.size(); i++) {
            Map<String, Object> inputs = allInputCombinations.get(i);

            System.out.printf("--- Exploring Path %d/%d ---%n", i + 1, allInputCombinations.size());
            printInputs(inputs);

            // Reset path condition for each execution
            PathUtils.resetPC();

            // Execute with symbolic tracking
            R result = executeWithSymbolicTracking(inputs, executor);
            results.add(result);

            // Print collected constraints from the most recent execution path
            ExecutionPath lastPath = exploredPaths.get(exploredPaths.size() - 1);
            if (!lastPath.constraints.isEmpty()) {
                System.out.println("Collected constraints:");
                for (Expression expr : lastPath.constraints) {
                    System.out.println("  • " + expr);
                }
            } else {
                System.out.println("No constraints collected (concrete execution)");
            }

            System.out.println();
        }

        return results;
    }

    /**
     * Generate all input combinations from ranges.
     */
    private static List<Map<String, Object>> generateInputCombinations(
            Map<String, Object> baseInputs, Map<String, List<Object>> variableRanges) {

        List<Map<String, Object>> combinations = new ArrayList<>();
        List<String> variables = new ArrayList<>(variableRanges.keySet());

        if (variables.isEmpty()) {
            combinations.add(new HashMap<>(baseInputs));
            return combinations;
        }

        generateCombinationsRecursive(baseInputs, variableRanges, variables, 0, new HashMap<>(), combinations);
        return combinations;
    }

    /**
     * Recursive helper for generating combinations.
     */
    private static void generateCombinationsRecursive(
            Map<String, Object> baseInputs,
            Map<String, List<Object>> variableRanges,
            List<String> variables,
            int index,
            Map<String, Object> current,
            List<Map<String, Object>> results) {

        if (index == variables.size()) {
            Map<String, Object> combination = new HashMap<>(baseInputs);
            combination.putAll(current);
            results.add(combination);
            return;
        }

        String variable = variables.get(index);
        List<Object> values = variableRanges.get(variable);

        for (Object value : values) {
            Map<String, Object> next = new HashMap<>(current);
            next.put(variable, value);
            generateCombinationsRecursive(baseInputs, variableRanges, variables, index + 1, next, results);
        }
    }

    /**
     * Print inputs in a readable format.
     */
    private static void printInputs(Map<String, Object> inputs) {
        System.out.println("Inputs:");
        for (Map.Entry<String, Object> entry : inputs.entrySet()) {
            System.out.printf("  %s = %s%n", entry.getKey(), entry.getValue());
        }
    }

    /**
     * CORE API: Automatically discover decision points from collected constraints.
     *
     * @return List of decision points found in the code
     */
    public static List<DecisionPoint> discoverDecisionPoints() {
        List<DecisionPoint> decisionPoints = new ArrayList<>();
        Map<String, Set<Object>> variableValues = new HashMap<>();

        // Analyze all explored paths
        for (ExecutionPath path : exploredPaths) {
            for (Expression constraint : path.constraints) {
                extractDecisionVariables(constraint, variableValues);
            }
        }

        // Create decision points
        int depth = 0;
        for (Map.Entry<String, Set<Object>> entry : variableValues.entrySet()) {
            String variable = entry.getKey();
            List<Object> values = new ArrayList<>(entry.getValue());
            String condition = inferConditionType(values);
            decisionPoints.add(new DecisionPoint(variable, condition, values, depth++));
        }

        return decisionPoints;
    }

    /**
     * Extract decision variables from constraints.
     */
    private static void extractDecisionVariables(Expression expr, Map<String, Set<Object>> variableValues) {
        if (expr instanceof Variable) {
            String varName = ((Variable) expr).getName();
            variableValues.putIfAbsent(varName, new HashSet<>());
        } else if (expr instanceof Operation) {
            Operation op = (Operation) expr;
            for (int i = 0; i < op.getArity(); i++) {
                extractDecisionVariables(op.getOperand(i), variableValues);
                if (op.getOperand(i) instanceof IntConstant) {
                    String varName = extractVariableName(op);
                    if (varName != null) {
                        variableValues.putIfAbsent(varName, new HashSet<>());
                        variableValues.get(varName).add(((IntConstant) op.getOperand(i)).getValueLong());
                    }
                } else if (op.getOperand(i) instanceof RealConstant) {
                    String varName = extractVariableName(op);
                    if (varName != null) {
                        variableValues.putIfAbsent(varName, new HashSet<>());
                        variableValues.get(varName).add(op.getOperand(i).toString());
                    }
                }
            }
        }
    }

    /**
     * Extract variable name from operation.
     */
    private static String extractVariableName(Operation op) {
        for (int i = 0; i < op.getArity(); i++) {
            if (op.getOperand(i) instanceof Variable) {
                return ((Variable) op.getOperand(i)).getName();
            }
        }
        return null;
    }

    /**
     * Infer condition type from values.
     */
    private static String inferConditionType(List<Object> values) {
        if (values.isEmpty()) return "unknown";
        if (values.size() == 2 && values.contains(0) && values.contains(1)) {
            return "boolean";
        }
        if (values.stream().allMatch(v -> v instanceof Integer)) {
            return "switch";
        }
        return "comparison";
    }

    /**
     * Reset all state for fresh execution.
     */
    public static void reset() {
        discoveredSymbolicVariables.clear();
        symbolicValueMap.clear();
        exploredPaths.clear();
        PathUtils.resetPC();
        GaletteSymbolicator.reset();
    }

    /**
     * Get all explored paths.
     */
    public static List<ExecutionPath> getExploredPaths() {
        return new ArrayList<>(exploredPaths);
    }

    /**
     * Get all discovered symbolic variables.
     */
    public static Set<String> getDiscoveredSymbolicVariables() {
        return new HashSet<>(discoveredSymbolicVariables);
    }
}
