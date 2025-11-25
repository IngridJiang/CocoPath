package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.vitruvius.AutomaticSymbolicExecutor.DecisionPoint;
import edu.neu.ccs.prl.galette.vitruvius.AutomaticSymbolicExecutor.ExecutionPath;
import java.util.*;
import za.ac.sun.cs.green.expr.*;

/**
 * Visualization and output formatting for symbolic execution results.
 *
 * Provides:
 * 1. Clear, concise result summaries
 * 2. Execution tree visualization (ASCII art)
 * 3. Constraint analysis with readable formatting
 * 4. Path coverage reports
 *
 * @purpose Visualization of symbolic execution paths
 * @feature Generates execution tree diagrams
 * @feature PNG/SVG output formats
 * @author Galette-Vitruvius Integration
 */
public class PathVisualization {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final boolean USE_COLORS =
            !System.getProperty("os.name").toLowerCase().contains("win");

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
     * Tree node representing an execution path.
     */
    public static class PathTreeNode {
        public final String label;
        public final String condition;
        public final List<PathTreeNode> children;
        public final int pathId;
        public final boolean isLeaf;

        public PathTreeNode(String label, String condition, int pathId, boolean isLeaf) {
            this.label = label;
            this.condition = condition;
            this.pathId = pathId;
            this.isLeaf = isLeaf;
            this.children = new ArrayList<>();
        }

        public void addChild(PathTreeNode child) {
            children.add(child);
        }
    }

