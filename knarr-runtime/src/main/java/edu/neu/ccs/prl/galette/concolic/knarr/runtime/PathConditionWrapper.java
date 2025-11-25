package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import java.util.ArrayList;
import java.util.List;
import za.ac.sun.cs.green.expr.BinaryOperation;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;
import za.ac.sun.cs.green.expr.UnaryOperation;

/**
 * Wrapper for managing path conditions in concolic execution.
 *
 * This class maintains a collection of constraints that represent
 * the current execution path and provides methods for manipulation
 * and solving.
 *
 * @author [Anne Koziolek](https://github.com/AnneKoziolek)
 */
public class PathConditionWrapper {

    /**
     * List of constraints in the current path condition.
     */
    private final List<Expression> constraints;

    /**
     * Create a new empty path condition.
     */
    public PathConditionWrapper() {
        this.constraints = new ArrayList<>();
    }

    /**
     * Create a path condition with an initial constraint.
     *
     * @param initialConstraint The first constraint
     */
    public PathConditionWrapper(Expression initialConstraint) {
        this.constraints = new ArrayList<>();
        if (initialConstraint != null) {
            constraints.add(initialConstraint);
        }
    }

    /**
     * Add a constraint to the path condition.
     *
     * @param constraint The constraint to add
     */
    public void addConstraint(Expression constraint) {
        if (constraint != null) {
            constraints.add(constraint);
        }
    }

    /**
     * Remove a constraint from the path condition.
     *
     * @param constraint The constraint to remove
     * @return true if the constraint was removed, false if not found
     */
    public boolean removeConstraint(Expression constraint) {
        return constraints.remove(constraint);
    }

    /**
     * Get all constraints in the path condition.
     *
     * @return List of constraints (defensive copy)
     */
    public List<Expression> getConstraints() {
        return new ArrayList<>(constraints);
    }

    /**
     * Get the number of constraints.
     *
     * @return Number of constraints in the path condition
     */
    public int size() {
        return constraints.size();
    }

    /**
     * Check if the path condition is empty.
     *
     * @return true if no constraints, false otherwise
     */
    public boolean isEmpty() {
        return constraints.isEmpty();
    }

    /**
     * Clear all constraints from the path condition.
     */
    public void clear() {
        constraints.clear();
    }

    /**
     * Create a single expression representing the conjunction of all constraints.
     *
     * @return Single expression representing the entire path condition, or null if empty
     */
    public Expression toSingleExpression() {
        if (constraints.isEmpty()) {
            return null;
        }

        if (constraints.size() == 1) {
            return constraints.get(0);
        }

        // Create a conjunction of all constraints
        Expression result = constraints.get(0);
        for (int i = 1; i < constraints.size(); i++) {
            result = new BinaryOperation(Operator.AND, result, constraints.get(i));
        }

        return result;
    }

    /**
     * Create a copy of this path condition.
     *
     * @return A new PathConditionWrapper with the same constraints
     */
    public PathConditionWrapper copy() {
        PathConditionWrapper copy = new PathConditionWrapper();
        copy.constraints.addAll(this.constraints);
        return copy;
    }

    // ==================== LEGACY KNARR COMPATIBILITY ====================

    /**
     * Add a constraint operation (legacy Knarr compatibility).
     *
     * @param op The operation to add
     */
    public synchronized void _addDet(Operation op) {
        addConstraint(op);
    }

    /**
     * Add a binary operation constraint (legacy Knarr compatibility).
     *
     * @param op The operator
     * @param left Left operand
     * @param right Right operand
     * @return The created operation
     */
    public synchronized Expression _addDet(Operator op, Expression left, Expression right) {
        Operation ret = new BinaryOperation(op, left, right);
        addConstraint(ret);
        return ret;
    }

    /**
     * Add a unary operation constraint (legacy Knarr compatibility).
     *
     * @param op The operator
     * @param operand The operand
     * @return The created operation
     */
    public synchronized Expression _addDet(Operator op, Expression operand) {
        Operation ret = new UnaryOperation(op, operand);
        addConstraint(ret);
        return ret;
    }

    /**
     * Merge constraints from another path condition.
     *
     * @param other The other path condition to merge
     */
    public void merge(PathConditionWrapper other) {
        if (other != null) {
            constraints.addAll(other.constraints);
        }
    }

    /**
     * Check if this path condition contains a specific constraint.
     *
     * @param constraint The constraint to check for
     * @return true if the constraint is present, false otherwise
     */
    public boolean contains(Expression constraint) {
        return constraints.contains(constraint);
    }

    /**
     * Get a string representation of the path condition.
     *
     * @return String representation showing all constraints
     */
    @Override
    public String toString() {
        if (constraints.isEmpty()) {
            return "PathCondition: []";
        }

        StringBuilder sb = new StringBuilder("PathCondition: [");
        for (int i = 0; i < constraints.size(); i++) {
            if (i > 0) {
                sb.append(" AND ");
            }
            sb.append(constraints.get(i).toString());
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * Get the constraints as an array.
     *
     * @return Array of constraint expressions
     */
    public Expression[] toArray() {
        return constraints.toArray(new Expression[0]);
    }
}
