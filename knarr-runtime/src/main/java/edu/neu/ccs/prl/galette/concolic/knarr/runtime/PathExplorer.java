package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.*;
import za.ac.sun.cs.green.expr.BinaryOperation;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * @purpose Automatic path exploration for symbolic execution
 * @feature DFS/BFS path exploration strategies
 * @feature Systematic constraint negation with domain constraints
 * @feature Path pruning and optimization
 *
 */
public class PathExplorer {

    private static final boolean DEBUG = true; // Boolean.getBoolean("path.explorer.debug");
    private static final int MAX_ITERATIONS =
            Integer.getInteger("path.explorer.max.iterations", 30); // Reduced from 100 for debugging

    public static class PathRecord {
        public final int pathId;
        public final Map<String, Object> inputs;
        public final List<Expression> constraints;
        public final long executionTimeMs;

        public PathRecord(int pathId, Map<String, Object> inputs, List<Expression> constraints, long executionTimeMs) {
            this.pathId = pathId;
            this.inputs = new HashMap<>(inputs);
            this.constraints = new ArrayList<>(constraints);
            this.executionTimeMs = executionTimeMs;
        }

        @Override
        public String toString() {
            return String.format(
                    "Path %d: inputs=%s, constraints=%d, time=%dms",
                    pathId, inputs, constraints.size(), executionTimeMs);
        }
    }

    @FunctionalInterface
    public interface PathExecutor {
        PathConditionWrapper execute(Object input);
    }

    @FunctionalInterface
    public interface MultiVarPathExecutor {
        PathConditionWrapper execute(Map<String, Object> inputs);
    }

    private final List<PathRecord> exploredPaths = new ArrayList<>();
    private final Set<String> exploredConstraintSignatures = new HashSet<>();
    private final List<Expression> negatedSwitchConstraints = new ArrayList<>();
    private Expression domainConstraint = null;

    // For multi-variable exploration: we need to separate negations per variable
    // to enable proper backtracking
    private final List<List<Expression>> negationsPerVariable = new ArrayList<>();

    // ThreadLocal to pass variable name -> Tag mapping to executor
    private static final ThreadLocal<Map<String, Tag>> currentVarToTag = ThreadLocal.withInitial(HashMap::new);

    /**
     * Get the Tag for a given variable name in the current execution context.
     * This is used by executors to retrieve tags without relying on value-based lookup.
     */
    public static Tag getTagForVariable(String varName) {
        return currentVarToTag.get().get(varName);
    }

    public List<PathRecord> exploreInteger(int initialValue, PathExecutor executor) {
        return exploreInteger(initialValue, executor, null);
    }

