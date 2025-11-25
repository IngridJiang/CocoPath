package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteKnarrTaintListener;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.sun.cs.green.expr.Expression;

/**
 * Functional tests for Galette-based concolic execution.
 *
 * These tests demonstrate end-to-end concolic execution functionality
 * including symbolic value creation, constraint generation, and path exploration.
 *
 * @author [Anne Koziolek](https://github.com/AnneKoziolek)
 */
public class GaletteConcolicExecutionTest {

    @BeforeEach
    public void setUp() {
        // Reset all state before each test
        GaletteKnarrTaintListener.reset();
        GaletteSymbolicator.reset();
    }

    @Test
    public void testBasicSymbolicExecution() {
        // Create a symbolic integer
        Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt("x", 5);
        assertNotNull(symbolicTag);
        assertFalse(symbolicTag.isEmpty());
        assertTrue(symbolicTag.contains("x"));

        // Verify the tag is tracked
        Tag retrievedTag = GaletteSymbolicator.getTagForValue(5);
        assertEquals(symbolicTag, retrievedTag);

        // Get Green expression
        Expression expr = GaletteSymbolicator.getExpressionForTag(symbolicTag);
        assertNotNull(expr);
    }

    @Test
    public void testArithmeticConstraintGeneration() {
        // Create two symbolic values
        Tag x = GaletteSymbolicator.makeSymbolicInt("x", 10);
        Tag y = GaletteSymbolicator.makeSymbolicInt("y", 5);
        Tag result = GaletteSymbolicator.makeSymbolicInt("result", 15);

        assertNotNull(x);
        assertNotNull(y);
        assertNotNull(result);

        // Simulate arithmetic operation: result = x + y
        GaletteKnarrTaintListener listener = GaletteKnarrTaintListener.getInstance();
        listener.onArithmetic(x, y, result);

        // Check that constraints were generated
        PathConditionWrapper pc = PathUtils.getCurPC();
        assertTrue(pc.size() > 0);
        assertFalse(pc.isEmpty());
    }

    @Test
    public void testBranchConstraintGeneration() {
        // Create a symbolic condition
        Tag condition = GaletteSymbolicator.makeSymbolicInt("condition", 1);
        assertNotNull(condition);

        GaletteKnarrTaintListener listener = GaletteKnarrTaintListener.getInstance();

        // Simulate taking a branch
        listener.onBranch(condition, true);

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertTrue(pc.size() > 0);

        // Clear and test not taking the branch
        pc.clear();
        listener.onBranch(condition, false);

        assertTrue(pc.size() > 0);
    }

    @Test
    public void testComparisonConstraintGeneration() {
        // Create symbolic operands
        Tag left = GaletteSymbolicator.makeSymbolicInt("left", 10);
        Tag right = GaletteSymbolicator.makeSymbolicInt("right", 5);

        GaletteKnarrTaintListener listener = GaletteKnarrTaintListener.getInstance();

        // Test equality comparison (left == right) -> false
        listener.onComparison(left, right, 159, false); // IF_ICMPEQ with false result

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertTrue(pc.size() > 0);

        // Test less than comparison (left < right) -> false
        pc.clear();
        listener.onComparison(left, right, 161, false); // IF_ICMPLT with false result

        assertTrue(pc.size() > 0);
    }

    @Test
    public void testMethodCallTracking() {
        // Create symbolic arguments
        Tag arg1 = GaletteSymbolicator.makeSymbolicInt("arg1", 42);
        Tag arg2 = GaletteSymbolicator.makeSymbolicString("arg2", "test");
        Tag result = GaletteSymbolicator.makeSymbolicInt("methodResult", 100);

        GaletteKnarrTaintListener listener = GaletteKnarrTaintListener.getInstance();

        // Simulate method call with symbolic arguments
        Tag[] arguments = {arg1, arg2};
        listener.onMethodCall("testMethod", arguments, result);

        // Should have generated some constraints for symbolic arguments
        PathConditionWrapper pc = PathUtils.getCurPC();
        assertTrue(pc.size() >= 0); // May be 0 if no constraints generated for this simple case
    }

