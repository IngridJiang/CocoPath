package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.*;
import za.ac.sun.cs.green.expr.BinaryOperation;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation.Operator;
import za.ac.sun.cs.green.expr.Variable;

/**
 * @purpose Automatic path exploration for symbolic execution
 * @feature DFS/BFS path exploration strategies
 * @feature Systematic constraint negation with domain constraints
 * @feature Path pruning and optimization
 *
 */
public class PathExplorer {

    private static final boolean DEBUG = true; // Boolean.getBoolean("path.explorer.debug");
    private static final int MAX_ITERATIONS = Integer.getInteger("path.explorer.max.iterations", 200);

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

    // If-based multi-variable exploration: stable variable ordering by first appearance
    // Maps qualified variable name → index in currentInputsList (insertion-ordered)
    private final java.util.LinkedHashMap<String, Integer> varNameToIndex = new java.util.LinkedHashMap<>();

    // Initial values passed to exploreMultipleIntegers, used to reset inner variables
    // when backtracking from an inner variable to an outer one.
    private List<Integer> storedInitialValues = null;

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
        InputSolution solution = Z3ConstraintSolver.solveConstraintWithZ3(combinedConstraint);

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
        varNameToIndex.clear();
        storedInitialValues = new ArrayList<>(initialValues);

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

            // Update variable registry and domain constraints from this execution's constraints
            if (pc != null && !pc.isEmpty()) {
                List<Expression> tempConstraints = pc.getConstraints();
                // Register new variable names (by order of first appearance in branch constraints)
                updateVarNameToIndex(tempConstraints);
                // Accumulate domain constraints (AND-expressions from getOrMakeSymbolicInt)
                extractDomainConstraints(tempConstraints, numVars);
            }

            // Build variable names list for recording (use stable index-ordered list)
            List<String> variableNames = new ArrayList<>(varNameToIndex.keySet());

            // If we still don't have variable names, use generic names
            if (variableNames.isEmpty()) {
                for (int i = 0; i < currentInputsList.size(); i++) {
                    variableNames.add("input_" + i);
                }
            }