    /**
     * Generate concise summary of symbolic execution results.
     *
     * @param paths List of explored execution paths
     * @return Formatted summary string
     */
    public static String generateSummary(List<ExecutionPath> paths) {
        if (paths.isEmpty()) {
            return "No execution paths explored.";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("\n").append(boxLine("SYMBOLIC EXECUTION SUMMARY", '═')).append("\n\n");

        // Overall statistics
        summary.append(colored("📊 STATISTICS", ANSI_CYAN, true)).append("\n");
        summary.append(String.format("  Total paths explored:     %d%n", paths.size()));
        summary.append(String.format("  Total execution time:     %d ms%n", totalExecutionTime(paths)));
        summary.append(String.format("  Average time per path:    %.2f ms%n", avgExecutionTime(paths)));
        summary.append(String.format("  Total constraints:        %d%n", totalConstraints(paths)));
        summary.append("\n");

        // Path-by-path results
        summary.append(colored("🛤️  EXPLORED PATHS", ANSI_BLUE, true)).append("\n\n");
        for (ExecutionPath path : paths) {
            summary.append(formatPath(path));
            summary.append("\n");
        }

        // Decision summary
        summary.append(colored("🎯 DECISION POINTS", ANSI_YELLOW, true)).append("\n");
        List<DecisionPoint> decisions = AutomaticSymbolicExecutor.discoverDecisionPoints();
        if (decisions.isEmpty()) {
            summary.append("  No decision points discovered (linear execution)\n");
        } else {
            for (DecisionPoint dp : decisions) {
                summary.append(
                        String.format("  %s: %s → %d branches%n", dp.variable, dp.condition, dp.possibleValues.size()));
                for (Object value : dp.possibleValues) {
                    summary.append(String.format("    • %s%n", value));
                }
            }
        }
        summary.append("\n");

        summary.append(boxLine("", '═')).append("\n");
        return summary.toString();
    }

    /**
     * Format individual path information.
     */
    private static String formatPath(ExecutionPath path) {
        StringBuilder sb = new StringBuilder();

        sb.append(colored(String.format("Path %d", path.pathId), ANSI_GREEN, true));
        sb.append(String.format(" (%d ms)%n", path.executionTime));

        // Inputs
        if (!path.symbolicInputs.isEmpty()) {
            sb.append("  Inputs: ");
            sb.append(formatMap(path.symbolicInputs));
            sb.append("\n");
        }

        // Constraints
        if (!path.constraints.isEmpty()) {
            sb.append("  Constraints:\n");
            for (Expression constraint : path.constraints) {
                String readable = makeConstraintReadable(constraint);
                sb.append("    ✓ ").append(readable).append("\n");
            }
        } else {
            sb.append("  Constraints: None (concrete execution)\n");
        }

        // Outputs
        if (!path.outputs.isEmpty() && path.outputs.containsKey("result")) {
            Object result = path.outputs.get("result");
            sb.append("  Result: ").append(formatValue(result)).append("\n");
        }

        return sb.toString();
    }

    /**
     * Make constraint expression more readable.
     */
    private static String makeConstraintReadable(Expression expr) {
        if (expr instanceof Operation) {
            Operation op = (Operation) expr;
            String operator = operatorToSymbol(op.getOperator());

            if (op.getArity() == 2) {
                String left = operandToString(op.getOperand(0));
                String right = operandToString(op.getOperand(1));
                return String.format("%s %s %s", left, operator, right);
            } else if (op.getArity() == 1) {
                String operand = operandToString(op.getOperand(0));
                return String.format("%s %s", operator, operand);
            }
        }
        return expr.toString();
    }

    /**
     * Convert operator to readable symbol.
     */
    private static String operatorToSymbol(Operation.Operator op) {
        switch (op) {
            case EQ:
                return "==";
            case NE:
                return "!=";
            case LT:
                return "<";
            case LE:
                return "<=";
            case GT:
                return ">";
            case GE:
                return ">=";
            case AND:
                return "&&";
            case OR:
                return "||";
            case NOT:
                return "!";
            case ADD:
                return "+";
            case SUB:
                return "-";
            case MUL:
                return "*";
            case DIV:
                return "/";
            default:
                return op.toString();
        }
    }

    /**
     * Convert operand to readable string.
     */
    private static String operandToString(Expression operand) {
        if (operand instanceof Variable) {
            return ((Variable) operand).getName();
        } else if (operand instanceof IntConstant) {
            return String.valueOf(((IntConstant) operand).getValueLong());
        } else if (operand instanceof RealConstant) {
            return operand.toString(); // RealConstant doesn't have simple getter, use toString()
        } else if (operand instanceof StringConstant) {
            return "\"" + operand.toString() + "\"";
        } else if (operand instanceof Operation) {
            return makeConstraintReadable(operand);
        }
        return operand.toString();
    }

    /**
     * Generate ASCII tree visualization of execution paths.
     *
     * @param paths List of explored execution paths
     * @return ASCII tree string
     */
    public static String generateTree(List<ExecutionPath> paths) {
        if (paths.isEmpty()) {
            return "No paths to visualize.";
        }

        StringBuilder tree = new StringBuilder();
        tree.append("\n").append(boxLine("EXECUTION TREE", '─')).append("\n\n");

        // Build tree from paths
        PathTreeNode root = buildTree(paths);

        // Render tree
        tree.append("ROOT\n");
        renderTree(root, "", true, tree);

        tree.append("\n").append(boxLine("", '─')).append("\n");
        return tree.toString();
    }

    /**
     * Build tree structure from execution paths.
     */
    private static PathTreeNode buildTree(List<ExecutionPath> paths) {
        PathTreeNode root = new PathTreeNode("ROOT", "", -1, false);

        // Group paths by decision variables
        Map<String, List<ExecutionPath>> groupedPaths = new HashMap<>();

        for (ExecutionPath path : paths) {
            String groupKey = extractGroupKey(path);
            groupedPaths.putIfAbsent(groupKey, new ArrayList<>());
            groupedPaths.get(groupKey).add(path);
        }

        // Create branches
        int branchId = 0;
        for (Map.Entry<String, List<ExecutionPath>> entry : groupedPaths.entrySet()) {
            String condition = entry.getKey();
            List<ExecutionPath> branchPaths = entry.getValue();

            for (ExecutionPath path : branchPaths) {
                String label = extractPathLabel(path);
                PathTreeNode child = new PathTreeNode(label, condition, path.pathId, true);
                root.addChild(child);
            }
        }

        return root;
    }

    /**
     * Extract group key from path (first constraint variable).
     */
    private static String extractGroupKey(ExecutionPath path) {
        if (path.constraints.isEmpty()) {
            return "concrete";
        }
        Expression firstConstraint = path.constraints.get(0);
        return makeConstraintReadable(firstConstraint);
    }

    /**
     * Extract readable label from path.
     */
    private static String extractPathLabel(ExecutionPath path) {
        if (path.constraints.isEmpty()) {
            return "Concrete execution";
        }

        StringBuilder label = new StringBuilder();
        for (int i = 0; i < Math.min(2, path.constraints.size()); i++) {
            if (i > 0) label.append(", ");
            label.append(makeConstraintReadable(path.constraints.get(i)));
        }
        if (path.constraints.size() > 2) {
            label.append(String.format(", ... (+%d more)", path.constraints.size() - 2));
        }
        return label.toString();
    }

    /**
     * Render tree using ASCII box-drawing characters.
     */
    private static void renderTree(PathTreeNode node, String prefix, boolean isLast, StringBuilder sb) {
        for (int i = 0; i < node.children.size(); i++) {
            PathTreeNode child = node.children.get(i);
            boolean isChildLast = (i == node.children.size() - 1);

            sb.append(prefix);
            sb.append(isChildLast ? "└── " : "├── ");

            // Color the path
            String pathColor = child.isLeaf ? ANSI_GREEN : ANSI_BLUE;
            sb.append(colored(child.label, pathColor, false));
            sb.append(String.format(" [Path %d]", child.pathId));
            sb.append("\n");

            // Recursively render children
            String newPrefix = prefix + (isChildLast ? "    " : "│   ");
            renderTree(child, newPrefix, isChildLast, sb);
        }
    }

    /**
     * Generate detailed constraint analysis.
     */
    public static String analyzeConstraints(List<ExecutionPath> paths) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("\n").append(boxLine("CONSTRAINT ANALYSIS", '─')).append("\n\n");

        Map<String, Set<Expression>> variableConstraints = new HashMap<>();

        // Group constraints by variable
        for (ExecutionPath path : paths) {
            for (Expression constraint : path.constraints) {
                Set<String> variables = extractVariables(constraint);
                for (String var : variables) {
                    variableConstraints.putIfAbsent(var, new HashSet<>());
                    variableConstraints.get(var).add(constraint);
                }
            }
        }

        if (variableConstraints.isEmpty()) {
            analysis.append("No constraints to analyze.\n");
        } else {
            for (Map.Entry<String, Set<Expression>> entry : variableConstraints.entrySet()) {
                analysis.append(colored("Variable: " + entry.getKey(), ANSI_CYAN, true))
                        .append("\n");
                for (Expression expr : entry.getValue()) {
                    analysis.append("  • ").append(makeConstraintReadable(expr)).append("\n");
                }
                analysis.append("\n");
            }
        }

        analysis.append(boxLine("", '─')).append("\n");
        return analysis.toString();
    }