    /**
     * Explore integer paths with a custom qualified name for symbolic execution.
     *
     * @param variableName The display name for the variable
     * @param initialValue The initial concrete value
     * @param executor The executor to run for each path
     * @param qualifiedName The qualified name to use for symbolic execution (e.g., "CreateAscetTaskRoutine:execute:userChoice")
     * @return List of explored paths
     */
    public List<PathRecord> exploreInteger(int initialValue, PathExecutor executor, String qualifiedName) {
        exploredPaths.clear();
        exploredConstraintSignatures.clear();
        negatedSwitchConstraints.clear();
        domainConstraint = null;

        int iteration = 0;
        Integer currentInput = initialValue;

        while (currentInput != null && iteration < MAX_ITERATIONS) {
            if (DEBUG) {
                System.out.println("[PathExplorer:exploreInteger] Iteration " + (iteration + 1) + ": " + qualifiedName
                        + " = " + currentInput);
            }

            // Reset path condition but NOT the symbolicator
            // We need to preserve labelToTag mappings for tag reuse
            PathUtils.resetPC();

            // Execute with the concrete value
            // The reactions will create and apply tags as needed
            long startTime = System.currentTimeMillis();
            PathConditionWrapper pc = executor.execute(currentInput);
            long endTime = System.currentTimeMillis();

            String variableName = extractTagFromValue(currentInput);

            if (pc == null || pc.isEmpty()) {
                if (DEBUG)
                    System.out.println("[PathExplorer:exploreInteger] No constraints collected - concrete execution");
                Map<String, Object> inputs = new HashMap<>();
                inputs.put(variableName, currentInput);
                exploredPaths.add(new PathRecord(iteration, inputs, new ArrayList<>(), endTime - startTime));
                currentInput++;
                iteration++;
                continue;
            }

            List<Expression> constraints = pc.getConstraints();
            String constraintSignature = buildConstraintSignature(constraints);

            if (DEBUG) {
                System.out.println("[PathExplorer:exploreInteger] Collected " + constraints.size() + " constraints");
                for (Expression expr : constraints) {
                    System.out.println("  - " + expr.toString());
                }
                System.out.println("[PathExplorer:exploreInteger] Execution time: " + (endTime - startTime) + " ms");
            }

            if (exploredConstraintSignatures.contains(constraintSignature)) {
                if (DEBUG) System.out.println("[PathExplorer:exploreInteger] Path already explored");
                break;
            }

            Map<String, Object> inputs = new HashMap<>();
            inputs.put(variableName, currentInput);
            exploredPaths.add(new PathRecord(iteration, inputs, constraints, endTime - startTime));
            exploredConstraintSignatures.add(constraintSignature);

            // Extract domain and switch constraints
            if (iteration == 0 && constraints.size() >= 1) {
                domainConstraint = constraints.get(0); // First constraint is domain
                if (DEBUG) System.out.println("[PathExplorer:exploreInteger]Domain constraint: " + domainConstraint);
            }

            currentInput = generateNextInput(constraints, variableName);

            if (currentInput == null) {
                if (DEBUG)
                    System.out.println(
                            "[PathExplorer:exploreInteger] No more satisfiable inputs - terminating exploration");
                break;
            }

            iteration++;
        }

        if (iteration >= MAX_ITERATIONS && DEBUG) {
            System.out.println("[PathExplorer:exploreInteger] Reached max iterations: " + MAX_ITERATIONS);
        }

        // Clean up: reset symbolicator state after exploration completes
        GaletteSymbolicator.reset();

        return new ArrayList<>(exploredPaths);
    }

    private Integer generateNextInput(List<Expression> currentConstraints, String variableName) {
        if (currentConstraints.isEmpty()) {
            return null;
        }

        // Get the switch constraint (last constraint, after domain)
        Expression switchConstraint = currentConstraints.get(currentConstraints.size() - 1);

        // Negate the switch constraint
        Expression negatedSwitch = ConstraintSolver.negateConstraint(switchConstraint);
        negatedSwitchConstraints.add(negatedSwitch);

        if (DEBUG) {
            System.out.println("[PathExplorer:generateNextInput] Negating switch constraint: " + switchConstraint
                    + " -> " + negatedSwitch);
        }

        // Build combined constraint: domain AND not_switch1 AND not_switch2 AND ... AND not_switchN
        Expression combinedConstraint = domainConstraint;

        for (Expression negated : negatedSwitchConstraints) {
            if (combinedConstraint == null) {
                combinedConstraint = negated;
            } else {
                combinedConstraint = new BinaryOperation(Operator.AND, combinedConstraint, negated);
            }
        }

        if (DEBUG) {
            System.out.println(
                    "[PathExplorer:generateNextInput] Combined constraint for solver: " + combinedConstraint);
        }

        // Solve the combined constraint
        InputSolution solution = ConstraintSolver.solveConstraint(combinedConstraint);

        if (solution == null || !solution.isSatisfiable()) {
            if (DEBUG)
                System.out.println("[PathExplorer:generateNextInput] UNSAT - no more inputs satisfy the constraints");
            return null;
        }

        // Extract the value for our variable
        // The solver uses the qualified name from the constraints, not the display name
        Object value = null;

        // Try all keys in the solution - the solver will have used the actual variable name from constraints
        for (String key : solution.getLabels()) {
            value = solution.getValue(key);
            if (value != null) {
                if (DEBUG)
                    System.out.println(
                            "[PathExplorer:generateNextInput] Found value under key: " + key + " = " + value);
                break;
            }
        }

        // If still no value, something went wrong
        if (value == null) {
            if (DEBUG) {
                System.out.println(
                        "[PathExplorer:generateNextInput]Warning: No value found in solution. Available keys: "
                                + solution.getLabels());
            }
        }

        if (value instanceof Integer) {
            if (DEBUG) System.out.println("[PathExplorer:generateNextInput] Next input from solver: " + value);
            return (Integer) value;
        } else if (value instanceof Number) {
            int intVal = ((Number) value).intValue();
            if (DEBUG)
                System.out.println("[PathExplorer:generateNextInput] Next input from solver (converted): " + intVal);
            return intVal;
        }

        if (DEBUG) System.out.println("[PathExplorer:generateNextInput] Could not extract integer value from solution");
        return null;
    }

