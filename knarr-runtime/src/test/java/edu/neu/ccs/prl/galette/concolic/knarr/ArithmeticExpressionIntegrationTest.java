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
 * Integration tests that simulate the full tinybrake-style workflow:
 * symbolic input creation → arithmetic → branch → constraint collection.
 *
 * These tests exercise the same pattern as CreateAndConfigureAxleUnitRoutine
 * but with arithmetic operations before the branch, which requires Knarr-style
 * expression propagation.
 *
 * Without expression propagation, the branch would only know "some tagged value
 * compared to a constant." With expression propagation, the constraint contains
 * the full arithmetic expression, enabling the solver to generate correct inputs.
 */
public class ArithmeticExpressionIntegrationTest {

    @BeforeEach
    public void setUp() {
        GaletteSymbolicator.reset();
    }

    /**
     * Simulates: int x = sym + 5; if (x > 10)
     * This is the simplest case from the ChatGPT plan's "success criteria."
     */
    @Test
    public void testSymPlusFiveGreaterThanTen() {
        // Step 1: Create symbolic input (like getOrMakeSymbolicInt in reactions)
        Tag symTag = GaletteSymbolicator.makeSymbolicInt("profileChoice", 10);

        // Step 2: Simulate IADD: x = profileChoice + 5
        Tag xTag = SymbolicExpressionPropagator.binaryIntOp(10, 5, symTag, null, 96);

        // Step 3: Simulate IF_ICMPGT 10: if (x > 10)
        // x = 15 > 10 → taken
        PathUtils.testAndRecordTwoValueBranchWithTag(15, 10, xTag, null, 163);

        // Step 4: Verify the constraint is GT(ADD(var(profileChoice), const(5)), const(10))
        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(1, pc.size());

        BinaryOperation gt = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.GT, gt.getOperator());

        // Left: ADD(var(profileChoice), const(5))
        BinaryOperation add = (BinaryOperation) gt.left;
        assertEquals(Operator.ADD, add.getOperator());
        assertEquals("profileChoice", ((IntVariable) add.left).getName());
        assertEquals(5, ((IntConstant) add.right).getValueLong());

