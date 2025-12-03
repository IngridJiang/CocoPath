package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import edu.neu.ccs.prl.galette.internal.runtime.Tainter;
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

    private static final boolean DEBUG = Boolean.getBoolean("path.explorer.debug");
    private static final int MAX_ITERATIONS = Integer.getInteger("path.explorer.max.iterations", 100);

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

    public List<PathRecord> exploreInteger(String variableName, int initialValue, PathExecutor executor) {
        exploredPaths.clear();
        exploredConstraintSignatures.clear();
        negatedSwitchConstraints.clear();
        domainConstraint = null;

        int iteration = 0;
        Integer currentInput = initialValue;

        while (currentInput != null && iteration < MAX_ITERATIONS) {
            if (DEBUG) {
                System.out.println("Iteration " + (iteration + 1) + ": " + variableName + " = " + currentInput);
            }

            // Reset symbolic execution state
            GaletteSymbolicator.reset();
            PathUtils.resetPC();

            // Create symbolic value
            String label = variableName + "_" + iteration;
            Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt(label, currentInput);
            int taggedValue = Tainter.setTag(currentInput, symbolicTag);

            // Execute and collect constraints
            long startTime = System.currentTimeMillis();
            PathConditionWrapper pc = executor.execute(taggedValue);
            long endTime = System.currentTimeMillis();

            if (pc == null || pc.isEmpty()) {
                if (DEBUG) System.out.println("No constraints collected - concrete execution");
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
                System.out.println("Collected " + constraints.size() + " constraints");
                for (Expression expr : constraints) {
                    System.out.println("  - " + expr.toString());
                }
                System.out.println("Execution time: " + (endTime - startTime) + " ms");
            }

            if (exploredConstraintSignatures.contains(constraintSignature)) {
                if (DEBUG) System.out.println("Path already explored");
                break;
            }

            Map<String, Object> inputs = new HashMap<>();
            inputs.put(variableName, currentInput);
            exploredPaths.add(new PathRecord(iteration, inputs, constraints, endTime - startTime));
            exploredConstraintSignatures.add(constraintSignature);

            // Extract domain and switch constraints
            if (iteration == 0 && constraints.size() >= 1) {
                domainConstraint = constraints.get(0); // First constraint is domain
                if (DEBUG) System.out.println("Domain constraint: " + domainConstraint);
            }

            currentInput = generateNextInput(constraints, variableName);

            if (currentInput == null) {
                if (DEBUG) System.out.println("No more satisfiable inputs - terminating exploration");
                break;
            }

            iteration++;
        }

        if (iteration >= MAX_ITERATIONS && DEBUG) {
            System.out.println("Reached max iterations: " + MAX_ITERATIONS);
        }

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
            System.out.println("Negating switch constraint: " + switchConstraint + " -> " + negatedSwitch);
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
            System.out.println("Combined constraint for solver: " + combinedConstraint);
        }

        // Solve the combined constraint
        InputSolution solution = ConstraintSolver.solveConstraint(combinedConstraint);

        if (solution == null || !solution.isSatisfiable()) {
            if (DEBUG) System.out.println("UNSAT - no more inputs satisfy the constraints");
            return null;
        }

        // Extract the value for our variable
        Object value = solution.getValue(variableName);
        if (value == null) {
            for (String key : solution.getLabels()) {
                if (key.startsWith(variableName) || key.equals("user_choice")) {
                    value = solution.getValue(key);
                    if (DEBUG) System.out.println("Found value under key: " + key);
                    break;
                }
            }
        }

        if (value instanceof Integer) {
            if (DEBUG) System.out.println("Next input from solver: " + value);
            return (Integer) value;
        } else if (value instanceof Number) {
            int intVal = ((Number) value).intValue();
            if (DEBUG) System.out.println("Next input from solver (converted): " + intVal);
            return intVal;
        }

        if (DEBUG) System.out.println("Could not extract integer value from solution");
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

    /**
     * Explore all paths for multiple symbolic integer variables.
     *
     * This method systematically explores all combinations of values for multiple
     * symbolic variables by using constraint solving to generate inputs.
     *
     * @param variableNames Ordered list of variable names
     * @param initialValues Initial concrete values for each variable (same order)
     * @param executor Execution function that takes a map of variable -> value
     * @return List of explored paths
     */
    public List<PathRecord> exploreMultipleIntegers(
            List<String> variableNames, List<Integer> initialValues, MultiVarPathExecutor executor) {

        if (variableNames.size() != initialValues.size()) {
            throw new IllegalArgumentException("Variable names and initial values must have the same size");
        }

        exploredPaths.clear();
        exploredConstraintSignatures.clear();
        negatedSwitchConstraints.clear();
        domainConstraint = null;
        negationsPerVariable.clear();

        int numVars = variableNames.size();

        // Initialize negations list for each variable
        for (int i = 0; i < numVars; i++) {
            negationsPerVariable.add(new ArrayList<>());
        }

        int iteration = 0;
        Map<String, Integer> currentInputs = new HashMap<>();
        for (int i = 0; i < numVars; i++) {
            currentInputs.put(variableNames.get(i), initialValues.get(i));
        }

        while (currentInputs != null && iteration < MAX_ITERATIONS) {
            if (DEBUG) {
                System.out.println("\n=== Iteration " + (iteration + 1) + " ===");
                for (String varName : variableNames) {
                    System.out.println("  " + varName + " = " + currentInputs.get(varName));
                }
            }

            // Reset symbolic execution state
            GaletteSymbolicator.reset();
            PathUtils.resetPC();

            // Create symbolic values for ALL variables
            Map<String, Object> taggedInputs = new HashMap<>();
            for (String varName : variableNames) {
                Integer value = currentInputs.get(varName);
                String label = varName + "_iter" + iteration;
                Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt(label, value);
                int taggedValue = Tainter.setTag(value, symbolicTag);
                taggedInputs.put(varName, taggedValue);
            }

            // Execute and collect constraints
            long startTime = System.currentTimeMillis();
            PathConditionWrapper pc = executor.execute(taggedInputs);
            long endTime = System.currentTimeMillis();

            if (pc == null || pc.isEmpty()) {
                if (DEBUG) System.out.println("No constraints collected - concrete execution");
                Map<String, Object> inputs = new HashMap<>(currentInputs);
                exploredPaths.add(new PathRecord(iteration, inputs, new ArrayList<>(), endTime - startTime));
                // Try incrementing first variable
                currentInputs = incrementInputs(currentInputs, variableNames);
                iteration++;
                continue;
            }

            List<Expression> constraints = pc.getConstraints();
            String constraintSignature = buildConstraintSignature(constraints);

            if (DEBUG) {
                System.out.println("Collected " + constraints.size() + " constraints:");
                for (Expression expr : constraints) {
                    System.out.println("  - " + expr.toString());
                }
                System.out.println("Execution time: " + (endTime - startTime) + " ms");
            }

            if (exploredConstraintSignatures.contains(constraintSignature)) {
                if (DEBUG) System.out.println("Path already explored (duplicate constraint signature)");
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
            currentInputs = generateNextMultiVarInput(constraints, variableNames, numVars);

            if (currentInputs == null) {
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
        // Domain constraints are the first numVars constraints
        // They have the form: (min <= var) AND (var < max)
        if (constraints.size() >= numVars) {
            List<Expression> domainExprs = new ArrayList<>();
            for (int i = 0; i < numVars; i++) {
                domainExprs.add(constraints.get(i));
            }

            // Combine domain constraints with AND
            domainConstraint = domainExprs.get(0);
            for (int i = 1; i < domainExprs.size(); i++) {
                domainConstraint = new BinaryOperation(Operator.AND, domainConstraint, domainExprs.get(i));
            }

            if (DEBUG) {
                System.out.println("Extracted domain constraints: " + domainConstraint);
            }
        }
    }

    /**
     * Generate the next input combination for multi-variable exploration with proper backtracking.
     *
     * Strategy: Enumerate combinations like an odometer
     * - Variables indexed 0 (leftmost) to n-1 (rightmost)
     * - Increment rightmost variable first
     * - When rightmost exhausted, reset it and increment previous variable
     * - Track negations separately per variable for proper backtracking
     */
    private Map<String, Integer> generateNextMultiVarInput(
            List<Expression> currentConstraints, List<String> variableNames, int numVars) {

        if (currentConstraints.isEmpty()) {
            return null;
        }

        // Path constraints are after domain constraints
        // For 2 variables: constraints = [domain1, domain2, switch1, switch2]
        int numDomain = numVars;
        int numPath = currentConstraints.size() - numDomain;

        if (numPath <= 0) {
            if (DEBUG) System.out.println("No path constraints to negate");
            return null;
        }

        // Extract path constraints (one per variable, in order)
        List<Expression> pathConstraints = new ArrayList<>();
        for (int i = 0; i < numPath; i++) {
            pathConstraints.add(currentConstraints.get(numDomain + i));
        }

        // Try to increment the rightmost (last) variable first
        int rightmostVarIdx = numPath - 1;
        Expression rightmostConstraint = pathConstraints.get(rightmostVarIdx);
        Expression negatedRightmost = ConstraintSolver.negateConstraint(rightmostConstraint);

        // Add negation for rightmost variable
        negationsPerVariable.get(rightmostVarIdx).add(negatedRightmost);

        if (DEBUG) {
            System.out.println("Incrementing rightmost variable (var" + rightmostVarIdx + "): " + negatedRightmost);
        }

        // Try to solve
        Map<String, Integer> result = trySolveMultiVarWithPerVarNegations(variableNames);

        if (result != null) {
            return result; // Success - found next value for rightmost variable
        }

        // Rightmost variable exhausted - need to backtrack
        if (DEBUG) System.out.println("Rightmost variable exhausted, backtracking...");

        // Backtrack: find a previous variable that can be incremented
        for (int varIdx = rightmostVarIdx; varIdx >= 0; varIdx--) {
            // Clear negations for this and all later variables (reset them)
            for (int j = varIdx; j < numVars; j++) {
                negationsPerVariable.get(j).clear();
            }

            if (varIdx == 0) {
                // Can't backtrack further - all combinations exhausted
                if (DEBUG) System.out.println("Backtracked to first variable - all paths explored");
                return null;
            }

            // Try to increment the previous variable (varIdx - 1)
            int prevVarIdx = varIdx - 1;
            Expression prevConstraint = pathConstraints.get(prevVarIdx);
            Expression negatedPrev = ConstraintSolver.negateConstraint(prevConstraint);
            negationsPerVariable.get(prevVarIdx).add(negatedPrev);

            if (DEBUG) {
                System.out.println("Backtracking to var" + prevVarIdx + ", adding negation: " + negatedPrev);
            }

            result = trySolveMultiVarWithPerVarNegations(variableNames);

            if (result != null) {
                // Success - found next value at this level
                if (DEBUG) System.out.println("Found solution after backtracking: " + result);
                return result;
            }

            // This variable also exhausted, continue backtracking
            if (DEBUG) System.out.println("Variable " + prevVarIdx + " also exhausted, continuing backtrack...");
        }

        // All variables exhausted
        if (DEBUG) System.out.println("All combinations exhausted");
        return null;
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

    private Map<String, Integer> incrementInputs(Map<String, Integer> current, List<String> variableNames) {
        if (variableNames.isEmpty()) {
            return null;
        }

        Map<String, Integer> next = new HashMap<>(current);
        String firstVar = variableNames.get(0);
        Integer val = next.get(firstVar);
        if (val == null) {
            return null;
        }

        next.put(firstVar, val + 1);
        return next;
    }
}