    /**
     * Extract all variable names from expression.
     */
    private static Set<String> extractVariables(Expression expr) {
        Set<String> variables = new HashSet<>();
        extractVariablesRecursive(expr, variables);
        return variables;
    }

    /**
     * Recursively extract variables.
     */
    private static void extractVariablesRecursive(Expression expr, Set<String> variables) {
        if (expr instanceof Variable) {
            variables.add(((Variable) expr).getName());
        } else if (expr instanceof Operation) {
            Operation op = (Operation) expr;
            for (int i = 0; i < op.getArity(); i++) {
                extractVariablesRecursive(op.getOperand(i), variables);
            }
        }
    }

    /**
     * Format map for display.
     */
    private static String formatMap(Map<String, Object> map) {
        if (map.isEmpty()) return "{}";
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            joiner.add(entry.getKey() + "=" + formatValue(entry.getValue()));
        }
        return joiner.toString();
    }

    /**
     * Format value for display.
     */
    private static String formatValue(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + value + "\"";
        return value.toString();
    }

    /**
     * Calculate total execution time.
     */
    private static long totalExecutionTime(List<ExecutionPath> paths) {
        return paths.stream().mapToLong(p -> p.executionTime).sum();
    }

    /**
     * Calculate average execution time.
     */
    private static double avgExecutionTime(List<ExecutionPath> paths) {
        if (paths.isEmpty()) return 0.0;
        return (double) totalExecutionTime(paths) / paths.size();
    }

    /**
     * Count total constraints across all paths.
     */
    private static int totalConstraints(List<ExecutionPath> paths) {
        return paths.stream().mapToInt(p -> p.constraints.size()).sum();
    }

    /**
     * Create colored text (if terminal supports it).
     */
    private static String colored(String text, String color, boolean bold) {
        if (!USE_COLORS) return text;
        String boldCode = bold ? "\u001B[1m" : "";
        return boldCode + color + text + ANSI_RESET;
    }

    /**
     * Create box line with centered text.
     */
    private static String boxLine(String text, char lineChar) {
        int width = 70;
        if (text.isEmpty()) {
            return repeatString(String.valueOf(lineChar), width);
        }
        int padding = (width - text.length() - 2) / 2;
        String line = String.valueOf(lineChar);
        return repeatString(line, padding) + " " + text + " " + repeatString(line, width - padding - text.length() - 2);
    }

    /**
     * Generate complete report with all visualizations.
     */
    public static String generateCompleteReport(List<ExecutionPath> paths) {
        StringBuilder report = new StringBuilder();

        report.append(generateSummary(paths));
        report.append("\n");
        report.append(generateTree(paths));
        report.append("\n");
        report.append(analyzeConstraints(paths));

        return report.toString();
    }

    /**
     * Print report to console.
     */
    public static void printReport(List<ExecutionPath> paths) {
        System.out.println(generateCompleteReport(paths));
    }

    /**
     * Export execution paths to JSON format for Python visualization.
     *
     * @param paths List of explored execution paths
     * @param outputFile Path to output JSON file
     */
    public static void exportToJson(List<ExecutionPath> paths, String outputFile) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter(outputFile);
            writer.write(generateJson(paths));
            writer.close();
            System.out.println("Exported execution paths to: " + outputFile);
            System.out.println("Run: python visualize_tree.py " + outputFile);
        } catch (java.io.IOException e) {
            System.err.println("Failed to export JSON: " + e.getMessage());
        }
    }

    /**
     * Generate JSON representation of execution paths.
     */
    private static String generateJson(List<ExecutionPath> paths) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"paths\": [\n");

        for (int i = 0; i < paths.size(); i++) {
            ExecutionPath path = paths.get(i);
            json.append("    {\n");
            json.append("      \"pathId\": ").append(path.pathId).append(",\n");
            json.append("      \"executionTime\": ").append(path.executionTime).append(",\n");

            // Inputs
            json.append("      \"inputs\": {");
            boolean firstInput = true;
            for (Map.Entry<String, Object> entry : path.symbolicInputs.entrySet()) {
                if (!firstInput) json.append(", ");
                json.append("\"").append(entry.getKey()).append("\": ");
                json.append(jsonValue(entry.getValue()));
                firstInput = false;
            }
            json.append("},\n");

            // Constraints
            json.append("      \"constraints\": [");
            for (int j = 0; j < path.constraints.size(); j++) {
                if (j > 0) json.append(", ");
                json.append("\"")
                        .append(escapeJson(makeConstraintReadable(path.constraints.get(j))))
                        .append("\"");
            }
            json.append("],\n");

            // Outputs
            json.append("      \"outputs\": {");
            boolean firstOutput = true;
            for (Map.Entry<String, Object> entry : path.outputs.entrySet()) {
                if (!firstOutput) json.append(", ");
                json.append("\"").append(entry.getKey()).append("\": ");
                json.append("\"")
                        .append(escapeJson(String.valueOf(entry.getValue())))
                        .append("\"");
                firstOutput = false;
            }
            json.append("}\n");

            json.append("    }");
            if (i < paths.size() - 1) json.append(",");
            json.append("\n");
        }

        json.append("  ],\n");

        // Decision points
        json.append("  \"decisionPoints\": [\n");
        List<DecisionPoint> decisions = AutomaticSymbolicExecutor.discoverDecisionPoints();
        for (int i = 0; i < decisions.size(); i++) {
            DecisionPoint dp = decisions.get(i);
            json.append("    {\n");
            json.append("      \"variable\": \"").append(dp.variable).append("\",\n");
            json.append("      \"condition\": \"")
                    .append(escapeJson(dp.condition))
                    .append("\",\n");
            json.append("      \"possibleValues\": [");
            for (int j = 0; j < dp.possibleValues.size(); j++) {
                if (j > 0) json.append(", ");
                json.append(jsonValue(dp.possibleValues.get(j)));
            }
            json.append("]\n");
            json.append("    }");
            if (i < decisions.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ]\n");

        json.append("}\n");
        return json.toString();
    }

    /**
     * Convert value to JSON representation.
     */
    private static String jsonValue(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "\"" + escapeJson((String) value) + "\"";
        if (value instanceof Number) return value.toString();
        if (value instanceof Boolean) return value.toString();
        return "\"" + escapeJson(value.toString()) + "\"";
    }

    /**
     * Escape special characters for JSON.
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
