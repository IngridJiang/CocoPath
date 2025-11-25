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
}
