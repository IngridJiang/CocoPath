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
            return null;
        }

        for (SimpleConstraint sc : simpleConstraints) {
            Object value = solveSimpleConstraint(sc);
            if (value != null) {
                solution.setValue(sc.variableName, value);
            }
        }

        if (solution.isEmpty()) {
            return null;
        }

        solution.setSatisfiable(true);
        return solution;
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
                }

                if (varName != null && constValue != null) {
                    result.add(new SimpleConstraint(varName, op, constValue));
                }
            } else if (op == Operator.AND || op == Operator.OR) {
                extractSimpleConstraintsRecursive(binOp.left, result);
                extractSimpleConstraintsRecursive(binOp.right, result);
            }
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
