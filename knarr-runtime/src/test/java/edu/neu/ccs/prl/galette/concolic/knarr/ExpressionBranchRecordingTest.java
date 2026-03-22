package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.SymbolicExpressionPropagator;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Tests for tag-based branch constraint recording combined with expression propagation.
 *
 * Verifies that after arithmetic on symbolic values, branch recording produces
 * compound predicates (e.g., GT(ADD(var(a), var(b)), const(10))) instead of
 * simple variable-vs-constant constraints.
 */
public class ExpressionBranchRecordingTest {

    @BeforeEach
    public void setUp() {
        GaletteSymbolicator.reset();
    }

    // ===== Single-value branches with tag =====

    @Test
    public void testSingleValueBranchWithSymbolicTag() {
        // if (sym == 0)  where sym has value 5 → branch NOT taken → NE constraint
        Tag sym = GaletteSymbolicator.makeSymbolicInt("sv", 5);

        PathUtils.testAndRecordSingleValueBranchWithTag(5, sym, 153); // IFEQ

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(1, pc.size());
        Expression constraint = pc.getConstraints().get(0);
        assertTrue(constraint instanceof BinaryOperation);
        BinaryOperation binOp = (BinaryOperation) constraint;
        // 5 != 0 → branch not taken → NE
        assertEquals(Operator.NE, binOp.getOperator());
        assertTrue(binOp.left instanceof IntVariable);
        assertEquals("sv", ((IntVariable) binOp.left).getName());
        assertEquals(0, ((IntConstant) binOp.right).getValueLong());
    }

