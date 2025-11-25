package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.ConstraintSolver;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.InputSolution;
import org.junit.jupiter.api.Test;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Tests for constraint negation and solving APIs.
 * These tests validate the core functionality of ConstraintSolver used by PathExplorer.
 */
public class ConstraintSolverTest {

    @Test
    public void testNegateEqualityConstraint() {
        Expression constraint = new BinaryOperation(Operator.EQ, new IntVariable("x", null, null), new IntConstant(0));
        Expression negated = ConstraintSolver.negateConstraint(constraint);

        assertTrue(negated instanceof BinaryOperation);
        assertEquals(Operator.NE, ((BinaryOperation) negated).getOperator());
    }

    @Test
    public void testNegateGreaterThanConstraint() {
        Expression constraint = new BinaryOperation(Operator.GT, new IntVariable("x", null, null), new IntConstant(5));
        Expression negated = ConstraintSolver.negateConstraint(constraint);

        assertTrue(negated instanceof BinaryOperation);
        assertEquals(Operator.LE, ((BinaryOperation) negated).getOperator());
    }

    @Test
    public void testSolveEqualityConstraint() {
        Expression constraint = new BinaryOperation(Operator.EQ, new IntVariable("x", null, null), new IntConstant(5));
        InputSolution solution = ConstraintSolver.solveConstraint(constraint);

        assertNotNull(solution);
        assertTrue(solution.isSatisfiable());
        assertEquals(5, solution.getIntValue("x", -1));
    }

    @Test
    public void testSolveNotEqualConstraint() {
        Expression constraint =
                new BinaryOperation(Operator.NE, new IntVariable("user_choice", null, null), new IntConstant(0));
        InputSolution solution = ConstraintSolver.solveConstraint(constraint);

        assertNotNull(solution);
        assertTrue(solution.isSatisfiable());
        int value = solution.getIntValue("user_choice", -999);
        assertNotEquals(0, value);
        assertEquals(1, value); // Solver generates threshold + 1
    }

    @Test
    public void testAutomaticInputGeneration() {
        // Test that constraint negation generates sequential inputs automatically
        for (int expected = 0; expected < 3; expected++) {
            String varName = "user_choice_" + expected;
            Expression constraint =
                    new BinaryOperation(Operator.EQ, new IntVariable(varName, null, null), new IntConstant(expected));

            Expression negated = ConstraintSolver.negateConstraint(constraint);
            InputSolution solution = ConstraintSolver.solveConstraint(negated);

            assertNotNull(solution);
            assertTrue(solution.isSatisfiable());
            assertEquals(expected + 1, solution.getIntValue(varName, -1));
        }
    }
}