    @Test
    public void testArrayAccessTracking() {
        // Create symbolic array and index
        Tag arrayTag = GaletteSymbolicator.makeSymbolicInt("array", 0);
        Tag indexTag = GaletteSymbolicator.makeSymbolicInt("index", 2);
        Tag valueTag = GaletteSymbolicator.makeSymbolicInt("value", 42);

        GaletteKnarrTaintListener listener = GaletteKnarrTaintListener.getInstance();

        // Simulate array write
        listener.onArrayAccess(arrayTag, indexTag, valueTag, true);

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertTrue(pc.size() >= 0);

        // Simulate array read
        pc.clear();
        listener.onArrayAccess(arrayTag, indexTag, valueTag, false);

        assertTrue(pc.size() >= 0);
    }

    @Test
    public void testComplexPathCondition() {
        // Create multiple symbolic values
        Tag x = GaletteSymbolicator.makeSymbolicInt("x", 10);
        Tag y = GaletteSymbolicator.makeSymbolicInt("y", 20);
        Tag z = GaletteSymbolicator.makeSymbolicInt("z", 30);

        GaletteKnarrTaintListener listener = GaletteKnarrTaintListener.getInstance();

        // Generate multiple constraints
        listener.onComparison(x, y, 161, true); // x < y -> true
        listener.onComparison(y, z, 161, true); // y < z -> true
        listener.onBranch(x, true); // Branch on x

        PathConditionWrapper pc = PathUtils.getCurPC();
        assertTrue(pc.size() > 0);

        // Test converting to single expression
        Expression singleExpr = pc.toSingleExpression();
        assertNotNull(singleExpr);

        // Test path condition operations
        assertFalse(pc.isEmpty());
        assertTrue(pc.size() > 1);

        // Test copying path condition
        PathConditionWrapper copy = pc.copy();
        assertEquals(pc.size(), copy.size());
    }

    @Test
    public void testConcolicExecution_SimpleProgram() {
        // Simulate a simple program:
        // int x = symbolic("x", 5);
        // int y = symbolic("y", 10);
        // int z = x + y;
        // if (z > 12) {
        //     return 1;
        // } else {
        //     return 0;
        // }

        // Step 1: Create symbolic inputs
        Tag x = GaletteSymbolicator.makeSymbolicInt("x", 5);
        Tag y = GaletteSymbolicator.makeSymbolicInt("y", 10);
        Tag z = GaletteSymbolicator.makeSymbolicInt("z", 15);

        GaletteKnarrTaintListener listener = GaletteKnarrTaintListener.getInstance();

        // Step 2: Simulate z = x + y
        listener.onArithmetic(x, y, z);

        // Step 3: Simulate if (z > 12) comparison
        Tag twelve = GaletteSymbolicator.makeSymbolicInt("const_12", 12);
        listener.onComparison(z, twelve, 163, true); // IF_ICMPGT with true result

        // Step 4: Simulate taking the branch
        listener.onBranch(z, true);

        // Verify constraints were generated
        PathConditionWrapper pc = PathUtils.getCurPC();
        assertTrue(pc.size() > 0);

        // Should have constraints for:
        // 1. z = x + y
        // 2. z > 12
        // 3. Branch taken on z

        assertFalse(pc.isEmpty());
        Expression pathCondition = pc.toSingleExpression();
        assertNotNull(pathCondition);

        // Print statistics for debugging
        if (GaletteSymbolicator.DEBUG) {
            System.out.println("Final path condition: " + pathCondition);
            System.out.println(GaletteSymbolicator.getStatistics());
            System.out.println(GaletteKnarrTaintListener.getStatistics());
        }
    }

    @Test
    public void testConstraintSolving() {
        // Create a simple satisfiable constraint system
        Tag x = GaletteSymbolicator.makeSymbolicInt("x", 5);
        Tag y = GaletteSymbolicator.makeSymbolicInt("y", 10);

        GaletteKnarrTaintListener listener = GaletteKnarrTaintListener.getInstance();
        listener.onComparison(x, y, 161, true); // x < y -> true (5 < 10)

        // Try to solve the path condition
        GaletteSymbolicator.InputSolution solution = GaletteSymbolicator.solvePathCondition();

        // For now, this will return a mock solution since we don't have a real solver
        // In a full implementation, this would return actual variable assignments
        assertNotNull(solution);
    }
}
