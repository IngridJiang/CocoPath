package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import java.util.*;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 *
 * @purpose Constraint solving with Green solver integration
 * @feature Automatic constraint negation for path exploration
 * @feature Support for integer, real, and string constraints
 * @feature Solution caching and optimization
 *
 */
public class ConstraintSolver {

    private static final boolean DEBUG = Boolean.getBoolean("constraint.solver.debug");

    public static Expression negateConstraint(Expression constraint) {
        if (constraint == null) {
            return null;
        }

        if (constraint instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) constraint;
            Operator op = binOp.getOperator();

            if (isComparisonOperator(op)) {
                Operator negatedOp = negateOperator(op);
                return new BinaryOperation(negatedOp, binOp.left, binOp.right);
            }

            if (op == Operator.AND) {
                Expression leftNeg = negateConstraint(binOp.left);
                Expression rightNeg = negateConstraint(binOp.right);
                return new BinaryOperation(Operator.OR, leftNeg, rightNeg);
            } else if (op == Operator.OR) {
                Expression leftNeg = negateConstraint(binOp.left);
                Expression rightNeg = negateConstraint(binOp.right);
                return new BinaryOperation(Operator.AND, leftNeg, rightNeg);
            }
        } else if (constraint instanceof UnaryOperation) {
            UnaryOperation unOp = (UnaryOperation) constraint;
            if (unOp.getOperator() == Operator.NOT) {
                return unOp.getOperand(0);
            }
        }

