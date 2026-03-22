package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.*;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Tests that the PathExplorer exploration loop correctly handles compound
 * expressions produced by Knarr-style expression propagation.
 *
 * Simulates the tinybrake workflow: symbolic input → arithmetic → branch → constraint collection.
 * Verifies that:
 * 1. PathExplorer.extractPrimaryVarName finds variables in nested expressions
 * 2. Constraint negation preserves compound structure
 * 3. The exploration loop finds multiple paths when branching on computed scores
 */
public class CompoundExpressionExplorationTest {

    @BeforeEach
    public void setUp() {
        GaletteSymbolicator.reset();
    }

    /**
     * Simulates: score = aggressiveness + 30; if (score < 64) → A; else → B
     * With aggressiveness=20, score=50 → branch A taken.
     * Exploration should negate LT(ADD(var,30),64) to GE(ADD(var,30),64)
     * and solve for aggressiveness >= 34.
     */
    @Test
    public void testExplorationWithCompoundBranchConstraint() {
        PathExplorer explorer = new PathExplorer();

        List<PathExplorer.PathRecord> paths = explorer.exploreInteger(
                20, // initial value
                input -> {
                    int concreteValue = ((Number) input).intValue();
                    PathUtils.resetPC();

                    // Create symbolic int (simulates getOrMakeSymbolicInt)
                    Integer symValue =
                            GaletteSymbolicator.getOrMakeSymbolicInt("aggressiveness", concreteValue, -1, 100);

                    // Record domain constraint
                    // (already done by getOrMakeSymbolicInt on first call)

                    // Simulate IADD: score = aggressiveness + 30
                    Tag aggTag = GaletteSymbolicator.getTagForValue(symValue);
                    int discBonus = 30;
                    int score;
                    Tag scoreTag;

                    if (aggTag != null) {
                        scoreTag = SymbolicExpressionPropagator.binaryIntOp(
                                concreteValue, discBonus, aggTag, null, 96); // IADD
                        score = concreteValue + discBonus;
                    } else {
                        score = concreteValue + discBonus;
                        scoreTag = null;
                    }

                    // Simulate IF_ICMPLT: if (score < 64)
                    int threshold = 64;
                    if (scoreTag != null) {
                        PathUtils.testAndRecordTwoValueBranchWithTag(
                                score,
                                threshold,
                                scoreTag,
                                null,
                                score < threshold ? 161 : 162); // IF_ICMPLT or IF_ICMPGE
                    } else {
                        // Fallback: record on raw variable
                        PathUtils.addIfComparisonConstraint(
                                "aggressiveness", score < threshold ? "LT" : "GE", threshold - discBonus);
                    }

                    return PathUtils.getCurPC();
                },
                "aggressiveness");

        // Should find at least 2 paths (score < 64 and score >= 64)
        assertTrue(paths.size() >= 2, "Should explore at least 2 paths, found " + paths.size());

        System.out.println("Explored " + paths.size() + " paths:");
        for (PathExplorer.PathRecord p : paths) {
            System.out.println("  Input=" + p.inputs + " constraints=" + p.constraints.size());
        }
    }

    /**
     * Verify that extractPrimaryVarName (via the exploration loop) correctly
     * identifies the variable in compound constraints.
     * Uses the multi-variable explorer to test variable name extraction.
     */
    @Test
    public void testCompoundConstraintVariableExtraction() {
        // Build a compound constraint: LT(ADD(var(myVar), const(10)), const(50))
        IntVariable myVar = new IntVariable("myVar", null, null);
        Expression addExpr = new BinaryOperation(Operator.ADD, myVar, new IntConstant(10));
        Expression constraint = new BinaryOperation(Operator.LT, addExpr, new IntConstant(50));

        // Verify the constraint structure
        assertTrue(constraint instanceof BinaryOperation);
        BinaryOperation binOp = (BinaryOperation) constraint;

        // The left operand is ADD(var, const), not a direct Variable
        assertFalse(binOp.left instanceof Variable);
        assertTrue(binOp.left instanceof BinaryOperation);

        // PathExplorer's extractPrimaryVarName should still find "myVar"
        // We test this indirectly by running the explorer with compound constraints
        PathExplorer explorer = new PathExplorer();

        List<PathExplorer.PathRecord> paths = explorer.exploreInteger(
                10,
                input -> {
                    int val = ((Number) input).intValue();
                    PathUtils.resetPC();
                    GaletteSymbolicator.getOrMakeSymbolicInt("myVar", val, 0, 100);

                    // Manually add compound constraint
                    IntVariable v = new IntVariable("myVar", null, null);
                    Expression add = new BinaryOperation(Operator.ADD, v, new IntConstant(10));
                    int score = val + 10;
                    Operator op = score < 50 ? Operator.LT : Operator.GE;
                    PathUtils.getCurPC().addConstraint(new BinaryOperation(op, add, new IntConstant(50)));

                    return PathUtils.getCurPC();
                },
                "myVar");

        assertTrue(paths.size() >= 2, "Explorer should handle compound constraints, found " + paths.size() + " paths");
    }

    /**
     * Verify constraint negation preserves compound structure.
     */
    @Test
    public void testNegateCompoundConstraint() {
        // LT(ADD(var(x), const(5)), const(34))
        IntVariable x = new IntVariable("x", null, null);
        Expression add = new BinaryOperation(Operator.ADD, x, new IntConstant(5));
        Expression constraint = new BinaryOperation(Operator.LT, add, new IntConstant(34));

        Expression negated = ConstraintSolver.negateConstraint(constraint);
        assertNotNull(negated);

        // Negation of LT is GE
        assertTrue(negated instanceof BinaryOperation);
        BinaryOperation negBin = (BinaryOperation) negated;
        assertEquals(Operator.GE, negBin.getOperator());

        // The compound ADD expression should be preserved in the negation
        assertTrue(negBin.left instanceof BinaryOperation);
        BinaryOperation negAdd = (BinaryOperation) negBin.left;
        assertEquals(Operator.ADD, negAdd.getOperator());
        assertTrue(negAdd.left instanceof IntVariable);
        assertEquals("x", ((IntVariable) negAdd.left).getName());
    }
}