            // Map the current input values to variable names for the PathRecord
            Map<String, Integer> currentInputs = new HashMap<>();
            for (Map.Entry<String, Integer> entry : varNameToIndex.entrySet()) {
                int idx = entry.getValue();
                if (idx < currentInputsList.size()) {
                    currentInputs.put(entry.getKey(), currentInputsList.get(idx));
                }
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

            // Generate next input combination
            currentInputsList = generateNextMultiVarInputList(constraints, variableNames, currentInputsList, numVars);

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

    /**
     * Scan the current constraint list for domain constraints (AND-expressions produced by
     * {@code PathUtils.addIntDomainConstraint}) and accumulate them into {@code domainConstraint}.
     *
     * <p>This method is called on <em>every</em> iteration so that variables whose domain is
     * registered later (e.g. calibChoice is not called when profile=skip) are captured the
     * first time they appear.
     *
     * <p>A domain constraint has the form {@code AND(LE(min, var), LT(var, max))} — a top-level
     * AND-expression.  Branch constraints from if-statement instrumentation are simple comparisons
     * (LT, GE, etc.) and are therefore easy to distinguish.
     */
    private void extractDomainConstraints(List<Expression> constraints, int numVars) {
        // If-based: detect top-level AND-expressions as domain constraints; accumulate cumulatively.
        for (Expression c : constraints) {
            if (isDomainConstraint(c)) {
                String cStr = c.toString();
                if (domainConstraint == null) {
                    domainConstraint = c;
                } else if (!domainConstraint.toString().contains(cStr)) {
                    // Only add if not already covered (avoids duplicating same var's domain)
                    domainConstraint = new BinaryOperation(Operator.AND, domainConstraint, c);
                }
            }
        }

        if (DEBUG && domainConstraint != null) {
            System.out.println("[PathExplorer:extractDomainConstraints] Accumulated domain: " + domainConstraint);
        }
    }

    /**
     * Returns true when {@code expr} is a domain constraint — a top-level AND-expression whose
     * children are simple comparisons (as produced by {@link PathUtils#addIntDomainConstraint}).
     * Branch constraints from if-statement instrumentation are plain comparisons (not AND).
     */
    private boolean isDomainConstraint(Expression expr) {
        if (!(expr instanceof BinaryOperation)) return false;
        return ((BinaryOperation) expr).getOperator() == Operator.AND;
    }

    /**
     * Extract the primary variable name from a simple comparison constraint
     * (e.g. {@code LT(var, 34)} → "var", {@code GE(0, var)} → "var").
     * Returns null for domain constraints (AND-expressions) or constants.
     */
    private String extractPrimaryVarName(Expression expr) {
        if (!(expr instanceof BinaryOperation)) return null;
        BinaryOperation binOp = (BinaryOperation) expr;
        if (binOp.left instanceof Variable) return ((Variable) binOp.left).getName();
        if (binOp.right instanceof Variable) return ((Variable) binOp.right).getName();
        return null;
    }

    /**
     * Update {@link #varNameToIndex} with any new variable names found in branch constraints
     * (non-AND expressions).  Variables are numbered by order of first appearance so that
     * the index matches the position in {@code currentInputsList}.
     */
    private void updateVarNameToIndex(List<Expression> constraints) {
        for (Expression c : constraints) {
            if (!isDomainConstraint(c)) {
                String varName = extractPrimaryVarName(c);
                if (varName != null && !varNameToIndex.containsKey(varName)) {
                    varNameToIndex.put(varName, varNameToIndex.size());
                    if (DEBUG) {
                        System.out.println("[PathExplorer:updateVarNameToIndex] Registered var["
                                + (varNameToIndex.size() - 1) + "] = " + varName);
                    }
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
     * Generate the next input list for if-based multi-variable exploration.
     *
     * <p>Separates domain constraints (top-level AND-expressions) from branch constraints
     * (simple comparisons from instrumented if-statements).  Groups branch constraints by
     * variable and uses the <em>last</em> branch constraint per variable as the representative
     * for backtracking — mirroring the single-variable "negate last constraint" strategy.
     *
     * <p>UNSAT-triggered backtracking: when all values for the rightmost active variable are
     * exhausted, remove its accumulated negations and try the next variable to the left.
     *
     * @param currentConstraints constraints collected by the current execution
     * @param variableNames      qualified variable names in index order (from varNameToIndex)
     * @param previousInputs     previous concrete input list (used as fallback for unassigned vars)
     * @param numVars            total number of symbolic variables
     */
    private List<Integer> generateNextMultiVarInputList(
            List<Expression> currentConstraints,
            List<String> variableNames,
            List<Integer> previousInputs,
            int numVars) {

        if (currentConstraints.isEmpty()) {
            return null;
        }

        // Collect branch constraints (non-domain) grouped by variable, in index order
        // orderedVarNames mirrors varNameToIndex key order
        List<String> orderedVarNames = new ArrayList<>(varNameToIndex.keySet());

        // For each variable, find the LAST branch constraint that mentions it
        // (the deepest if-comparison, which uniquely characterises the interval taken)
        List<Expression> lastBranchPerVar = new ArrayList<>();
        for (String varName : orderedVarNames) {
            Expression lastBranch = null;
            for (Expression c : currentConstraints) {
                if (!isDomainConstraint(c) && varName.equals(extractPrimaryVarName(c))) {
                    lastBranch = c;
                }
            }
            lastBranchPerVar.add(lastBranch); // may be null if var inactive in this path
        }

        if (DEBUG) {
            System.out.println("[PathExplorer:generateNextMultiVarInputList] last-branch-per-var:");
            for (int i = 0; i < orderedVarNames.size(); i++) {
                System.out.println("  var[" + i + "] " + orderedVarNames.get(i) + " → " + lastBranchPerVar.get(i));
            }
        }

        // UNSAT-triggered backtracking from rightmost active variable
        for (int tryVarIdx = lastBranchPerVar.size() - 1; tryVarIdx >= 0; tryVarIdx--) {
            Expression branchToNegate = lastBranchPerVar.get(tryVarIdx);
            if (branchToNegate == null) {
                // Variable was not active in this path (e.g. calibChoice on skip path) — skip
                continue;
            }

            Expression negated = ConstraintSolver.negateConstraint(branchToNegate);
            negatedSwitchConstraints.add(negated);

            if (DEBUG) {
                System.out.println("[PathExplorer:generateNextMultiVarInputList] Negating var["
                        + tryVarIdx + "] (" + orderedVarNames.get(tryVarIdx) + "): "
                        + branchToNegate + " → " + negated);
            }

            // Build: domain AND all accumulated negations
            Expression combined = domainConstraint;
            for (Expression neg : negatedSwitchConstraints) {
                combined = (combined == null) ? neg : new BinaryOperation(Operator.AND, combined, neg);
            }

            // Pin outer variables (index < tryVarIdx) to their current-execution intervals.
            // Without this, the solver is free to pick arbitrary values for those variables
            // (e.g. disc1Profile=-1 when we only want to explore disc2's next interval).
            for (Expression c : currentConstraints) {
                if (!isDomainConstraint(c)) {
                    String varName = extractPrimaryVarName(c);
                    Integer varIdx = varNameToIndex.get(varName);
                    if (varIdx != null && varIdx < tryVarIdx) {
                        combined = (combined == null) ? c : new BinaryOperation(Operator.AND, combined, c);
                    }
                }
            }

            if (DEBUG) {
                System.out.println("[PathExplorer:generateNextMultiVarInputList] Solver constraint: " + combined);
            }

            InputSolution solution = Z3ConstraintSolver.solveConstraintWithZ3(combined);

            if (solution != null && solution.isSatisfiable()) {
                // SAT: build result list, starting from previous values as fallback
                List<Integer> result = new ArrayList<>(previousInputs);
                // Pad to numVars in case previousInputs is shorter
                while (result.size() < numVars) result.add(0);

                for (String key : solution.getLabels()) {
                    Object value = solution.getValue(key);
                    Integer idx = varNameToIndex.get(key);
                    if (idx != null && value instanceof Number && idx < result.size()) {
                        result.set(idx, ((Number) value).intValue());
                        if (DEBUG) {
                            System.out.println("[PathExplorer:generateNextMultiVarInputList] " + "Set result[" + idx
                                    + "] (" + key + ") = " + result.get(idx));
                        }
                    }
                }

                // When we backtracked to an outer variable (tryVarIdx < last index),
                // reset all inner variables (index > tryVarIdx) to their initial values
                // so that the inner exploration restarts from the beginning.
                if (storedInitialValues != null && tryVarIdx < orderedVarNames.size() - 1) {
                    for (int j = tryVarIdx + 1; j < orderedVarNames.size(); j++) {
                        String innerVarName = orderedVarNames.get(j);
                        Integer innerIdx = varNameToIndex.get(innerVarName);
                        if (innerIdx != null && innerIdx < result.size() && innerIdx < storedInitialValues.size()) {
                            result.set(innerIdx, storedInitialValues.get(innerIdx));
                            if (DEBUG) {
                                System.out.println("[PathExplorer:generateNextMultiVarInputList] Reset inner var["
                                        + innerIdx + "] (" + innerVarName + ") to initial "
                                        + storedInitialValues.get(innerIdx));
                            }
                        }
                    }
                }

                return result;
            }

            // UNSAT: variable at tryVarIdx is exhausted under current negations.
            // Remove ALL negations for variables at index >= tryVarIdx and backtrack.
            String exhaustedVarName = orderedVarNames.get(tryVarIdx);
            final int exhaustedIdx = tryVarIdx;
            negatedSwitchConstraints.removeIf(neg -> {
                String varInNeg = extractPrimaryVarName(neg);
                if (varInNeg != null) {
                    Integer negVarIdx = varNameToIndex.get(varInNeg);
                    return negVarIdx != null && negVarIdx >= exhaustedIdx;
                }
                // Fallback: string-based check for the exhausted variable
                return neg.toString().contains(exhaustedVarName);
            });

            if (DEBUG) {
                System.out.println("[PathExplorer:generateNextMultiVarInputList] UNSAT for var["
                        + tryVarIdx + "] (" + exhaustedVarName + "), backtracking. "
                        + "Remaining negations: " + negatedSwitchConstraints.size());
            }
        }

        if (DEBUG) {
            System.out.println(
                    "[PathExplorer:generateNextMultiVarInputList] " + "All variables exhausted - exploration complete");
        }
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
        InputSolution solution = Z3ConstraintSolver.solveConstraintWithZ3(combinedConstraint);

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
