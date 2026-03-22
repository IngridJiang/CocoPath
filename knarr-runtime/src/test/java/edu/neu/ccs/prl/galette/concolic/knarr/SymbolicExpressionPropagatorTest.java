package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.SymbolicExpressionPropagator;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Unit tests for Knarr-style symbolic expression propagation.
 *
 * Tests that arithmetic on symbolic values builds compound Green AST expressions
 * (e.g., ADD(var(a), const(5))) instead of just merging dependency tags.
 *
 * Inspired by Knarr's StackOpITCase.
 */
public class SymbolicExpressionPropagatorTest {

    @BeforeEach
    public void setUp() {
        GaletteSymbolicator.reset();
    }

    // ===== Binary operations: one symbolic + one concrete =====

    @Test
    public void testAddSymbolicPlusConcrete() {
        // sym + 5
        Tag symTag = GaletteSymbolicator.makeSymbolicInt("a", 10);
        Tag emptyTag = null;

        Tag result = SymbolicExpressionPropagator.binaryIntOp(10, 5, symTag, emptyTag, 96); // IADD

        assertNotNull(result);
        assertFalse(result.isEmpty());

        Expression expr = GaletteSymbolicator.getExpressionForTag(result);
        assertNotNull(expr, "Result should have an associated expression");
        assertTrue(expr instanceof BinaryOperation);
        BinaryOperation binOp = (BinaryOperation) expr;
        assertEquals(Operator.ADD, binOp.getOperator());
        assertTrue(binOp.left instanceof IntVariable);
        assertEquals("a", ((IntVariable) binOp.left).getName());
        assertTrue(binOp.right instanceof IntConstant);
        assertEquals(5, ((IntConstant) binOp.right).getValueLong());
    }

    @Test
    public void testSubConcretMinusSymbolic() {
        // 20 - sym
        Tag emptyTag = null;
        Tag symTag = GaletteSymbolicator.makeSymbolicInt("b", 7);

        Tag result = SymbolicExpressionPropagator.binaryIntOp(20, 7, emptyTag, symTag, 100); // ISUB

        Expression expr = GaletteSymbolicator.getExpressionForTag(result);
        assertNotNull(expr);
        BinaryOperation binOp = (BinaryOperation) expr;
        assertEquals(Operator.SUB, binOp.getOperator());
        assertTrue(binOp.left instanceof IntConstant);
        assertEquals(20, ((IntConstant) binOp.left).getValueLong());
        assertTrue(binOp.right instanceof IntVariable);
        assertEquals("b", ((IntVariable) binOp.right).getName());
    }

    // ===== Binary operations: both symbolic =====

    @Test
    public void testAddTwoSymbolic() {
        // sym1 + sym2
        Tag tag1 = GaletteSymbolicator.makeSymbolicInt("x", 3);
        Tag tag2 = GaletteSymbolicator.makeSymbolicInt("y", 4);

        Tag result = SymbolicExpressionPropagator.binaryIntOp(3, 4, tag1, tag2, 96); // IADD

        Expression expr = GaletteSymbolicator.getExpressionForTag(result);
        assertNotNull(expr);
        BinaryOperation binOp = (BinaryOperation) expr;
        assertEquals(Operator.ADD, binOp.getOperator());
        assertTrue(binOp.left instanceof IntVariable);
        assertEquals("x", ((IntVariable) binOp.left).getName());
        assertTrue(binOp.right instanceof IntVariable);
        assertEquals("y", ((IntVariable) binOp.right).getName());
    }

    // ===== Neither operand symbolic =====

    @Test
    public void testNeitherSymbolic() {
        // Both tags null (empty) → result is null tag, no expression
        Tag result = SymbolicExpressionPropagator.binaryIntOp(3, 4, null, null, 96); // IADD
        assertNull(result, "Non-symbolic operation should return null (empty) tag");
    }

    @Test
    public void testOneNullOneNonSymbolicTag() {
        // A tag exists but has no expression → not symbolic
        Tag plain = Tag.of("just_a_label");
        Tag result = SymbolicExpressionPropagator.binaryIntOp(3, 4, plain, null, 96); // IADD
        // plain has no expression in tagToExpression → not symbolic → Tag.union
        Tag expected = Tag.union(plain, null);
        assertEquals(expected, result);
    }

    // ===== All arithmetic operators =====

