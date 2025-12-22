package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.concolic.knarr.green.GaletteGreenBridge;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Utility class for performing symbolic comparisons and collecting path constraints.
 *
 * This class provides methods to compare symbolic values while automatically
 * collecting the corresponding path constraints for concolic execution.
 *
 * @purpose Specialized comparison operations for symbolic values
 * @feature Handles ==, !=, <, >, <=, >= with constraints
 */
public class SymbolicComparison {

    /**
     * Perform a symbolic "greater than" comparison and collect path constraints.
     *
     * @param leftValue The left operand value
     * @param leftTag The symbolic tag for the left operand (can be null for concrete)
     * @param rightValue The right operand value
     * @param rightTag The symbolic tag for the right operand (can be null for concrete)
     * @return The concrete result of the comparison
     */
    public static boolean greaterThan(double leftValue, Tag leftTag, double rightValue, Tag rightTag) {
        // Perform the concrete comparison
        boolean result = leftValue > rightValue;

        // If either operand is symbolic, collect the constraint
        if (leftTag != null || rightTag != null) {
            collectComparisonConstraint(
                    leftValue, leftTag, rightValue, rightTag, result ? Operator.GT : Operator.LE, result);

            if (GaletteSymbolicator.DEBUG) {
                System.out.println("Symbolic comparison: " + leftValue + " > " + rightValue + " = " + result);
                System.out.println("Left tag: " + leftTag + ", Right tag: " + rightTag);
            }
        }

        return result;
    }

    /**
     * Perform a symbolic "greater than or equal" comparison and collect path constraints.
     */
    public static boolean greaterThanOrEqual(double leftValue, Tag leftTag, double rightValue, Tag rightTag) {
        boolean result = leftValue >= rightValue;

        if (leftTag != null || rightTag != null) {
            collectComparisonConstraint(
                    leftValue, leftTag, rightValue, rightTag, result ? Operator.GE : Operator.LT, result);
        }

        return result;
    }

    /**
     * Perform a symbolic "less than" comparison and collect path constraints.
     */
    public static boolean lessThan(double leftValue, Tag leftTag, double rightValue, Tag rightTag) {
        boolean result = leftValue < rightValue;

        if (leftTag != null || rightTag != null) {
            collectComparisonConstraint(
                    leftValue, leftTag, rightValue, rightTag, result ? Operator.LT : Operator.GE, result);
        }

        return result;
    }

    /**
     * Perform a symbolic "less than or equal" comparison and collect path constraints.
     */
    public static boolean lessThanOrEqual(double leftValue, Tag leftTag, double rightValue, Tag rightTag) {
        boolean result = leftValue <= rightValue;

        if (leftTag != null || rightTag != null) {
            collectComparisonConstraint(
                    leftValue, leftTag, rightValue, rightTag, result ? Operator.LE : Operator.GT, result);
        }

        return result;
    }

    /**
     * Perform a symbolic "equal" comparison and collect path constraints.
     */
    public static boolean equal(double leftValue, Tag leftTag, double rightValue, Tag rightTag) {
        boolean result = Double.compare(leftValue, rightValue) == 0;

        if (leftTag != null || rightTag != null) {
            collectComparisonConstraint(
                    leftValue, leftTag, rightValue, rightTag, result ? Operator.EQ : Operator.NE, result);
        }

        return result;
    }

