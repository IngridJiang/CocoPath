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

    private final List<PathRecord> exploredPaths = new ArrayList<>();
    private final Set<String> exploredConstraintSignatures = new HashSet<>();
    private final List<Expression> negatedSwitchConstraints = new ArrayList<>();
    private Expression domainConstraint = null;

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
}
