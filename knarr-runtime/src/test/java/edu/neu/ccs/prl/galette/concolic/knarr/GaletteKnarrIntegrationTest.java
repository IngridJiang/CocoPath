package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.green.GaletteGreenBridge;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.ConcolicControlStack;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteTaintListener;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;

/**
 * Integration tests for the Galette-Knarr runtime components.
 *
 * These tests verify that the core migration from Phosphor to Galette
 * is working correctly and that basic concolic execution functionality
 * is preserved.
 *
 * @author [Anne Koziolek](https://github.com/AnneKoziolek)
 */
public class GaletteKnarrIntegrationTest {

    private GaletteTaintListener listener;

    @BeforeEach
    public void setUp() {
        listener = GaletteTaintListener.getInstance();
        ConcolicControlStack.clearControl();
        GaletteGreenBridge.clearVariableCache();
    }

    @Test
    public void testTagCreation() {
        // Test basic Galette Tag functionality
        Tag tag = Tag.of("test_label");
        assertNotNull(tag);
        assertFalse(tag.isEmpty());
        assertEquals(1, tag.size());
        assertTrue(tag.contains("test_label"));
    }

    @Test
    public void testTagToGreenBridge() {
        // Test conversion from Galette Tag to Green expression
        Tag tag = Tag.of("symbolic_var");
        Expression expr = GaletteGreenBridge.tagToGreenExpression(tag, 42);

        assertNotNull(expr);
        assertTrue(expr instanceof IntVariable);
    }

    @Test
    public void testConcreteValueConversion() {
        // Test conversion of concrete values to Green constants
        Expression intExpr = GaletteGreenBridge.tagToGreenExpression(null, 42);
        assertTrue(intExpr instanceof IntConstant);
        assertEquals(42, ((IntConstant) intExpr).getValueLong());

        Expression boolExpr = GaletteGreenBridge.tagToGreenExpression(null, true);
        assertTrue(boolExpr instanceof IntConstant);
        assertEquals(1, ((IntConstant) boolExpr).getValueLong());
    }

    @Test
    public void testControlStackOperations() {
        // Test control flow stack management
        assertTrue(!ConcolicControlStack.hasControlFlow());
        assertEquals(0, ConcolicControlStack.getControlDepth());

        Tag controlTag = Tag.of("control_condition");
        ConcolicControlStack.pushControl(controlTag);

        assertTrue(ConcolicControlStack.hasControlFlow());
        assertEquals(1, ConcolicControlStack.getControlDepth());
        assertEquals(controlTag, ConcolicControlStack.peekControl());

        Tag poppedTag = ConcolicControlStack.popControl();
        assertEquals(controlTag, poppedTag);
        assertTrue(!ConcolicControlStack.hasControlFlow());
    }

    @Test
    public void testPathConditionWrapper() {
        // Test path condition management
        PathConditionWrapper pc = new PathConditionWrapper();
        assertTrue(pc.isEmpty());
        assertEquals(0, pc.size());

        Expression constraint = new IntVariable("x", null, null);
        pc.addConstraint(constraint);

        assertFalse(pc.isEmpty());
        assertEquals(1, pc.size());
        assertTrue(pc.contains(constraint));

        Expression single = pc.toSingleExpression();
        assertEquals(constraint, single);
    }

    @Test
    public void testTaintListenerBranchHandling() {
        // Test concolic listener branch handling
        PathConditionWrapper initialPC = PathUtils.getCurPC();
        int initialConstraints = initialPC.size();

        Tag branchTag = Tag.of("branch_condition");
        listener.onBranch(branchTag, true);

        // Should have added a constraint and updated control stack
        assertTrue(ConcolicControlStack.hasControlFlow());
        PathConditionWrapper updatedPC = PathUtils.getCurPC();
        assertTrue(updatedPC.size() > initialConstraints);
    }

    @Test
    public void testLongArithmeticOperation() {
        // Test symbolic long arithmetic
        Tag leftTag = Tag.of("left_operand");
        Tag rightTag = Tag.of("right_operand");

        PathUtils.GaletteTaintedLong result = new PathUtils.GaletteTaintedLong();
        PathUtils.performLongOp(leftTag, 10L, rightTag, 5L, 97, result); // 97 = LADD

        assertNotNull(result.tag);
        assertEquals(15L, result.value); // 10 + 5 = 15
    }

    @Test
    public void testNullTagHandling() {
        // Test that null tags are handled gracefully
        assertDoesNotThrow(() -> {
            listener.onBranch(null, true);
            listener.onComparison(null, null, 159, true);
            listener.onArithmetic(null, null, null);
        });
    }

    @Test
    public void testVariableCaching() {
        // Test that Green bridge caches variables correctly
        assertEquals(0, GaletteGreenBridge.getVariableCount());

        Tag tag1 = Tag.of("var1");
        Tag tag2 = Tag.of("var2");
        Tag tag1_again = Tag.of("var1"); // Same label

        GaletteGreenBridge.tagToGreenExpression(tag1, 10);
        assertEquals(1, GaletteGreenBridge.getVariableCount());

        GaletteGreenBridge.tagToGreenExpression(tag2, 20);
        assertEquals(2, GaletteGreenBridge.getVariableCount());

        GaletteGreenBridge.tagToGreenExpression(tag1_again, 30);
        assertEquals(2, GaletteGreenBridge.getVariableCount()); // Should reuse var1
    }
}