    /**
     * Collect a comparison constraint for the current path condition.
     */
    private static void collectComparisonConstraint(
            double leftValue, Tag leftTag, double rightValue, Tag rightTag, Operator operator, boolean result) {
        try {
            // Create expressions for left and right operands
            Expression leftExpr = createExpression(leftValue, leftTag);
            Expression rightExpr = createExpression(rightValue, rightTag);

            if (leftExpr != null && rightExpr != null) {
                // Create the comparison expression based on the actual path taken
                Expression constraint = new BinaryOperation(operator, leftExpr, rightExpr);

                // Add to current path condition
                PathConditionWrapper pc = PathUtils.getCurPC();
                pc.addConstraint(constraint);

                if (GaletteSymbolicator.DEBUG) {
                    System.out.println("Added constraint: " + constraint);
                    System.out.println("Path condition now has " + pc.size() + " constraints");
                }
            }
        } catch (Exception e) {
            System.err.println("Error collecting comparison constraint: " + e.getMessage());
            if (GaletteSymbolicator.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a Green expression from a value and its tag.
     */
    private static Expression createExpression(double value, Tag tag) {
        if (tag != null && !tag.isEmpty()) {
            // Get or create symbolic expression
            Expression existing = GaletteSymbolicator.getExpressionForTag(tag);
            if (existing != null) {
                return existing;
            }

            // Create new expression from tag
            Expression tagExpr = GaletteGreenBridge.tagToExpression(tag);
            if (tagExpr != null) {
                return tagExpr;
            }
        }

        // Create concrete constant
        return new RealConstant(value);
    }

    /**
     * Helper method specifically for the thickness comparison in our example.
     * This makes it easy to replace the original comparison.
     */
    public static boolean isThicknessGreaterThan(double thickness, Tag thicknessTag, double threshold) {
        return greaterThan(thickness, thicknessTag, threshold, null);
    }

    /**
     * Create a symbolic constraint from a concrete boolean condition.
     * This is useful when you have complex conditions that need to be converted to constraints.
     */
    public static void addBooleanConstraint(boolean condition, String description) {
        try {
            // Create a simple boolean constraint
            IntConstant boolConst = new IntConstant(condition ? 1 : 0);
            IntConstant trueConst = new IntConstant(1);

            Expression constraint = new BinaryOperation(Operator.EQ, boolConst, trueConst);

            PathConditionWrapper pc = PathUtils.getCurPC();
            pc.addConstraint(constraint);

            if (GaletteSymbolicator.DEBUG) {
                System.out.println("Added boolean constraint (" + description + "): " + constraint);
            }
        } catch (Exception e) {
            System.err.println("Error adding boolean constraint: " + e.getMessage());
        }
    }

    /**
     * Handle a symbolic switch statement and record the constraint for the taken path.
     * This method is designed to be called from Vitruvius reactions or any switch-like decision point.
     *
     * @param value The concrete value being switched on
     * @param tag The symbolic tag associated with the value (can be null for concrete values)
     * @param cases All possible case values in the switch
     * @return The original value (for transparent integration)
     */
    public static int symbolicSwitch(int value, Tag tag, int... cases) {
        try {
            System.out.println("[SymbolicComparison:symbolicSwitch] called with value: " + value + ", cases: "
                    + java.util.Arrays.toString(cases));
            // If we have a symbolic tag, record the constraint
            if (tag != null && !tag.isEmpty()) {
                // Get or create symbolic expression for the tag
                Expression varExpr = GaletteGreenBridge.tagToExpression(tag);
                if (varExpr == null) {
                    // Create a new symbolic variable if needed
                    String varName = "switch_var_" + tag.toString().hashCode();
                    varExpr = new IntVariable(varName, Integer.MIN_VALUE, Integer.MAX_VALUE);
                }

                // Add domain constraint based on the cases
                if (cases.length > 0) {
                    int minCase = cases[0];
                    int maxCase = cases[0];
                    for (int c : cases) {
                        minCase = Math.min(minCase, c);
                        maxCase = Math.max(maxCase, c);
                    }

                    // Add domain constraint: min <= var <= max
                    Expression lowerBound = new BinaryOperation(Operator.GE, varExpr, new IntConstant(minCase));
                    Expression upperBound = new BinaryOperation(Operator.LE, varExpr, new IntConstant(maxCase));
                    Expression domainConstraint = new BinaryOperation(Operator.AND, lowerBound, upperBound);

                    PathConditionWrapper pc = PathUtils.getCurPC();
                    pc.addConstraint(domainConstraint);
                }

                // Add the equality constraint for the taken path
                Expression takenConstraint = new BinaryOperation(Operator.EQ, varExpr, new IntConstant(value));
                PathConditionWrapper pc = PathUtils.getCurPC();
                pc.addConstraint(takenConstraint);

                if (GaletteSymbolicator.DEBUG) {
                    System.out.println(
                            "Symbolic switch: value=" + value + ", cases=" + java.util.Arrays.toString(cases));
                    System.out.println("Added constraint: " + varExpr + " == " + value);
                }
            } else {
                // No tag, but still record as a concrete constraint using variable name inference
                String varName = "user_choice"; // Default variable name
                PathUtils.addSwitchConstraint(varName, value);

                if (GaletteSymbolicator.DEBUG) {
                    System.out.println("Concrete switch (no tag): value=" + value);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in symbolicSwitch: " + e.getMessage());
            if (GaletteSymbolicator.DEBUG) {
                e.printStackTrace();
            }
        }

        return value;
    }

    /**
     * Handle a symbolic choice (e.g., from user interaction dialog) and record constraints.
     * This is specifically designed for Vitruvius user interaction scenarios.
     *
     * @param value The chosen value
     * @param tag The symbolic tag (can be null)
     * @param min Minimum valid choice (inclusive)
     * @param max Maximum valid choice (inclusive)
     * @return The original value (for transparent integration)
     */
    public static int symbolicChoice(int value, Tag tag, int min, int max) {
        try {
            System.out.println("[SymbolicComparison:symbolicChoice] called with value: " + value + ", range: [" + min
                    + "," + max + "]");
            if (tag != null && !tag.isEmpty()) {
                // Tagged symbolic value
                Expression varExpr = GaletteGreenBridge.tagToExpression(tag);
                if (varExpr == null) {
                    String varName = "choice_var_" + tag.toString().hashCode();
                    varExpr = new IntVariable(varName, min, max);
                }

                // Add domain constraint: min <= var <= max
                Expression lowerBound = new BinaryOperation(Operator.GE, varExpr, new IntConstant(min));
                Expression upperBound = new BinaryOperation(Operator.LE, varExpr, new IntConstant(max));
                Expression domainConstraint = new BinaryOperation(Operator.AND, lowerBound, upperBound);

                // Add chosen value constraint: var == value
                Expression choiceConstraint = new BinaryOperation(Operator.EQ, varExpr, new IntConstant(value));

                PathConditionWrapper pc = PathUtils.getCurPC();
                pc.addConstraint(domainConstraint);
                pc.addConstraint(choiceConstraint);

                if (GaletteSymbolicator.DEBUG) {
                    System.out.println("Symbolic choice: value=" + value + ", range=[" + min + "," + max + "]");
                }
            } else {
                // Fallback to string-based constraint recording
                String varName = "user_choice";
                PathUtils.addIntDomainConstraint(
                        varName, min, max + 1); // max+1 because our method uses exclusive upper bound
                PathUtils.addSwitchConstraint(varName, value);

                if (GaletteSymbolicator.DEBUG) {
                    System.out.println(
                            "Concrete choice (no tag): value=" + value + ", range=[" + min + "," + max + "]");
                }
            }
        } catch (Exception e) {
            System.err.println("Error in symbolicChoice: " + e.getMessage());
            if (GaletteSymbolicator.DEBUG) {
                e.printStackTrace();
            }
        }

        return value;
    }

    /**
     * Handle a symbolic choice for use directly in Vitruvius reactions.
     * This version takes an Integer (boxed) to work with Vitruvius user interaction results.
     *
     * In the new design:
     * - Domain constraints are handled by GaletteSymbolicator.getOrMakeSymbolicInt
     * - This method only records the specific choice constraint
     *
     * @param selected The selected choice (can be null if dialog cancelled)
     * @param min Minimum valid choice
     * @param max Maximum valid choice
     * @return The selected value, or -1 if null
     */
    public static int symbolicVitruviusChoice(Integer selected, int min, int max) {
        System.out.println("[SymbolicComparison:symbolicVitruviusChoice] CALLED!");
        System.out.println("[SymbolicComparison:symbolicVitruviusChoice]   - Input value: " + selected);
        System.out.println("[SymbolicComparison:symbolicVitruviusChoice]   - Range: [" + min + ", " + max + "]");

        if (selected == null) {
            System.out.println("[SymbolicComparison:symbolicVitruviusChoice]   - Selected is null, returning -1");
            return -1; // Dialog cancelled
        }

        // HYBRID APPROACH: Try Tag first, fallback to ThreadLocal inference

        // Strategy 1: Try to extract tag using Galette's Tainter
        Tag tag = null;
        String qualifiedName = null;

        try {
            tag = edu.neu.ccs.prl.galette.internal.runtime.Tainter.getTag(selected);
            if (tag != null && !tag.isEmpty()) {
                qualifiedName = tag.getLabels()[0].toString();

                System.out.println("[SymbolicComparison:symbolicVitruviusChoice]   - Tag found: " + tag);
                System.out.println(
                        "[SymbolicComparison:symbolicVitruviusChoice]   - Qualified name from Tag: " + qualifiedName);
            }
        } catch (Exception e) {
            System.out.println("[SymbolicComparison:symbolicVitruviusChoice]   - Exception getting tag: "
                    + e.getClass().getName() + ": " + e.getMessage());
        }

        // Strategy 2: If Tag not available, use ThreadLocal inference (HYBRID fallback)
        if (qualifiedName == null) {
            qualifiedName = GaletteSymbolicator.getQualifiedNameForValue(selected);

            if (qualifiedName != null) {
                System.out.println(
                        "[SymbolicComparison:symbolicVitruviusChoice]   - Qualified name inferred from ThreadLocal: "
                                + qualifiedName);
            } else {
                System.out.println(
                        "[SymbolicComparison:symbolicVitruviusChoice]   - WARNING: Cannot infer variable name for value "
                                + selected);
            }
        }

        // Record switch constraint if we have a qualified name (from either strategy)
        if (qualifiedName != null) {
            PathUtils.addSwitchConstraint(qualifiedName, selected);

            System.out.println("[SymbolicComparison:symbolicVitruviusChoice]   - Recorded switch constraint: "
                    + qualifiedName + " == " + selected);
        } else {
            System.err.println(
                    "[SymbolicComparison:symbolicVitruviusChoice]   - ERROR: No qualified name available, switch constraint NOT recorded!");
        }

        System.out.println("[SymbolicComparison:symbolicVitruviusChoice]   - Returning: " + selected);
        return selected;
    }
}