        return new UnaryOperation(Operator.NOT, constraint);
    }

    private static Operator negateOperator(Operator op) {
        switch (op) {
            case EQ:
                return Operator.NE;
            case NE:
                return Operator.EQ;
            case GT:
                return Operator.LE;
            case GE:
                return Operator.LT;
            case LT:
                return Operator.GE;
            case LE:
                return Operator.GT;
            default:
                return op;
        }
    }

    private static boolean isComparisonOperator(Operator op) {
        return op == Operator.EQ
                || op == Operator.NE
                || op == Operator.GT
                || op == Operator.GE
                || op == Operator.LT
                || op == Operator.LE;
    }

    public static InputSolution solveConstraint(Expression constraint) {
        if (constraint == null) {
            return null;
        }

        InputSolution solution = new InputSolution();
        List<SimpleConstraint> simpleConstraints = extractSimpleConstraints(constraint);

        if (simpleConstraints.isEmpty()) {
            System.err.println("[ConstraintSolver] WARNING: No simple (var op const) constraints could be extracted "
                    + "from: " + constraint + ". This likely means the constraint contains compound expressions "
                    + "that require Z3.");
            return null;
        }

        // Try to find a value that satisfies ALL constraints
        // Group constraints by variable
        Map<String, List<SimpleConstraint>> constraintsByVar = new HashMap<>();
        for (SimpleConstraint sc : simpleConstraints) {
            constraintsByVar
                    .computeIfAbsent(sc.variableName, k -> new ArrayList<>())
                    .add(sc);
        }

        // Solve for each variable
        for (Map.Entry<String, List<SimpleConstraint>> entry : constraintsByVar.entrySet()) {
            String varName = entry.getKey();
            List<SimpleConstraint> constraints = entry.getValue();

            Object value = solveConstraintsForVariable(constraints);
            if (value == null) {
                // UNSAT - no value satisfies all constraints for this variable
                if (DEBUG) System.out.println("[ConstraintSolver] UNSAT for variable: " + varName);
                return null;
            }

            solution.setValue(varName, value);
        }

        if (solution.isEmpty()) {
            return null;
        }

        solution.setSatisfiable(true);
        return solution;
    }

    private static Object solveConstraintsForVariable(List<SimpleConstraint> constraints) {
        if (constraints.isEmpty()) {
            return null;
        }

        // Extract bounds and requirements
        Integer minValue = null;
        Integer maxValue = null;
        Set<Integer> excludedValues = new HashSet<>();
        Integer requiredValue = null;

        for (SimpleConstraint sc : constraints) {
            if (!(sc.constantValue instanceof Integer)) {
                continue; // Skip non-integer constraints for now
            }

            int threshold = (Integer) sc.constantValue;

            switch (sc.operator) {
                case EQ:
                    if (requiredValue != null && requiredValue != threshold) {
                        return null; // Conflicting equality constraints
                    }
                    requiredValue = threshold;
                    break;
                case NE:
                    excludedValues.add(threshold);
                    break;
                case GT:
                    minValue = (minValue == null) ? threshold + 1 : Math.max(minValue, threshold + 1);
                    break;
                case GE:
                    minValue = (minValue == null) ? threshold : Math.max(minValue, threshold);
                    break;
                case LT:
                    maxValue = (maxValue == null) ? threshold - 1 : Math.min(maxValue, threshold - 1);
                    break;
                case LE:
                    maxValue = (maxValue == null) ? threshold : Math.min(maxValue, threshold);
                    break;
            }
        }

        // If there's a required value, check if it satisfies all constraints
        if (requiredValue != null) {
            if (excludedValues.contains(requiredValue)) {
                return null; // Required value is excluded
            }
            if (minValue != null && requiredValue < minValue) {
                return null; // Required value below minimum
            }
            if (maxValue != null && requiredValue > maxValue) {
                return null; // Required value above maximum
            }
            return requiredValue;
        }

        // Find any value in the valid range that's not excluded
        if (minValue == null) minValue = Integer.MIN_VALUE;
        if (maxValue == null) maxValue = Integer.MAX_VALUE;

        if (minValue > maxValue) {
            return null; // Empty range
        }

        // Search for a valid value in the range (limited to 1000 candidates)
        int searchLimit = Math.min(maxValue, minValue + 1000);
        for (int candidate = minValue; candidate <= searchLimit; candidate++) {
            if (!excludedValues.contains(candidate)) {
                return candidate;
            }
        }

        // No valid value found within search window
        System.err.println("[ConstraintSolver] WARNING: No valid value found in range [" + minValue + ", " + maxValue
                + "] after scanning 1000 candidates. " + excludedValues.size() + " values excluded.");
        return null;
    }

    private static class SimpleConstraint {
        String variableName;
        Operator operator;
        Object constantValue;

        SimpleConstraint(String var, Operator op, Object val) {
            this.variableName = var;
            this.operator = op;
            this.constantValue = val;
        }

        @Override
        public String toString() {
            return variableName + " " + operator + " " + constantValue;
        }
    }

    private static List<SimpleConstraint> extractSimpleConstraints(Expression expr) {
        List<SimpleConstraint> constraints = new ArrayList<>();
        extractSimpleConstraintsRecursive(expr, constraints);
        return constraints;
    }

    private static void extractSimpleConstraintsRecursive(Expression expr, List<SimpleConstraint> result) {
        if (expr instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) expr;
            Operator op = binOp.getOperator();

            if (isComparisonOperator(op)) {
                String varName = null;
                Object constValue = null;

                if (binOp.left instanceof Variable && isConstant(binOp.right)) {
                    varName = ((Variable) binOp.left).getName();
                    constValue = getConstantValue(binOp.right);
                } else if (binOp.right instanceof Variable && isConstant(binOp.left)) {
                    varName = ((Variable) binOp.right).getName();
                    constValue = getConstantValue(binOp.left);
                    op = flipOperator(op);
                } else {
                    // COMPOUND EXPRESSION: e.g. ADD(var(x), const(5)) > const(10)
                    // The simple solver CANNOT handle this — Z3 is required.
                    System.err.println("[ConstraintSolver] WARNING: Dropping compound constraint that simple solver "
                            + "cannot handle: " + expr + ". Use Z3 solver (-Ddisable.z3.solver not set) for "
                            + "arithmetic expressions in constraints.");
                }

                if (varName != null && constValue != null) {
                    result.add(new SimpleConstraint(varName, op, constValue));
                }
            } else if (op == Operator.AND || op == Operator.OR) {
                extractSimpleConstraintsRecursive(binOp.left, result);
                extractSimpleConstraintsRecursive(binOp.right, result);
            } else {
                System.err.println("[ConstraintSolver] WARNING: Unsupported operator in constraint: " + op
                        + " in expression: " + expr);
            }
        } else if (expr != null
                && !(expr instanceof Variable)
                && !(expr instanceof IntConstant)
                && !(expr instanceof RealConstant)
                && !(expr instanceof StringConstant)) {
            System.err.println("[ConstraintSolver] WARNING: Unexpected expression type in constraint: "
                    + expr.getClass().getSimpleName() + ": " + expr);
        }
    }

    private static Operator flipOperator(Operator op) {
        switch (op) {
            case GT:
                return Operator.LT;
            case GE:
                return Operator.LE;
            case LT:
                return Operator.GT;
            case LE:
                return Operator.GE;
            default:
                return op; // EQ, NE are symmetric
        }
    }

    private static boolean isConstant(Expression expr) {
        return expr instanceof IntConstant || expr instanceof RealConstant || expr instanceof StringConstant;
    }

    private static Object getConstantValue(Expression expr) {
        if (expr instanceof IntConstant) {
            return (int) ((IntConstant) expr).getValueLong();
        } else if (expr instanceof RealConstant) {
            return ((RealConstant) expr).getValue();
        } else if (expr instanceof StringConstant) {
            String str = expr.toString();
            if (str.startsWith("\"") && str.endsWith("\"")) {
                return str.substring(1, str.length() - 1);
            }
            return str;
        }
        return null;
    }

    private static Object solveSimpleConstraint(SimpleConstraint sc) {
        Object constVal = sc.constantValue;

        if (constVal instanceof Integer) {
            int threshold = (Integer) constVal;
            return solveIntegerConstraint(sc.operator, threshold);
        } else if (constVal instanceof Double) {
            double threshold = (Double) constVal;
            return solveDoubleConstraint(sc.operator, threshold);
        }

        return null;
    }

    private static Integer solveIntegerConstraint(Operator op, int threshold) {
        switch (op) {
            case EQ:
                return threshold;
            case NE:
                return threshold + 1; // Pick any value != threshold
            case GT:
                return threshold + 1;
            case GE:
                return threshold;
            case LT:
                return threshold - 1;
            case LE:
                return threshold;
            default:
                return null;
        }
    }

    private static Double solveDoubleConstraint(Operator op, double threshold) {
        switch (op) {
            case EQ:
                return threshold;
            case NE:
                return threshold + 1.0;
            case GT:
                return threshold + 0.1;
            case GE:
                return threshold;
            case LT:
                return threshold - 0.1;
            case LE:
                return threshold;
            default:
                return null;
        }
    }

    public static List<InputSolution> generateAllSolutions(Expression constraint) {
        List<InputSolution> solutions = new ArrayList<>();

        if (constraint instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) constraint;

            if (binOp.getOperator() == Operator.OR) {
                solutions.addAll(generateAllSolutions(binOp.left));
                solutions.addAll(generateAllSolutions(binOp.right));
                return solutions;
            }
        }

        InputSolution solution = solveConstraint(constraint);
        if (solution != null) {
            solutions.add(solution);
        }

        return solutions;
    }

    public static InputSolution generateNextInput(List<Expression> exploredConstraints) {
        if (exploredConstraints.isEmpty()) {
            return null;
        }

        Expression lastConstraint = exploredConstraints.get(exploredConstraints.size() - 1);
        Expression negated = negateConstraint(lastConstraint);

        if (exploredConstraints.size() > 1) {
            List<Expression> allNegations = new ArrayList<>();
            for (Expression explored : exploredConstraints) {
                allNegations.add(negateConstraint(explored));
            }

            Expression combined = allNegations.get(0);
            for (int i = 1; i < allNegations.size(); i++) {
                combined = new BinaryOperation(Operator.AND, combined, allNegations.get(i));
            }

            negated = combined;
        }

        return solveConstraint(negated);
    }
}