    @Test
    public void testMultiplication() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("m", 6);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(6, 7, sym, null, 104); // IMUL
        BinaryOperation binOp = (BinaryOperation) GaletteSymbolicator.getExpressionForTag(result);
        assertEquals(Operator.MUL, binOp.getOperator());
    }

    @Test
    public void testDivision() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("d", 10);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(10, 3, sym, null, 108); // IDIV
        BinaryOperation binOp = (BinaryOperation) GaletteSymbolicator.getExpressionForTag(result);
        assertEquals(Operator.DIV, binOp.getOperator());
    }

    @Test
    public void testRemainder() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("r", 10);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(10, 3, sym, null, 112); // IREM
        BinaryOperation binOp = (BinaryOperation) GaletteSymbolicator.getExpressionForTag(result);
        assertEquals(Operator.MOD, binOp.getOperator());
    }

    @Test
    public void testBitwiseAnd() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("ba", 0xFF);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(0xFF, 0x0F, sym, null, 126); // IAND
        BinaryOperation binOp = (BinaryOperation) GaletteSymbolicator.getExpressionForTag(result);
        assertEquals(Operator.BIT_AND, binOp.getOperator());
    }

    @Test
    public void testBitwiseOr() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("bo", 0xF0);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(0xF0, 0x0F, sym, null, 128); // IOR
        BinaryOperation binOp = (BinaryOperation) GaletteSymbolicator.getExpressionForTag(result);
        assertEquals(Operator.BIT_OR, binOp.getOperator());
    }

    @Test
    public void testBitwiseXor() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("bx", 0xFF);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(0xFF, 0xFF, sym, null, 130); // IXOR
        BinaryOperation binOp = (BinaryOperation) GaletteSymbolicator.getExpressionForTag(result);
        assertEquals(Operator.BIT_XOR, binOp.getOperator());
    }

    @Test
    public void testShiftLeft() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("sl", 1);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(1, 3, sym, null, 120); // ISHL
        BinaryOperation binOp = (BinaryOperation) GaletteSymbolicator.getExpressionForTag(result);
        assertEquals(Operator.SHIFTL, binOp.getOperator());
    }

    @Test
    public void testShiftRight() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("sr", 16);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(16, 2, sym, null, 122); // ISHR
        BinaryOperation binOp = (BinaryOperation) GaletteSymbolicator.getExpressionForTag(result);
        assertEquals(Operator.SHIFTR, binOp.getOperator());
    }

    @Test
    public void testUnsignedShiftRight() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("usr", -1);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(-1, 1, sym, null, 124); // IUSHR
        BinaryOperation binOp = (BinaryOperation) GaletteSymbolicator.getExpressionForTag(result);
        assertEquals(Operator.SHIFTUR, binOp.getOperator());
    }

    // ===== Unary operations =====

    @Test
    public void testNegation() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("n", 42);

        Tag result = SymbolicExpressionPropagator.unaryIntOp(42, sym, 116); // INEG

        assertNotNull(result);
        Expression expr = GaletteSymbolicator.getExpressionForTag(result);
        assertNotNull(expr);
        assertTrue(expr instanceof UnaryOperation);
        UnaryOperation unOp = (UnaryOperation) expr;
        assertEquals(Operator.NEG, unOp.getOperator());
        assertTrue(unOp.getOperand(0) instanceof IntVariable);
        assertEquals("n", ((IntVariable) unOp.getOperand(0)).getName());
    }

    @Test
    public void testNegationNonSymbolic() {
        Tag empty = null;
        Tag result = SymbolicExpressionPropagator.unaryIntOp(42, empty, 116); // INEG
        // Non-symbolic: should return the input tag as-is
        assertEquals(empty, result);
    }

    // ===== Chained expressions =====

    @Test
    public void testChainedArithmetic() {
        // x = sym + 5; y = x * 2  → MUL(ADD(var(sym), const(5)), const(2))
        Tag sym = GaletteSymbolicator.makeSymbolicInt("sym", 10);

        // Step 1: sym + 5
        Tag addResult = SymbolicExpressionPropagator.binaryIntOp(10, 5, sym, null, 96); // IADD

        // Step 2: (sym + 5) * 2
        Tag mulResult = SymbolicExpressionPropagator.binaryIntOp(15, 2, addResult, null, 104); // IMUL

        Expression expr = GaletteSymbolicator.getExpressionForTag(mulResult);
        assertNotNull(expr);
        assertTrue(expr instanceof BinaryOperation);
        BinaryOperation mul = (BinaryOperation) expr;
        assertEquals(Operator.MUL, mul.getOperator());

        // Left operand should be ADD(var(sym), const(5))
        assertTrue(mul.left instanceof BinaryOperation);
        BinaryOperation add = (BinaryOperation) mul.left;
        assertEquals(Operator.ADD, add.getOperator());
        assertTrue(add.left instanceof IntVariable);
        assertEquals("sym", ((IntVariable) add.left).getName());
        assertTrue(add.right instanceof IntConstant);
        assertEquals(5, ((IntConstant) add.right).getValueLong());

        // Right operand should be const(2)
        assertTrue(mul.right instanceof IntConstant);
        assertEquals(2, ((IntConstant) mul.right).getValueLong());
    }

    @Test
    public void testChainedSubtraction() {
        // x = sym1 + sym2; y = x - 3  → SUB(ADD(var(sym1), var(sym2)), const(3))
        Tag sym1 = GaletteSymbolicator.makeSymbolicInt("s1", 4);
        Tag sym2 = GaletteSymbolicator.makeSymbolicInt("s2", 6);

        Tag addResult = SymbolicExpressionPropagator.binaryIntOp(4, 6, sym1, sym2, 96); // IADD
        Tag subResult = SymbolicExpressionPropagator.binaryIntOp(10, 3, addResult, null, 100); // ISUB

        Expression expr = GaletteSymbolicator.getExpressionForTag(subResult);
        assertNotNull(expr);
        BinaryOperation sub = (BinaryOperation) expr;
        assertEquals(Operator.SUB, sub.getOperator());

        assertTrue(sub.left instanceof BinaryOperation);
        BinaryOperation add = (BinaryOperation) sub.left;
        assertEquals(Operator.ADD, add.getOperator());
        assertEquals("s1", ((IntVariable) add.left).getName());
        assertEquals("s2", ((IntVariable) add.right).getName());

        assertEquals(3, ((IntConstant) sub.right).getValueLong());
    }

    @Test
    public void testDeepChaining() {
        // ((a + b) * c) - d  → four symbolic variables, three operations
        Tag a = GaletteSymbolicator.makeSymbolicInt("a", 1);
        Tag b = GaletteSymbolicator.makeSymbolicInt("b", 2);
        Tag c = GaletteSymbolicator.makeSymbolicInt("c", 3);
        Tag d = GaletteSymbolicator.makeSymbolicInt("d", 4);

        Tag ab = SymbolicExpressionPropagator.binaryIntOp(1, 2, a, b, 96); // a + b
        Tag abc = SymbolicExpressionPropagator.binaryIntOp(3, 3, ab, c, 104); // (a+b) * c
        Tag abcd = SymbolicExpressionPropagator.binaryIntOp(9, 4, abc, d, 100); // ((a+b)*c) - d

        Expression expr = GaletteSymbolicator.getExpressionForTag(abcd);
        assertNotNull(expr);

        // Verify tree structure
        BinaryOperation sub = (BinaryOperation) expr;
        assertEquals(Operator.SUB, sub.getOperator());

        BinaryOperation mul = (BinaryOperation) sub.left;
        assertEquals(Operator.MUL, mul.getOperator());

        BinaryOperation add = (BinaryOperation) mul.left;
        assertEquals(Operator.ADD, add.getOperator());
        assertEquals("a", ((IntVariable) add.left).getName());
        assertEquals("b", ((IntVariable) add.right).getName());

        assertEquals("c", ((IntVariable) mul.right).getName());
        assertEquals("d", ((IntVariable) sub.right).getName());
    }

    @Test
    public void testNegationInChain() {
        // -(sym + 5) → NEG(ADD(var(sym), const(5)))
        Tag sym = GaletteSymbolicator.makeSymbolicInt("v", 10);

        Tag addResult = SymbolicExpressionPropagator.binaryIntOp(10, 5, sym, null, 96);
        Tag negResult = SymbolicExpressionPropagator.unaryIntOp(15, addResult, 116);

        Expression expr = GaletteSymbolicator.getExpressionForTag(negResult);
        assertNotNull(expr);
        assertTrue(expr instanceof UnaryOperation);
        UnaryOperation neg = (UnaryOperation) expr;
        assertEquals(Operator.NEG, neg.getOperator());

        assertTrue(neg.getOperand(0) instanceof BinaryOperation);
        BinaryOperation add = (BinaryOperation) neg.getOperand(0);
        assertEquals(Operator.ADD, add.getOperator());
        assertEquals("v", ((IntVariable) add.left).getName());
    }

    // ===== Edge cases =====

    @Test
    public void testDivisionByZero() {
        // Division by zero should not crash — concrete result is 0
        Tag sym = GaletteSymbolicator.makeSymbolicInt("dz", 10);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(10, 0, sym, null, 108); // IDIV
        assertNotNull(result);
        Expression expr = GaletteSymbolicator.getExpressionForTag(result);
        assertNotNull(expr);
        assertEquals(Operator.DIV, ((BinaryOperation) expr).getOperator());
    }

    @Test
    public void testNegativeValues() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("neg", -5);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(-5, -3, sym, null, 96); // IADD
        Expression expr = GaletteSymbolicator.getExpressionForTag(result);
        assertNotNull(expr);
        BinaryOperation binOp = (BinaryOperation) expr;
        assertEquals(Operator.ADD, binOp.getOperator());
        assertEquals(-3, ((IntConstant) binOp.right).getValueLong());
    }

    @Test
    public void testFreshTagUniqueness() {
        // Each call should produce a distinct tag
        Tag sym = GaletteSymbolicator.makeSymbolicInt("u", 1);
        Tag r1 = SymbolicExpressionPropagator.binaryIntOp(1, 2, sym, null, 96);
        Tag r2 = SymbolicExpressionPropagator.binaryIntOp(1, 3, sym, null, 96);
        assertNotEquals(r1, r2, "Each operation should produce a unique tag");
    }

    @Test
    public void testResetClearsState() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("reset_v", 5);
        Tag result = SymbolicExpressionPropagator.binaryIntOp(5, 3, sym, null, 96);
        assertNotNull(GaletteSymbolicator.getExpressionForTag(result));

        GaletteSymbolicator.reset();

        // After reset, old tags should not resolve
        assertNull(GaletteSymbolicator.getExpressionForTag(result));
    }
}