    @Test
    public void testSingleValueBranchTaken() {
        // if (sym == 0) where sym has value 0 → branch TAKEN → EQ constraint
        Tag sym = GaletteSymbolicator.makeSymbolicInt("z", 0);

        PathUtils.testAndRecordSingleValueBranchWithTag(0, sym, 153); // IFEQ

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(1, pc.size());
        BinaryOperation binOp = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.EQ, binOp.getOperator());
    }

    @Test
    public void testSingleValueBranchNullTag() {
        // Null tag should produce no constraint
        PathUtils.testAndRecordSingleValueBranchWithTag(5, null, 153);
        assertEquals(0, PathUtils.getCurPC().size());
    }

    @Test
    public void testSingleValueBranchTagWithNoExpression() {
        // Tag exists but has no associated expression → no constraint
        Tag plainTag = Tag.of("no_expression");
        PathUtils.testAndRecordSingleValueBranchWithTag(5, plainTag, 153);
        assertEquals(0, PathUtils.getCurPC().size());
    }

    // ===== Two-value branches with tags =====

    @Test
    public void testTwoValueBranchLeftSymbolic() {
        // if (sym > 10) where sym=15 → taken → GT
        Tag sym = GaletteSymbolicator.makeSymbolicInt("lv", 15);

        PathUtils.testAndRecordTwoValueBranchWithTag(15, 10, sym, null, 163); // IF_ICMPGT

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(1, pc.size());
        BinaryOperation binOp = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.GT, binOp.getOperator());
        assertTrue(binOp.left instanceof IntVariable);
        assertEquals("lv", ((IntVariable) binOp.left).getName());
        assertTrue(binOp.right instanceof IntConstant);
        assertEquals(10, ((IntConstant) binOp.right).getValueLong());
    }

    @Test
    public void testTwoValueBranchBothSymbolic() {
        // if (sym1 < sym2) where sym1=3, sym2=7 → taken → LT
        Tag sym1 = GaletteSymbolicator.makeSymbolicInt("p", 3);
        Tag sym2 = GaletteSymbolicator.makeSymbolicInt("q", 7);

        PathUtils.testAndRecordTwoValueBranchWithTag(3, 7, sym1, sym2, 161); // IF_ICMPLT

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(1, pc.size());
        BinaryOperation binOp = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.LT, binOp.getOperator());
        assertTrue(binOp.left instanceof IntVariable);
        assertEquals("p", ((IntVariable) binOp.left).getName());
        assertTrue(binOp.right instanceof IntVariable);
        assertEquals("q", ((IntVariable) binOp.right).getName());
    }

    @Test
    public void testTwoValueBranchNotTaken() {
        // if (sym >= 10) where sym=5 → NOT taken → LT (negation of GE)
        Tag sym = GaletteSymbolicator.makeSymbolicInt("nt", 5);

        PathUtils.testAndRecordTwoValueBranchWithTag(5, 10, sym, null, 162); // IF_ICMPGE

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(1, pc.size());
        BinaryOperation binOp = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.LT, binOp.getOperator()); // GE not taken → LT
    }

    @Test
    public void testTwoValueBranchBothConcrete() {
        // Both concrete → no constraint
        PathUtils.testAndRecordTwoValueBranchWithTag(3, 7, null, null, 161);
        assertEquals(0, PathUtils.getCurPC().size());
    }

    // ===== Expression propagation + branch recording (end-to-end) =====

    @Test
    public void testArithmeticThenBranch_SymPlusConcrete() {
        // x = sym + 5; if (x > 10) where sym=10, x=15 → taken → GT(ADD(var(sym), const(5)), const(10))
        Tag sym = GaletteSymbolicator.makeSymbolicInt("sym", 10);
        Tag addResult = SymbolicExpressionPropagator.binaryIntOp(10, 5, sym, null, 96); // IADD

        PathUtils.testAndRecordTwoValueBranchWithTag(15, 10, addResult, null, 163); // IF_ICMPGT

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(1, pc.size());
        BinaryOperation gt = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.GT, gt.getOperator());

        // Left side should be the compound expression ADD(var(sym), const(5))
        assertTrue(gt.left instanceof BinaryOperation);
        BinaryOperation add = (BinaryOperation) gt.left;
        assertEquals(Operator.ADD, add.getOperator());
        assertEquals("sym", ((IntVariable) add.left).getName());
        assertEquals(5, ((IntConstant) add.right).getValueLong());

        // Right side should be const(10)
        assertEquals(10, ((IntConstant) gt.right).getValueLong());
    }

    @Test
    public void testArithmeticThenBranch_TwoSymbolic() {
        // x = a + b; if (x > 10) where a=6, b=7, x=13 → taken
        Tag a = GaletteSymbolicator.makeSymbolicInt("a", 6);
        Tag b = GaletteSymbolicator.makeSymbolicInt("b", 7);
        Tag addResult = SymbolicExpressionPropagator.binaryIntOp(6, 7, a, b, 96); // IADD

        PathUtils.testAndRecordTwoValueBranchWithTag(13, 10, addResult, null, 163);

        BinaryOperation gt =
                (BinaryOperation) PathUtils.getCurPC().getConstraints().get(0);
        BinaryOperation add = (BinaryOperation) gt.left;
        assertEquals("a", ((IntVariable) add.left).getName());
        assertEquals("b", ((IntVariable) add.right).getName());
    }

    @Test
    public void testChainedArithmeticThenBranch() {
        // x = sym * 2; y = x - 3; if (y == 0)
        // sym=2 → x=4 → y=1 → y==0 is false → NE
        Tag sym = GaletteSymbolicator.makeSymbolicInt("s", 2);
        Tag mulResult = SymbolicExpressionPropagator.binaryIntOp(2, 2, sym, null, 104); // IMUL
        Tag subResult = SymbolicExpressionPropagator.binaryIntOp(4, 3, mulResult, null, 100); // ISUB

        // if (y == 0) using single-value branch (compare to zero)
        PathUtils.testAndRecordSingleValueBranchWithTag(1, subResult, 153); // IFEQ

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(1, pc.size());
        BinaryOperation eq = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.NE, eq.getOperator()); // 1 != 0 → not taken → NE

        // Left side: SUB(MUL(var(s), const(2)), const(3))
        assertTrue(eq.left instanceof BinaryOperation);
        BinaryOperation sub = (BinaryOperation) eq.left;
        assertEquals(Operator.SUB, sub.getOperator());
        BinaryOperation mul = (BinaryOperation) sub.left;
        assertEquals(Operator.MUL, mul.getOperator());
        assertEquals("s", ((IntVariable) mul.left).getName());
    }

    @Test
    public void testMultipleBranches() {
        // if (sym >= 0) { if (sym < 34) { ... } }
        // sym=50 → first taken (GE), second not taken (GE instead of LT)
        Tag sym = GaletteSymbolicator.makeSymbolicInt("choice", 50);

        PathUtils.testAndRecordTwoValueBranchWithTag(50, 0, sym, null, 162); // IF_ICMPGE
        PathUtils.testAndRecordTwoValueBranchWithTag(50, 34, sym, null, 161); // IF_ICMPLT

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(2, pc.size());

        // First constraint: choice >= 0
        BinaryOperation c1 = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.GE, c1.getOperator());
        assertEquals("choice", ((IntVariable) c1.left).getName());
        assertEquals(0, ((IntConstant) c1.right).getValueLong());

        // Second constraint: choice >= 34 (NOT taken for LT, so negated to GE)
        BinaryOperation c2 = (BinaryOperation) pc.getConstraints().get(1);
        assertEquals(Operator.GE, c2.getOperator());
        assertEquals("choice", ((IntVariable) c2.left).getName());
        assertEquals(34, ((IntConstant) c2.right).getValueLong());
    }

    @Test
    public void testAllTwoValueOpcodes() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("op", 5);

        // IF_ICMPEQ (159) with 5 == 5 → taken → EQ
        PathUtils.testAndRecordTwoValueBranchWithTag(5, 5, sym, null, 159);
        assertEquals(
                Operator.EQ,
                ((BinaryOperation) PathUtils.getCurPC().getConstraints().get(0)).getOperator());

        PathUtils.getCurPC().clear();

        // IF_ICMPNE (160) with 5 != 3 → taken → NE
        PathUtils.testAndRecordTwoValueBranchWithTag(5, 3, sym, null, 160);
        assertEquals(
                Operator.NE,
                ((BinaryOperation) PathUtils.getCurPC().getConstraints().get(0)).getOperator());

        PathUtils.getCurPC().clear();

        // IF_ICMPLE (164) with 5 <= 5 → taken → LE
        PathUtils.testAndRecordTwoValueBranchWithTag(5, 5, sym, null, 164);
        assertEquals(
                Operator.LE,
                ((BinaryOperation) PathUtils.getCurPC().getConstraints().get(0)).getOperator());
    }
}
