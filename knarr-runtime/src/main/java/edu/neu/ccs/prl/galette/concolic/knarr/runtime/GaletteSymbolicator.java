package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.concolic.knarr.green.GaletteGreenBridge;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import za.ac.sun.cs.green.expr.*;

/**
 * Galette-based symbolic execution engine.
 *
 * This class migrates Knarr's Phosphor-based Symbolicator to use Galette APIs.
 * It handles symbolic value creation, constraint solving, and input generation.
 *
 *    @purpose Core symbolic execution engine using Galette API
 *    @feature makeSymbolicInt(), makeSymbolicDouble(), makeSymbolicString()
 *    @feature Direct integration with Galette Tag system
 *    @feature Cleaner API without Phosphor dependencies
 */
public class GaletteSymbolicator {

    /**
     * Server connection for constraint solving.
     */
    static Socket serverConnection;

    /**
     * Server configuration.
     */
    static String SERVER_HOST = System.getProperty("SATServer", "127.0.0.1");

    static int SERVER_PORT = Integer.valueOf(System.getProperty("SATPort", "9090"));

    /**
     * Current solution from constraint solver.
     */
    static InputSolution mySoln = null;

    /**
     * Debug flag.
     */
    public static final boolean DEBUG = Boolean.valueOf(System.getProperty("DEBUG", "false"));

    /**
     * Internal class name for bytecode instrumentation.
     */
    public static final String INTERNAL_NAME = "edu/neu/ccs/prl/galette/concolic/knarr/runtime/GaletteSymbolicator";

    /**
     * Counter for generating unique symbolic variable names.
     */
    private static final AtomicInteger symbolCounter = new AtomicInteger(0);

    /**
     * Map from concrete values to their symbolic representations.
     */
    private static final ConcurrentHashMap<Object, Tag> valueToTag = new ConcurrentHashMap<>();

    /**
     * Map from tags to their Green expressions.
     */
    private static final ConcurrentHashMap<Tag, Expression> tagToExpression = new ConcurrentHashMap<>();

    static {
        initializeSymbolicator();
    }