        // Right: const(10)
        assertEquals(10, ((IntConstant) gt.right).getValueLong());
    }

    /**
     * Simulates the tinybrake brake profile with arithmetic:
     * score = aggressiveness * 2 + baseOffset;
     * if (score < 34) → offroad
     * else if (score < 67) → comfort
     * else → sport
     */
    @Test
    public void testBrakeProfileWithArithmetic() {
        // aggressiveness = 20 (symbolic), baseOffset = 5 (concrete)
        Tag aggTag = GaletteSymbolicator.makeSymbolicInt("aggressiveness", 20);

        // score = aggressiveness * 2
        Tag mulResult = SymbolicExpressionPropagator.binaryIntOp(20, 2, aggTag, null, 104); // IMUL
        // score = (aggressiveness * 2) + 5
        Tag scoreTag = SymbolicExpressionPropagator.binaryIntOp(40, 5, mulResult, null, 96); // IADD
        // score = 45

        // if (score >= 0) → taken (45 >= 0)
        PathUtils.testAndRecordTwoValueBranchWithTag(45, 0, scoreTag, null, 162); // IF_ICMPGE

        // if (score < 34) → NOT taken (45 >= 34)
        PathUtils.testAndRecordTwoValueBranchWithTag(45, 34, scoreTag, null, 161); // IF_ICMPLT

        // if (score < 67) → taken (45 < 67)
        PathUtils.testAndRecordTwoValueBranchWithTag(45, 67, scoreTag, null, 161); // IF_ICMPLT

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(3, pc.size());

        // Verify all constraints reference the compound expression
        for (Expression constraint : pc.getConstraints()) {
            BinaryOperation binOp = (BinaryOperation) constraint;
            Expression leftSide = binOp.left;

            // The left side should be the compound arithmetic expression
            assertTrue(
                    leftSide instanceof BinaryOperation,
                    "Branch predicate should contain compound expression, got: "
                            + leftSide.getClass().getSimpleName());
        }

        // Verify first constraint: GE(ADD(MUL(var(aggressiveness), const(2)), const(5)), const(0))
        BinaryOperation c1 = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.GE, c1.getOperator());
        BinaryOperation addExpr = (BinaryOperation) c1.left;
        assertEquals(Operator.ADD, addExpr.getOperator());
        BinaryOperation mulExpr = (BinaryOperation) addExpr.left;
        assertEquals(Operator.MUL, mulExpr.getOperator());
        assertEquals("aggressiveness", ((IntVariable) mulExpr.left).getName());
    }

    /**
     * Two symbolic inputs: diff = a - b; if (diff > 0)
     * Models comparing two measurements.
     */
    @Test
    public void testTwoSymbolicInputsDifference() {
        Tag a = GaletteSymbolicator.makeSymbolicInt("sensorA", 30);
        Tag b = GaletteSymbolicator.makeSymbolicInt("sensorB", 20);

        // diff = a - b
        Tag diffTag = SymbolicExpressionPropagator.binaryIntOp(30, 20, a, b, 100); // ISUB

        // if (diff > 0) → compare diff against 0 via single-value branch
        // diff = 10, IFGT tests if value > 0
        PathUtils.testAndRecordSingleValueBranchWithTag(10, diffTag, 157); // IFGT

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertEquals(1, pc.size());

        BinaryOperation gt = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.GT, gt.getOperator());

        // Left: SUB(var(sensorA), var(sensorB))
        BinaryOperation sub = (BinaryOperation) gt.left;
        assertEquals(Operator.SUB, sub.getOperator());
        assertEquals("sensorA", ((IntVariable) sub.left).getName());
        assertEquals("sensorB", ((IntVariable) sub.right).getName());

        // Right: const(0) (implicit for single-value branch)
        assertEquals(0, ((IntConstant) gt.right).getValueLong());
    }

    /**
     * Complex expression: force = (area + pressure) * friction; if (force > 1000)
     * Models a brake force calculation with three symbolic parameters.
     */
    @Test
    public void testBrakeForceCalculation() {
        Tag area = GaletteSymbolicator.makeSymbolicInt("pistonArea", 50);
        Tag pressure = GaletteSymbolicator.makeSymbolicInt("pressure", 30);
        Tag friction = GaletteSymbolicator.makeSymbolicInt("friction", 15);

        // sum = area + pressure
        Tag sumTag = SymbolicExpressionPropagator.binaryIntOp(50, 30, area, pressure, 96); // IADD
        // force = sum * friction
        Tag forceTag = SymbolicExpressionPropagator.binaryIntOp(80, 15, sumTag, friction, 104); // IMUL
        // force = 1200

        // if (force > 1000) → taken
        PathUtils.testAndRecordTwoValueBranchWithTag(1200, 1000, forceTag, null, 163);

        PathConditionWrapper pc = PathUtils.getCurPC();
        BinaryOperation gt = (BinaryOperation) pc.getConstraints().get(0);
        assertEquals(Operator.GT, gt.getOperator());

        // Left: MUL(ADD(var(pistonArea), var(pressure)), var(friction))
        BinaryOperation mul = (BinaryOperation) gt.left;
        assertEquals(Operator.MUL, mul.getOperator());

        BinaryOperation add = (BinaryOperation) mul.left;
        assertEquals(Operator.ADD, add.getOperator());
        assertEquals("pistonArea", ((IntVariable) add.left).getName());
        assertEquals("pressure", ((IntVariable) add.right).getName());

        assertEquals("friction", ((IntVariable) mul.right).getName());

        assertEquals(1000, ((IntConstant) gt.right).getValueLong());
    }

    /**
     * Negation: x = -sym; if (x < -5)
     */
    @Test
    public void testNegationThenBranch() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("temp", 10);

        // x = -temp
        Tag negTag = SymbolicExpressionPropagator.unaryIntOp(10, sym, 116); // INEG
        // x = -10

        // if (x < -5) via IF_ICMPLT: -10 < -5 → taken
        PathUtils.testAndRecordTwoValueBranchWithTag(-10, -5, negTag, null, 161);

        BinaryOperation lt =
                (BinaryOperation) PathUtils.getCurPC().getConstraints().get(0);
        assertEquals(Operator.LT, lt.getOperator());

        // Left: NEG(var(temp))
        assertTrue(lt.left instanceof UnaryOperation);
        UnaryOperation neg = (UnaryOperation) lt.left;
        assertEquals(Operator.NEG, neg.getOperator());
        assertEquals("temp", ((IntVariable) neg.getOperand(0)).getName());

        assertEquals(-5, ((IntConstant) lt.right).getValueLong());
    }

    /**
     * Bitwise masking: masked = sym & 0xFF; if (masked == 0)
     * Models extracting a byte from a wider value.
     */
    @Test
    public void testBitwiseMaskThenBranch() {
        Tag sym = GaletteSymbolicator.makeSymbolicInt("raw", 0x1234);

        // masked = raw & 0xFF
        Tag maskedTag = SymbolicExpressionPropagator.binaryIntOp(0x1234, 0xFF, sym, null, 126); // IAND
        // masked = 0x34 = 52

        // if (masked == 0) via IFEQ: 52 == 0 → NOT taken → NE
        PathUtils.testAndRecordSingleValueBranchWithTag(52, maskedTag, 153); // IFEQ

        BinaryOperation ne =
                (BinaryOperation) PathUtils.getCurPC().getConstraints().get(0);
        assertEquals(Operator.NE, ne.getOperator());
        assertTrue(ne.left instanceof BinaryOperation);
        BinaryOperation and = (BinaryOperation) ne.left;
        assertEquals(Operator.BIT_AND, and.getOperator());
    }

    /**
     * Verify that domain constraints + arithmetic constraints combine correctly.
     * Mimics the full getOrMakeSymbolicInt flow with domain [0, 100].
     */
    @Test
    public void testDomainPlusArithmeticConstraints() {
        // Step 1: Create symbolic with domain
        Tag symTag = GaletteSymbolicator.makeSymbolicInt("choice", 50);
        PathUtils.addIntDomainConstraint("choice", 0, 101); // [0, 101)

        // Step 2: Arithmetic: score = choice + 10
        Tag scoreTag = SymbolicExpressionPropagator.binaryIntOp(50, 10, symTag, null, 96);

        // Step 3: Branch: if (score > 60) → taken (60 > 60 is false... use 70)
        // Actually score = 60, 60 > 60 is false. Let's use > 55.
        PathUtils.testAndRecordTwoValueBranchWithTag(60, 55, scoreTag, null, 163);

        PathConditionWrapper pc = PathUtils.getCurPC();
        // Should have domain constraint + branch constraint
        assertEquals(2, pc.size());

        // Verify the domain constraint exists (first added)
        Expression domainExpr = pc.getConstraints().get(0);
        assertNotNull(domainExpr);

        // Verify the branch constraint has the compound expression
        BinaryOperation branchConstraint = (BinaryOperation) pc.getConstraints().get(1);
        assertEquals(Operator.GT, branchConstraint.getOperator());
        assertTrue(branchConstraint.left instanceof BinaryOperation);
    }
}