    private String buildConstraintSignature(List<Expression> constraints) {
        if (constraints.isEmpty()) {
            return "empty";
        }

        List<String> sorted = new ArrayList<>();
        for (Expression expr : constraints) {
            sorted.add(expr.toString());
        }
        Collections.sort(sorted);

        return String.join(" AND ", sorted);
    }

    public List<PathRecord> getExploredPaths() {
        return new ArrayList<>(exploredPaths);
    }

    public List<PathRecord> exploreMultipleIntegers(List<Integer> initialValues, MultiVarPathExecutor executor) {
        return exploreMultipleIntegers(initialValues, executor, null);
    }

    /**
     * Explore all paths for multiple symbolic integer variables.
     *
     * This method systematically explores all combinations of values for multiple
     * symbolic variables by using constraint solving to generate inputs.
     * Variable names are extracted from tags created by reactions.
     *
     * @param initialValues Initial concrete values for each variable
     * @param executor Execution function that takes a map of variable -> value
     * @return List of explored paths
     */
    public List<PathRecord> exploreMultipleIntegers(
            List<Integer> initialValues, MultiVarPathExecutor executor, String qualifiedName) {

        exploredPaths.clear();
        exploredConstraintSignatures.clear();
        negatedSwitchConstraints.clear();
        domainConstraint = null;
        negationsPerVariable.clear();

        int numVars = initialValues.size();

        // Initialize negations list for each variable
        for (int i = 0; i < numVars; i++) {
            negationsPerVariable.add(new ArrayList<>());
        }

        int iteration = 0;
        List<Integer> currentInputsList = new ArrayList<>(initialValues);

        while (currentInputsList != null && iteration < MAX_ITERATIONS) {
            if (DEBUG) {
                System.out.println(
                        "\n[PathExplorer:exploreMultipleIntegers] === Iteration " + (iteration + 1) + " ===");
                for (int i = 0; i < currentInputsList.size(); i++) {
                    System.out.println("  Input " + i + " = " + currentInputsList.get(i));
                }
            }

            // Reset path condition but NOT the symbolicator
            // We need to preserve labelToTag mappings for tag reuse
            PathUtils.resetPC();

            // Create map with indexed keys for now - will be replaced with actual variable names after execution
            Map<String, Object> inputsForExecution = new HashMap<>();
            for (int i = 0; i < currentInputsList.size(); i++) {
                inputsForExecution.put("var_" + i, currentInputsList.get(i));
            }

            // Execute and collect constraints
            // The reactions will create and apply tags as needed
            long startTime = System.currentTimeMillis();
            PathConditionWrapper pc = executor.execute(inputsForExecution);
            long endTime = System.currentTimeMillis();

            // Extract variable names from constraints after execution
            List<String> variableNames = new ArrayList<>();
            Map<String, Integer> currentInputs = new HashMap<>();

            // If we have constraints, extract variable names from them
            if (pc != null && !pc.isEmpty()) {
                List<Expression> tempConstraints = pc.getConstraints();
                // Look for switch constraints (equality constraints) to extract variable names
                Set<String> uniqueVarNames = new LinkedHashSet<>();
                for (Expression constraint : tempConstraints) {
                    String constraintStr = constraint.toString();
                    if (constraintStr.contains("==")) {
                        String varName = constraintStr.split("==")[0];
                        uniqueVarNames.add(varName);
                    }
                }
                variableNames.addAll(uniqueVarNames);
            }

            // If we still don't have variable names, use generic names
            if (variableNames.isEmpty()) {
                for (int i = 0; i < currentInputsList.size(); i++) {
                    variableNames.add("input_" + i);
                }
            }

            // Map the values to variable names
            for (int i = 0; i < Math.min(currentInputsList.size(), variableNames.size()); i++) {
                currentInputs.put(variableNames.get(i), currentInputsList.get(i));
            }

            if (DEBUG) {
                System.out.println("[PathExplorer:exploreMultipleIntegers] Extracted variable names from constraints:");
                for (String varName : variableNames) {
                    System.out.println("  " + varName + " = " + currentInputs.get(varName));
                }
            }

            if (pc == null || pc.isEmpty()) {
                if (DEBUG)
                    System.out.println(
                            "[PathExplorer:exploreMultipleIntegers] No constraints collected - concrete execution");
                Map<String, Object> inputs = new HashMap<>(currentInputs);
                exploredPaths.add(new PathRecord(iteration, inputs, new ArrayList<>(), endTime - startTime));
                // Try incrementing first variable
                currentInputsList = incrementInputsList(currentInputsList);
                iteration++;
                continue;
            }

            List<Expression> constraints = pc.getConstraints();
            String constraintSignature = buildConstraintSignature(constraints);

            if (DEBUG) {
                System.out.println(
                        "[PathExplorer:exploreMultipleIntegers] Collected " + constraints.size() + " constraints:");
                for (Expression expr : constraints) {
                    System.out.println("  - " + expr.toString());
                }
                System.out.println(
                        "[PathExplorer:exploreMultipleIntegers] Execution time: " + (endTime - startTime) + " ms");
            }

            if (exploredConstraintSignatures.contains(constraintSignature)) {
                if (DEBUG)
                    System.out.println(
                            "[PathExplorer:exploreMultipleIntegers] Path already explored (duplicate constraint signature)");
                break;
            }

            Map<String, Object> inputs = new HashMap<>(currentInputs);
            exploredPaths.add(new PathRecord(iteration, inputs, constraints, endTime - startTime));
            exploredConstraintSignatures.add(constraintSignature);

            // Extract domain constraints on first iteration
            if (iteration == 0) {
                extractDomainConstraints(constraints, numVars);
            }

            // Generate next input combination
            currentInputsList = generateNextMultiVarInputList(constraints, variableNames, numVars);

            if (currentInputsList == null) {
                if (DEBUG) System.out.println("No more satisfiable inputs - terminating exploration");
                break;
            }

            iteration++;
        }

        if (iteration >= MAX_ITERATIONS && DEBUG) {
            System.out.println("Reached max iterations: " + MAX_ITERATIONS);
        }

        if (DEBUG) {
            System.out.println("\n=== Exploration Complete ===");
            System.out.println("Total paths explored: " + exploredPaths.size());
        }

        return new ArrayList<>(exploredPaths);
    }