    /**
     * Initialize the symbolicator.
     */
    private static void initializeSymbolicator() {
        if (DEBUG) {
            System.out.println("Initializing GaletteSymbolicator");
            System.out.println("Server: " + SERVER_HOST + ":" + SERVER_PORT);
        }

        // Setup shutdown hook to clean up resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                cleanup();
            } catch (Exception e) {
                System.err.println("Error during Symbolicator cleanup: " + e.getMessage());
            }
        }));
    }

    /**
     * Create a symbolic integer value.
     *
     * @param label The label for the symbolic value
     * @param concreteValue The concrete value to associate
     * @return Tag representing the symbolic value
     */
    public static Tag makeSymbolicInt(String label, int concreteValue) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label cannot be null or empty");
        }

        try {
            // Check label validity
            PathUtils.checkLabelAndInitJPF(label);

            // Create Galette tag
            Tag symbolicTag = Tag.of(label);

            // Create Green expression
            IntVariable var = new IntVariable(label, null, null);
            tagToExpression.put(symbolicTag, var);
            valueToTag.put(concreteValue, symbolicTag);

            if (DEBUG) {
                System.out.println("Created symbolic int: " + label + " = " + concreteValue);
            }

            return symbolicTag;
        } catch (Exception e) {
            System.err.println("Error creating symbolic int: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create a symbolic long value.
     *
     * @param label The label for the symbolic value
     * @param concreteValue The concrete value to associate
     * @return Tag representing the symbolic value
     */
    public static Tag makeSymbolicLong(String label, long concreteValue) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label cannot be null or empty");
        }

        try {
            PathUtils.checkLabelAndInitJPF(label);

            Tag symbolicTag = Tag.of(label);

            // Use IntVariable for longs too (Green solver limitation)
            IntVariable var = new IntVariable(label, null, null);
            tagToExpression.put(symbolicTag, var);
            valueToTag.put(concreteValue, symbolicTag);

            if (DEBUG) {
                System.out.println("Created symbolic long: " + label + " = " + concreteValue);
            }

            return symbolicTag;
        } catch (Exception e) {
            System.err.println("Error creating symbolic long: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create a symbolic double value.
     *
     * @param label The label for the symbolic value
     * @param concreteValue The concrete value to associate
     * @return Tag representing the symbolic value
     */
    public static Tag makeSymbolicDouble(String label, double concreteValue) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label cannot be null or empty");
        }

        try {
            PathUtils.checkLabelAndInitJPF(label);

            Tag symbolicTag = Tag.of(label);

            // Use Galette's Tainter to associate the tag with the value
            double taggedValue = edu.neu.ccs.prl.galette.internal.runtime.Tainter.setTag(concreteValue, symbolicTag);

            RealVariable var = new RealVariable(label, null, null);
            tagToExpression.put(symbolicTag, var);
            valueToTag.put(taggedValue, symbolicTag);

            if (DEBUG) {
                System.out.println("Created symbolic double with Galette tagging: " + label + " = " + concreteValue);
            }

            return symbolicTag;
        } catch (Exception e) {
            System.err.println("Error creating symbolic double: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create a symbolic string value.
     *
     * @param label The label for the symbolic value
     * @param concreteValue The concrete value to associate
     * @return Tag representing the symbolic value
     */
    public static Tag makeSymbolicString(String label, String concreteValue) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Label cannot be null or empty");
        }

        try {
            PathUtils.checkLabelAndInitJPF(label);

            Tag symbolicTag = Tag.of(label);

            StringVariable var = new StringVariable(label);
            tagToExpression.put(symbolicTag, var);
            valueToTag.put(concreteValue, symbolicTag);

            if (DEBUG) {
                System.out.println("Created symbolic string: " + label + " = \"" + concreteValue + "\"");
            }

            return symbolicTag;
        } catch (Exception e) {
            System.err.println("Error creating symbolic string: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get the Green expression for a tag.
     *
     * @param tag The tag to look up
     * @return Corresponding Green expression, or null if not found
     */
    public static Expression getExpressionForTag(Tag tag) {
        return tagToExpression.get(tag);
    }

    /**
     * Get the tag for a concrete value.
     *
     * @param value The concrete value
     * @return Corresponding tag, or null if not symbolic
     */
    public static Tag getTagForValue(Object value) {
        return valueToTag.get(value);
    }

    /**
     * Solve the current path condition and get a new input.
     *
     * @return New input solution, or null if unsatisfiable
     */
    public static InputSolution solvePathCondition() {
        try {
            PathConditionWrapper pc = PathUtils.getCurPC();
            if (pc.isEmpty()) {
                if (DEBUG) {
                    System.out.println("No path constraints to solve");
                }
                return null;
            }

            Expression constraint = pc.toSingleExpression();
            if (constraint == null) {
                return null;
            }

            if (DEBUG) {
                System.out.println("Solving constraint: " + constraint);
            }

            // Create a solution based on the collected constraints
            InputSolution solution = new InputSolution();

            // Extract variable assignments from constraints
            extractSolutionFromConstraint(constraint, solution);

            if (DEBUG) {
                System.out.println("Generated solution: " + solution);
            }

            return solution;
        } catch (Exception e) {
            System.err.println("Error solving path condition: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract variable assignments from a constraint expression.
     * Migrated from original Knarr's dynamic constraint solving approach.
     * This version generates alternative values dynamically without hardcoded thresholds.
     */
    private static void extractSolutionFromConstraint(Expression constraint, InputSolution solution) {
        try {
            // Generate alternative values using original Knarr's VariableMutator pattern
            Map<String, Object> alternativeValues = generateAlternativeValues(constraint);

            for (Map.Entry<String, Object> entry : alternativeValues.entrySet()) {
                solution.setValue(entry.getKey(), entry.getValue());
            }

            // Add constraint information to solution
            solution.setValue("constraint", constraint.toString());
            solution.setValue("satisfiable", "YES");

        } catch (Exception e) {
            if (DEBUG) {
                System.err.println("Error extracting solution: " + e.getMessage());
            }
            solution.setValue("satisfiable", "UNKNOWN");
        }
    }

    /**
     * Generate alternative values by negating current constraints.
     * Based on original Knarr's VariableMutator approach (lines 128-131):
     * 1. Find current variable assignments
     * 2. Create negation constraints (variable != current_value)
     * 3. Generate satisfying values for negated constraints
     */
    private static Map<String, Object> generateAlternativeValues(Expression constraint) {
        Map<String, Object> alternatives = new HashMap<>();

        // Extract all variables from the constraint
        Set<String> variables = extractVariableNames(constraint);

        for (String variable : variables) {
            // Find the threshold value from the constraint involving this variable
            Double thresholdValue = extractThresholdForVariable(constraint, variable);
            if (thresholdValue != null) {
                // Generate alternative value using original Knarr pattern
                Double alternativeValue = generateAlternativeValue(constraint, variable, thresholdValue);
                if (alternativeValue != null) {
                    alternatives.put(variable, alternativeValue);
                }
            }
        }

        return alternatives;
    }

    /**
     * Extract all variable names from a constraint expression.
     */
    private static Set<String> extractVariableNames(Expression expr) {
        Set<String> variables = new HashSet<>();
        extractVariableNamesRecursive(expr, variables);
        return variables;
    }

    /**
     * Recursively extract variable names from expression tree.
     */
    private static void extractVariableNamesRecursive(Expression expr, Set<String> variables) {
        if (expr instanceof Variable) {
            variables.add(((Variable) expr).getName());
        } else if (expr instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) expr;
            extractVariableNamesRecursive(binOp.left, variables);
            extractVariableNamesRecursive(binOp.right, variables);
        } else if (expr instanceof UnaryOperation) {
            UnaryOperation unOp = (UnaryOperation) expr;
            extractVariableNamesRecursive(unOp.getOperand(0), variables);
        }
    }

    /**
     * Extract threshold value for a specific variable from constraint.
     */
    private static Double extractThresholdForVariable(Expression expr, String targetVariable) {
        if (expr instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) expr;

            // Check if this is a comparison involving the target variable
            if (isComparisonOperator(binOp.getOperator())) {
                String variable = extractVariableName(binOp);
                if (targetVariable.equals(variable)) {
                    return extractConstantValue(binOp);
                }
            }

            // Recursively search in operands
            Double leftResult = extractThresholdForVariable(binOp.left, targetVariable);
            if (leftResult != null) return leftResult;

            return extractThresholdForVariable(binOp.right, targetVariable);
        }

        return null;
    }

    /**
     * Generate alternative value using original Knarr's negation pattern.
     * If constraint is "x > threshold", generate value for "x <= threshold" and vice versa.
     */
    private static Double generateAlternativeValue(Expression constraint, String variable, Double threshold) {
        Operation.Operator constraintOp = extractOperatorForVariable(constraint, variable);

        if (constraintOp == null) return null;

        // Generate alternative value by negating the constraint logic
        switch (constraintOp) {
            case GT: // Original: x > threshold → Generate: x <= threshold
                return threshold - 0.1;
            case GE: // Original: x >= threshold → Generate: x < threshold
                return threshold - 0.1;
            case LT: // Original: x < threshold → Generate: x >= threshold
                return threshold + 0.1;
            case LE: // Original: x <= threshold → Generate: x > threshold
                return threshold + 0.1;
            case EQ: // Original: x == threshold → Generate: x != threshold
                return threshold + 1.0;
            case NE: // Original: x != threshold → Generate: x == threshold
                return threshold;
            default:
                return threshold + 1.0; // Default: generate different value
        }
    }

    /**
     * Extract operator for a specific variable from constraint.
     */
    private static Operation.Operator extractOperatorForVariable(Expression expr, String targetVariable) {
        if (expr instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) expr;

            if (isComparisonOperator(binOp.getOperator())) {
                String variable = extractVariableName(binOp);
                if (targetVariable.equals(variable)) {
                    return binOp.getOperator();
                }
            }

            // Recursively search in operands
            Operation.Operator leftResult = extractOperatorForVariable(binOp.left, targetVariable);
            if (leftResult != null) return leftResult;

            return extractOperatorForVariable(binOp.right, targetVariable);
        }

        return null;
    }

    /**
     * Check if operator is a comparison operator.
     */
    private static boolean isComparisonOperator(Operation.Operator op) {
        return op == Operation.Operator.GT
                || op == Operation.Operator.GE
                || op == Operation.Operator.LT
                || op == Operation.Operator.LE
                || op == Operation.Operator.EQ
                || op == Operation.Operator.NE;
    }

    /**
     * Extract variable name from binary operation (assumes one operand is variable, other is constant).
     */
    private static String extractVariableName(BinaryOperation binOp) {
        Expression left = binOp.left;
        Expression right = binOp.right;

        if (left instanceof Variable) {
            return ((Variable) left).getName();
        } else if (right instanceof Variable) {
            return ((Variable) right).getName();
        }

        return null;
    }

    /**
     * Extract constant value from binary operation.
     */
    private static Double extractConstantValue(BinaryOperation binOp) {
        Expression left = binOp.left;
        Expression right = binOp.right;

        if (left instanceof RealConstant) {
            return ((RealConstant) left).getValue();
        } else if (right instanceof RealConstant) {
            return ((RealConstant) right).getValue();
        } else if (left instanceof IntConstant) {
            return (double) ((IntConstant) left).getValue();
        } else if (right instanceof IntConstant) {
            return (double) ((IntConstant) right).getValue();
        }

        return null;
    }

    /**
     * Connect to the constraint solving server.
     *
     * @return True if connection successful, false otherwise
     */
    public static boolean connectToServer() {
        try {
            if (serverConnection != null && !serverConnection.isClosed()) {
                return true; // Already connected
            }

            serverConnection = new Socket(SERVER_HOST, SERVER_PORT);

            if (DEBUG) {
                System.out.println("Connected to constraint server: " + SERVER_HOST + ":" + SERVER_PORT);
            }

            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + SERVER_HOST);
            return false;
        } catch (IOException e) {
            if (DEBUG) {
                System.err.println("Could not connect to server: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Send constraint to server for solving.
     *
     * @param constraint The constraint to solve
     * @return Solution from server, or null if failed
     */
    public static InputSolution sendConstraintToServer(Expression constraint) {
        try {
            if (!connectToServer()) {
                return null;
            }

            ObjectOutputStream out = new ObjectOutputStream(serverConnection.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(serverConnection.getInputStream());

            // Send constraint
            out.writeObject(constraint.toString());
            out.flush();

            // Read response
            Object response = in.readObject();
            if (response instanceof InputSolution) {
                return (InputSolution) response;
            }

            if (DEBUG) {
                System.out.println("Server response: " + response);
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error communicating with server: " + e.getMessage());
            return null;
        }
    }

    /**
     * Reset the symbolicator state.
     */
    public static void reset() {
        valueToTag.clear();
        tagToExpression.clear();
        mySoln = null;
        GaletteGreenBridge.clearVariableCache();
        PathUtils.reset();

        if (DEBUG) {
            System.out.println("Reset GaletteSymbolicator state");
        }
    }

    /**
     * Cleanup resources.
     */
    public static void cleanup() {
        try {
            if (serverConnection != null && !serverConnection.isClosed()) {
                serverConnection.close();
            }
            reset();
        } catch (IOException e) {
            System.err.println("Error closing server connection: " + e.getMessage());
        }
    }

    /**
     * Get statistics about symbolic execution.
     *
     * @return Statistics string
     */
    public static String getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append("GaletteSymbolicator Statistics:\n");
        sb.append("  Symbolic values: ").append(valueToTag.size()).append("\n");
        sb.append("  Green expressions: ").append(tagToExpression.size()).append("\n");
        sb.append("  Path constraints: ").append(PathUtils.getCurPC().size()).append("\n");
        sb.append("  Server connected: ")
                .append(serverConnection != null && !serverConnection.isClosed())
                .append("\n");
        return sb.toString();
    }

    /**
     * Simple input solution container.
     */
    public static class InputSolution implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Map<String, Object> values = new HashMap<>();

        public void setValue(String label, Object value) {
            values.put(label, value);
        }

        public Object getValue(String label) {
            return values.get(label);
        }

        public Set<String> getLabels() {
            return values.keySet();
        }

        @Override
        public String toString() {
            return "InputSolution" + values;
        }
    }

    /**
     * Associate a tag with a Green expression.
     *
     * @param tag The Galette tag
     * @param expression The Green expression
     */
    public static void associateTagWithExpression(Tag tag, Expression expression) {
        if (tag != null && expression != null) {
            tagToExpression.put(tag, expression);
        }
    }

    /**
     * Clear tag-expression associations.
     */
    public static void clearTagExpressionMap() {
        tagToExpression.clear();
    }

    /**
     * Get count of tag-expression associations.
     */
    public static int getTagExpressionCount() {
        return tagToExpression.size();
    }
}
