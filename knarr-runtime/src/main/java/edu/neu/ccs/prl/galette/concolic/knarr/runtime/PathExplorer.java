package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.*;
import java.util.ArrayDeque;
import java.util.Deque;
import za.ac.sun.cs.green.expr.BinaryOperation;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation.Operator;
import za.ac.sun.cs.green.expr.UnaryOperation;
import za.ac.sun.cs.green.expr.Variable;

/**
 * Automatic path exploration engine for concolic execution.
 *
 * <p>This class orchestrates systematic exploration of all execution paths
 * by collecting path constraints and using constraint solving to generate
 * new inputs that exercise different branches.
 *
 * @origin KNARR_GALETTE - New class integrating Knarr concepts with Galette.
 *         This class is inspired by Knarr's concolic execution approach but
 *         redesigned for Galette's taint tracking system. Key features:
 *         <ul>
 *           <li>exploreInteger() - Single-variable path exploration</li>
 *           <li>exploreMultipleIntegers() - Multi-variable path exploration</li>
 *           <li>Uses Z3ConstraintSolver for constraint solving</li>
 *           <li>Supports domain-bounded exploration (e.g., 5 values = 5 paths)</li>
 *           <li>Tracks path coverage to avoid redundant exploration</li>
 *         </ul>
 *         Unlike original Knarr which uses server-client architecture for solving,
 *         this uses direct Z3 integration via z3-turnkey.
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
    private final Set<String> exploredInputSignatures = new HashSet<>();
    private final List<Expression> negatedSwitchConstraints = new ArrayList<>();
    private Expression domainConstraint = null;

    // If-based multi-variable exploration: stable variable ordering by first appearance
    // Maps qualified variable name → index in currentInputsList (insertion-ordered)
    private final java.util.LinkedHashMap<String, Integer> varNameToIndex = new java.util.LinkedHashMap<>();

    // DART-style DFS worklist for multi-variable exploration.
    // Each entry represents an unexplored negation point in the execution tree.
    private final Deque<NegationPoint> explorationWorklist = new ArrayDeque<>();
    private final Set<String> exploredPrefixes = new HashSet<>();

    /**
     * Represents a negation point in the DART-style DFS exploration.
     * A negation point is: "keep prefix constraints, negate the branch at position k."
     */
    private static class NegationPoint {
        final List<Expression> prefixConstraints;
        final Expression negatedBranch;

        NegationPoint(List<Expression> prefix, Expression negated) {
            this.prefixConstraints = new ArrayList<>(prefix);
            this.negatedBranch = negated;
        }

        String signature() {
            StringBuilder sb = new StringBuilder();
            for (Expression e : prefixConstraints) sb.append(e).append(";");
            sb.append("NEG:").append(negatedBranch);
            return sb.toString();
        }

        @Override
        public String toString() {
            return "NegationPoint{prefix=" + prefixConstraints.size() + " constraints, neg=" + negatedBranch + "}";
        }
    }

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
                System.err.println("[PathExplorer:exploreInteger] WARNING: No constraints collected — "
                        + "execution was fully concrete (value=" + currentInput + "). Symbolic tracking may be lost. "
                        + "Blindly incrementing as fallback.");
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
                            "[PathExplorer:generateNextInput] Z3 found value under key: " + key + " = " + value);
                break;
            }
        }

        // If still no value, something went wrong
        if (value == null) {
            System.err.println("[PathExplorer:generateNextInput] WARNING: Z3 returned SAT but no value could be "
                    + "extracted. Available keys: " + solution.getLabels()
                    + ". Expected variable: " + variableName);
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
        exploredInputSignatures.clear();
        domainConstraint = null;
        varNameToIndex.clear();
        explorationWorklist.clear();
        exploredPrefixes.clear();

        int numVars = initialValues.size();
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
                System.err.println("[PathExplorer:exploreMultipleIntegers] WARNING: No constraints collected — "
                        + "execution was fully concrete. This means symbolic tracking was lost. "
                        + "Blindly incrementing first variable as fallback.");
                Map<String, Object> inputs = new HashMap<>(currentInputs);
                exploredPaths.add(new PathRecord(iteration, inputs, new ArrayList<>(), endTime - startTime));
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

            // Deduplicate by constraint signature OR by input values
            // (same inputs can produce slightly different constraint lists due to domain accumulation timing)
            String inputSignature = new TreeMap<>(currentInputs).toString();
            boolean isDuplicate = exploredConstraintSignatures.contains(constraintSignature)
                    || exploredInputSignatures.contains(inputSignature);

            if (isDuplicate) {
                if (DEBUG)
                    System.out.println("[PathExplorer:exploreMultipleIntegers] Duplicate path (inputs=" + inputSignature
                            + ") — skipping, trying next worklist entry");
                // Don't break — pop the next worklist entry instead.
                // Pass empty constraints so no new negation points are pushed,
                // but the existing worklist entries from previous iterations are still tried.
                currentInputsList = generateNextMultiVarInputList(
                        Collections.emptyList(), variableNames, currentInputsList, numVars);
                if (currentInputsList == null) {
                    if (DEBUG) System.out.println("No more satisfiable inputs after duplicate - terminating");
                    break;
                }
                iteration++;
                continue;
            }

            Map<String, Object> inputs = new HashMap<>(currentInputs);
            exploredPaths.add(new PathRecord(iteration, inputs, constraints, endTime - startTime));
            exploredConstraintSignatures.add(constraintSignature);
            exploredInputSignatures.add(inputSignature);

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
                    domainConstraint = new BinaryOperation(Operator.AND, domainConstraint, c);
                } else if (DEBUG) {
                    System.out.println("[PathExplorer:extractDomainConstraints] Skipping duplicate domain constraint "
                            + "(string-based dedup): " + cStr);
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
    /**
     * Extract the primary variable name from an expression.
     * Handles both simple constraints (var op const) and compound constraints
     * from expression propagation (e.g., GT(ADD(var(a), const(5)), const(10))).
     * Recursively searches the expression tree for the first Variable.
     */
    private String extractPrimaryVarName(Expression expr) {
        if (expr instanceof Variable) return ((Variable) expr).getName();
        if (expr instanceof UnaryOperation) {
            return extractPrimaryVarName(((UnaryOperation) expr).operand);
        }
        if (!(expr instanceof BinaryOperation)) return null;
        BinaryOperation binOp = (BinaryOperation) expr;
        // Try left subtree first (most common: var or compound expression on left)
        String left = extractPrimaryVarName(binOp.left);
        if (left != null) return left;
        // Then right subtree
        return extractPrimaryVarName(binOp.right);
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
     * Generate the next input list using DART-style DFS over the execution tree.
     *
     * <p>After each execution producing branch constraints {@code [b1, b2, ..., bN]},
     * this method pushes negation points onto a worklist:
     * <ul>
     *   <li>{@code (prefix=[b1..b(N-1)], negate=NOT(bN))} — try different last branch</li>
     *   <li>{@code (prefix=[b1..b(N-2)], negate=NOT(b(N-1)))} — try different second-to-last</li>
     *   <li>etc.</li>
     * </ul>
     *
     * <p>It then pops from the worklist, builds {@code domain AND prefix AND negated_branch},
     * and sends to Z3. The prefix constraints naturally pin outer variables without any
     * per-variable grouping. Z3 solves for ALL variables simultaneously.
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

        // Step 1: Separate domain from branch constraints (preserving execution order)
        // (currentConstraints may be empty when retrying after a duplicate path — that's OK,
        //  we just skip pushing new negation points and go straight to popping the worklist)
        List<Expression> branchConstraints = new ArrayList<>();
        for (Expression c : currentConstraints) {
            if (!isDomainConstraint(c)) {
                branchConstraints.add(c);
            }
        }

        if (DEBUG) {
            System.out.println(
                    "[PathExplorer:DART] Current path has " + branchConstraints.size() + " branch constraints:");
            for (int i = 0; i < branchConstraints.size(); i++) {
                System.out.println("  [" + i + "] " + branchConstraints.get(i));
            }
        }

        // Step 2: Push negation points for the current path onto the worklist.
        // Process from shallowest (index 0) to deepest (index N-1) so that
        // the deepest ends up on top of the stack (DFS: explore deepest first).
        int pushed = 0;
        for (int k = 0; k < branchConstraints.size(); k++) {
            List<Expression> prefix = new ArrayList<>(branchConstraints.subList(0, k));
            Expression negated = ConstraintSolver.negateConstraint(branchConstraints.get(k));
            NegationPoint np = new NegationPoint(prefix, negated);

            String sig = np.signature();
            if (!exploredPrefixes.contains(sig)) {
                explorationWorklist.push(np); // push to front (DFS: deepest first)
                exploredPrefixes.add(sig);
                pushed++;
            }
        }

        if (DEBUG) {
            System.out.println("[PathExplorer:DART] Pushed " + pushed + " new negation points. " + "Worklist size: "
                    + explorationWorklist.size());
        }

        // Step 3: Pop from worklist until we find a SAT solution or exhaust all options
        while (!explorationWorklist.isEmpty()) {
            NegationPoint np = explorationWorklist.pop();

            // Build: domain AND prefix[0] AND ... AND prefix[m] AND negatedBranch
            Expression combined = domainConstraint;
            for (Expression prefixExpr : np.prefixConstraints) {
                combined = (combined == null) ? prefixExpr : new BinaryOperation(Operator.AND, combined, prefixExpr);
            }
            combined = (combined == null)
                    ? np.negatedBranch
                    : new BinaryOperation(Operator.AND, combined, np.negatedBranch);

            if (DEBUG) {
                System.out.println("[PathExplorer:DART] Trying: " + np);
                System.out.println("[PathExplorer:DART] Solver query: " + combined);
            }

            InputSolution solution = Z3ConstraintSolver.solveConstraintWithZ3(combined);

            if (solution != null && solution.isSatisfiable()) {
                // SAT: extract values for all variables
                List<Integer> result = new ArrayList<>(previousInputs);
                while (result.size() < numVars) result.add(0);

                boolean anyMapped = false;
                for (String key : solution.getLabels()) {
                    Object value = solution.getValue(key);
                    Integer idx = varNameToIndex.get(key);
                    if (idx != null && value instanceof Number && idx < result.size()) {
                        result.set(idx, ((Number) value).intValue());
                        anyMapped = true;
                        if (DEBUG) {
                            System.out.println(
                                    "[PathExplorer:DART] Set result[" + idx + "] (" + key + ") = " + result.get(idx));
                        }
                    } else {
                        System.err.println("[PathExplorer:DART] WARNING: Z3 returned variable '" + key
                                + "' = " + value + " but could not map to input index. "
                                + "Known variables: " + varNameToIndex.keySet());
                    }
                }
                if (!anyMapped) {
                    System.err.println("[PathExplorer:DART] ERROR: Z3 returned SAT but NO variables "
                            + "could be mapped to inputs. Solution: " + solution);
                }

                if (DEBUG) {
                    System.out.println("[PathExplorer:DART] SAT! Next inputs: " + result + ". Remaining worklist: "
                            + explorationWorklist.size());
                }
                return result;
            }

            if (DEBUG) {
                System.out.println("[PathExplorer:DART] UNSAT, trying next worklist entry... ("
                        + explorationWorklist.size() + " remaining)");
            }
        }

        // Worklist exhausted: all reachable paths explored
        if (DEBUG) {
            System.out.println("[PathExplorer:DART] Worklist empty — all paths explored");
        }
        return null;
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
            System.err.println("[PathExplorer:extractTagFromValue] WARNING: Error extracting tag from value " + selected
                    + ": " + e.getMessage());
        }
        return null;
    }
}