    private void extractDomainConstraints(List<Expression> constraints, int numVars) {
        // Domain constraints have the form: (min <= var) AND (var <= max)
        // They alternate with equality constraints: [domain1, equality1, domain2, equality2, ...]
        // We need to extract every other constraint starting from index 0
        if (constraints.size() >= numVars * 2) {
            List<Expression> domainExprs = new ArrayList<>();

            // Extract domain constraints (every other constraint, starting at 0)
            for (int i = 0; i < numVars; i++) {
                int constraintIndex = i * 2; // 0, 2, 4, ...
                if (constraintIndex < constraints.size()) {
                    Expression constraint = constraints.get(constraintIndex);
                    // Verify it looks like a domain constraint (contains >= or <=)
                    String constraintStr = constraint.toString();
                    if (constraintStr.contains(">=") || constraintStr.contains("<=")) {
                        domainExprs.add(constraint);
                    }
                }
            }

            // Combine domain constraints with AND
            if (!domainExprs.isEmpty()) {
                domainConstraint = domainExprs.get(0);
                for (int i = 1; i < domainExprs.size(); i++) {
                    domainConstraint = new BinaryOperation(Operator.AND, domainConstraint, domainExprs.get(i));
                }

                if (DEBUG) {
                    System.out.println("Extracted domain constraints: " + domainConstraint);
                }
            }
        }
    }

    /**
     * Helper method to increment a list of inputs for simple exploration fallback
     */
    private List<Integer> incrementInputsList(List<Integer> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return null;
        }
        List<Integer> newInputs = new ArrayList<>(inputs);
        newInputs.set(0, newInputs.get(0) + 1);
        return newInputs;
    }

    /**
     * Generate next multi-variable input as a list instead of a map.
     * This allows us to work without knowing variable names upfront.
     */
    private List<Integer> generateNextMultiVarInputList(
            List<Expression> currentConstraints, List<String> variableNames, int numVars) {

        Map<String, Integer> resultMap = generateNextMultiVarInput(currentConstraints, variableNames, numVars);

        if (resultMap == null) {
            return null;
        }

        // Since variableNames might be empty, we need to extract values based on the order
        // they appear in the resultMap. The resultMap contains qualified names from constraints.
        List<Integer> resultList = new ArrayList<>();

        // Sort the keys to ensure consistent ordering
        List<String> sortedKeys = new ArrayList<>(resultMap.keySet());
        Collections.sort(sortedKeys);

        for (String key : sortedKeys) {
            resultList.add(resultMap.get(key));
            if (DEBUG) {
                System.out.println("Adding to result list: " + key + " = " + resultMap.get(key));
            }
        }

        return resultList;
    }

    /**
     * Generate the next input combination for multi-variable exploration.
     * This version extracts variable names from the constraints themselves,
     * similar to how generateNextInput works for single variables.
     */
    private Map<String, Integer> generateNextMultiVarInput(
            List<Expression> currentConstraints, List<String> variableNames, int numVars) {

        if (currentConstraints.isEmpty()) {
            return null;
        }

        // Extract switch constraints
        // After first iteration, we only get switch constraints (no domain constraints)
        // So we need to handle both cases: with and without domain constraints
        List<Expression> switchConstraints = new ArrayList<>();

        if (currentConstraints.size() == numVars * 2) {
            // First iteration: has both domain and switch constraints
            // Extract switch constraints from odd indices: 1, 3, 5, ...
            for (int i = 0; i < numVars; i++) {
                int switchIndex = i * 2 + 1;
                if (switchIndex < currentConstraints.size()) {
                    switchConstraints.add(currentConstraints.get(switchIndex));
                }
            }
        } else if (currentConstraints.size() == numVars) {
            // Subsequent iterations: only switch constraints
            switchConstraints.addAll(currentConstraints);
        } else {
            if (DEBUG) {
                System.out.println("[PathExplorer:generateNextMultiVarInput] Unexpected number of constraints: "
                        + currentConstraints.size() + " for " + numVars + " variables");
            }
            // Try to handle it anyway - assume they're all switch constraints
            switchConstraints.addAll(currentConstraints);
        }

        if (switchConstraints.isEmpty()) {
            if (DEBUG) System.out.println("[PathExplorer:generateNextMultiVarInput] No switch constraints to negate");
            return null;
        }

        // Extract domain size from domain constraint to avoid hard-coding
        // Domain constraint looks like: (0<=var)&&(var<5) which means domain is [0,4]
        int maxDomainSize = 5; // Default
        if (domainConstraint != null) {
            String domainStr = domainConstraint.toString();
            if (DEBUG) {
                System.out.println("[PathExplorer:generateNextMultiVarInput] Domain constraint string: " + domainStr);
            }

            // Look for pattern "variable<NUMBER)" where NUMBER is immediately after <
            // E.g., "CreateAscetTaskRoutine:execute:userChoice_forTask_task1<5"
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<(\\d+)\\)");
            java.util.regex.Matcher matcher = pattern.matcher(domainStr);
            if (matcher.find()) {
                try {
                    int upperBound = Integer.parseInt(matcher.group(1));
                    if (upperBound > 0 && upperBound <= 100) {
                        maxDomainSize = upperBound;
                        if (DEBUG) {
                            System.out.println("[PathExplorer:generateNextMultiVarInput] Extracted upper bound: "
                                    + upperBound + " from pattern <" + upperBound + ")");
                        }
                    }
                } catch (NumberFormatException e) {
                    // Keep default
                }
            }
        }

        if (DEBUG) {
            System.out.println("[PathExplorer:generateNextMultiVarInput] Using domain size: " + maxDomainSize);
        }

        // Multi-variable exploration: properly handle backtracking
        // Strategy: For 2 variables with domain [0,4], we want:
        // (0,0), (0,1), (0,2), (0,3), (0,4), then
        // (1,0), (1,1), (1,2), (1,3), (1,4), then
        // (2,0), etc. up to (4,4) = 25 total combinations

        // Count how many negations we have for each variable
        int[] negationsPerVar = new int[numVars];
        for (Expression neg : negatedSwitchConstraints) {
            String negStr = neg.toString();
            // Check which variable this negation is for
            for (int i = 0; i < numVars && i < switchConstraints.size(); i++) {
                String varName = switchConstraints.get(i).toString().split("==")[0];
                if (negStr.contains(varName)) {
                    negationsPerVar[i]++;
                    break;
                }
            }
        }

        if (DEBUG) {
            System.out.println("[PathExplorer:generateNextMultiVarInput] Negations per variable: "
                    + Arrays.toString(negationsPerVar));
        }

        // Find which variable to negate next
        boolean foundVariableToNegate = false;

        // Check if we can increment the rightmost variable
        int lastVarIdx = numVars - 1;
        if (lastVarIdx < switchConstraints.size() && negationsPerVar[lastVarIdx] < maxDomainSize - 1) {
            // Can still negate the last variable
            Expression lastSwitch = switchConstraints.get(lastVarIdx);
            Expression negatedSwitch = ConstraintSolver.negateConstraint(lastSwitch);
            negatedSwitchConstraints.add(negatedSwitch);
            foundVariableToNegate = true;

            if (DEBUG) {
                System.out.println("[PathExplorer:generateNextMultiVarInput] Negating last variable: " + lastSwitch
                        + " -> " + negatedSwitch);
            }
        } else if (lastVarIdx >= 0 && negationsPerVar[lastVarIdx] >= maxDomainSize - 1) {
            // Last variable is exhausted, need to backtrack
            if (DEBUG)
                System.out.println("[PathExplorer:generateNextMultiVarInput] Last variable exhausted, backtracking...");

            // Find the first variable from the right that can be incremented
            for (int i = lastVarIdx - 1; i >= 0; i--) {
                if (i < switchConstraints.size() && negationsPerVar[i] < maxDomainSize - 1) {
                    // Found a variable that can be incremented

                    // First, clear all negations for variables to the right of this one
                    List<Expression> toRemove = new ArrayList<>();
                    for (Expression neg : negatedSwitchConstraints) {
                        String negStr = neg.toString();
                        // Check if this negation is for a variable after i
                        for (int j = i + 1; j < numVars && j < switchConstraints.size(); j++) {
                            String varName = switchConstraints.get(j).toString().split("==")[0];
                            if (negStr.contains(varName)) {
                                toRemove.add(neg);
                                break;
                            }
                        }
                    }
                    negatedSwitchConstraints.removeAll(toRemove);

                    if (DEBUG && !toRemove.isEmpty()) {
                        System.out.println("[PathExplorer:generateNextMultiVarInput] Cleared " + toRemove.size()
                                + " negations for variables after " + i);
                    }

                    // Now negate variable i
                    Expression switchToNegate = switchConstraints.get(i);
                    Expression negatedSwitch = ConstraintSolver.negateConstraint(switchToNegate);
                    negatedSwitchConstraints.add(negatedSwitch);
                    foundVariableToNegate = true;

                    if (DEBUG) {
                        System.out.println("[PathExplorer:generateNextMultiVarInput] Negating variable " + i + ": "
                                + switchToNegate + " -> " + negatedSwitch);
                    }
                    break;
                }
            }
        }

        if (!foundVariableToNegate) {
            if (DEBUG)
                System.out.println(
                        "[PathExplorer:generateNextMultiVarInput] All variables exhausted - exploration complete");
            return null;
        }

        if (DEBUG) {
            System.out.println("[PathExplorer:generateNextMultiVarInput] Total negated constraints: "
                    + negatedSwitchConstraints.size());
        }

        // Build combined constraint: domain AND all negated switches
        Expression combinedConstraint = domainConstraint;

        // Add all negated switch constraints
        for (Expression negated : negatedSwitchConstraints) {
            if (combinedConstraint == null) {
                combinedConstraint = negated;
            } else {
                combinedConstraint = new BinaryOperation(Operator.AND, combinedConstraint, negated);
            }
        }

        if (DEBUG) {
            System.out.println(
                    "[PathExplorer:generateNextMultiVarInput] Combined constraint for solver: " + combinedConstraint);
        }

        // Solve the combined constraint
        InputSolution solution = ConstraintSolver.solveConstraint(combinedConstraint);

        if (solution == null || !solution.isSatisfiable()) {
            if (DEBUG)
                System.out.println(
                        "[PathExplorer:generateNextMultiVarInput] UNSAT - no more inputs satisfy the constraints");
            return null;
        }

        // Extract values for all variables from the solution
        Map<String, Integer> resultMap = new HashMap<>();

        // The solver returns values with the qualified names from constraints
        for (String key : solution.getLabels()) {
            Object value = solution.getValue(key);
            if (value != null) {
                if (DEBUG) {
                    System.out.println(
                            "[PathExplorer:generateNextMultiVarInput] Found value in solution: " + key + " = " + value);
                }

                if (value instanceof Integer) {
                    resultMap.put(key, (Integer) value);
                } else if (value instanceof Number) {
                    resultMap.put(key, ((Number) value).intValue());
                }
            }
        }

        if (resultMap.isEmpty()) {
            if (DEBUG) {
                System.out.println("Warning: No values found in solution. Available keys: " + solution.getLabels());
            }
            return null;
        }

        return resultMap;
    }

    /**
     * Helper method to solve using per-variable negations
     */
    private Map<String, Integer> trySolveMultiVarWithPerVarNegations(List<String> variableNames) {
        // Build combined constraint: domain AND all negations from all variables
        Expression combinedConstraint = domainConstraint;

        int totalNegations = 0;
        for (List<Expression> varNegations : negationsPerVariable) {
            for (Expression negation : varNegations) {
                totalNegations++;
                if (combinedConstraint == null) {
                    combinedConstraint = negation;
                } else {
                    combinedConstraint = new BinaryOperation(Operator.AND, combinedConstraint, negation);
                }
            }
        }

        if (DEBUG) {
            System.out.println("Solving with " + totalNegations + " total negations");
        }

        // Solve the combined constraint
        InputSolution solution = ConstraintSolver.solveConstraint(combinedConstraint);

        if (solution == null || !solution.isSatisfiable()) {
            if (DEBUG) System.out.println("UNSAT");
            return null; // UNSAT
        }

        // Extract values for ALL variables
        Map<String, Integer> nextInputs = new HashMap<>();
        for (String varName : variableNames) {
            Object value = solution.getValue(varName);
            if (value == null) {
                // Try with iteration suffix
                for (String key : solution.getLabels()) {
                    if (key.startsWith(varName)) {
                        value = solution.getValue(key);
                        break;
                    }
                }
            }

            if (value instanceof Integer) {
                nextInputs.put(varName, (Integer) value);
            } else if (value instanceof Number) {
                nextInputs.put(varName, ((Number) value).intValue());
            } else {
                if (DEBUG) System.out.println("Could not extract integer value for " + varName);
                return null;
            }
        }

        if (DEBUG) {
            System.out.println("SAT: " + nextInputs);
        }

        return nextInputs;
    }

    /**
     * This may not be needed at all, as in Anne's run this did not find a tag but the exploration worked anyway.
     * @param selected
     * @return
     */
    private String extractTagFromValue(Integer selected) {
        // Check if the value has a tag
        Tag tag = null;
        try {
            // Try to extract tag using Galette's Tainter
            tag = edu.neu.ccs.prl.galette.internal.runtime.Tainter.getTag(selected);
            if (tag != null && !tag.isEmpty()) {
                System.out.println("[PathExplorer:extractTagFromValue]   - Tag found: " + tag);
                System.out.println("[PathExplorer:extractTagFromValue]   - Tag labels: "
                        + java.util.Arrays.toString(tag.getLabels()));

                // Extract qualified name from tag label
                String qualifiedName = tag.getLabels()[0].toString();
                return qualifiedName;
            } else {
                System.out.println("[PathExplorer:extractTagFromValue]   - No tag found on value");
            }
        } catch (Exception e) {
            System.out.println("[PathExplorer:extractTagFromValue]   - Error extracting tag: " + e.getMessage());
        }
        return null;
    }
}
