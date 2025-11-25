package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.green.GaletteGreenBridge;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.RealVariable;

/**
 * Test suite specifically for verifying basic tag operations work correctly.
 *
 * This test validates the milestone "Basic Tag operations work correctly"
 * by testing tag creation, conversion, storage, and retrieval operations.
 *
 * @author [Anne Koziolek](https://github.com/AnneKoziolek)
 */
public class TagOperationsTest {

    @BeforeEach
    public void setUp() {
        // Reset state before each test
        GaletteSymbolicator.reset();
    }

    @Test
    public void testTagCreation() {
        // Test that we can create tags for different types
        Tag intTag = GaletteSymbolicator.makeSymbolicInt("test_int", 42);
        assertNotNull(intTag, "Integer tag should be created");
        assertFalse(intTag.isEmpty(), "Tag should not be empty");

        Tag longTag = GaletteSymbolicator.makeSymbolicLong("test_long", 123456789L);
        assertNotNull(longTag, "Long tag should be created");
        assertFalse(longTag.isEmpty(), "Tag should not be empty");

        Tag doubleTag = GaletteSymbolicator.makeSymbolicDouble("test_double", 3.14159);
        assertNotNull(doubleTag, "Double tag should be created");
        assertFalse(doubleTag.isEmpty(), "Tag should not be empty");

        Tag stringTag = GaletteSymbolicator.makeSymbolicString("test_string", "hello");
        assertNotNull(stringTag, "String tag should be created");
        assertFalse(stringTag.isEmpty(), "Tag should not be empty");
    }

    @Test
    public void testTagUniqueness() {
        // Test that different labels create different tags
        Tag tag1 = GaletteSymbolicator.makeSymbolicInt("label1", 10);
        Tag tag2 = GaletteSymbolicator.makeSymbolicInt("label2", 20);

        assertNotNull(tag1, "First tag should be created");
        assertNotNull(tag2, "Second tag should be created");
        assertNotEquals(tag1, tag2, "Different labels should create different tags");
    }

    @Test
    public void testTagConsistency() {
        // Test that the system handles label management correctly
        String label1 = "consistent_label_1";
        String label2 = "consistent_label_2";
        Tag tag1 = GaletteSymbolicator.makeSymbolicInt(label1, 42);
        Tag tag2 = GaletteSymbolicator.makeSymbolicInt(label2, 42);

        assertNotNull(tag1, "First tag should be created");
        assertNotNull(tag2, "Second tag should be created");

        // The behavior should be consistent for different labels
        Expression expr1 = GaletteGreenBridge.tagToExpression(tag1);
        Expression expr2 = GaletteGreenBridge.tagToExpression(tag2);
        assertNotNull(expr1, "First expression should be created");
        assertNotNull(expr2, "Second expression should be created");

        // Different labels should create different variables
        assertNotEquals(
                ((IntVariable) expr1).getName(),
                ((IntVariable) expr2).getName(),
                "Different labels should create different variables");
    }

    @Test
    public void testTagToExpressionConversion() {
        // Test that tags can be converted to Green solver expressions
        Tag intTag = GaletteSymbolicator.makeSymbolicInt("expr_test", 100);
        assertNotNull(intTag, "Tag should be created");

        Expression expr = GaletteGreenBridge.tagToExpression(intTag);
        assertNotNull(expr, "Expression should be created from tag");
        assertTrue(expr instanceof IntVariable, "Expression should be an IntVariable");

        IntVariable intVar = (IntVariable) expr;
        assertEquals("expr_test", intVar.getName(), "Variable name should match label");
    }

    @Test
    public void testTagStorage() {
        // Test that tags are properly stored and can be retrieved
        String label = "storage_test";
        int value = 999;

        Tag tag = GaletteSymbolicator.makeSymbolicInt(label, value);
        assertNotNull(tag, "Tag should be created");

        // Verify the tag is stored in the bridge
        Expression expr = GaletteGreenBridge.tagToExpression(tag);
        assertNotNull(expr, "Expression should be retrievable");
    }

    @Test
    public void testTagTypeSafety() {
        // Test that different types create appropriate expressions
        Tag intTag = GaletteSymbolicator.makeSymbolicInt("int_type", 42);
        Tag longTag = GaletteSymbolicator.makeSymbolicLong("long_type", 42L);
        Tag doubleTag = GaletteSymbolicator.makeSymbolicDouble("double_type", 42.0);

        Expression intExpr = GaletteGreenBridge.tagToExpression(intTag);
        Expression longExpr = GaletteGreenBridge.tagToExpression(longTag);
        Expression doubleExpr = GaletteGreenBridge.tagToExpression(doubleTag);

        assertNotNull(intExpr, "Int expression should be created");
        assertNotNull(longExpr, "Long expression should be created");
        assertNotNull(doubleExpr, "Double expression should be created");

        // All should be variables with appropriate names
        assertTrue(intExpr instanceof IntVariable, "Int should create IntVariable");
        assertEquals("int_type", ((IntVariable) intExpr).getName());

        assertTrue(longExpr instanceof IntVariable, "Long should create IntVariable");
        assertEquals("long_type", ((IntVariable) longExpr).getName());

        assertTrue(doubleExpr instanceof RealVariable, "Double should create RealVariable");
        assertEquals("double_type", ((RealVariable) doubleExpr).getName());
    }

    @Test
    public void testTagReset() {
        // Test that tag system can be properly reset
        Tag tag1 = GaletteSymbolicator.makeSymbolicInt("reset_test1", 1);
        Tag tag2 = GaletteSymbolicator.makeSymbolicInt("reset_test2", 2);

        assertNotNull(tag1, "First tag should be created");
        assertNotNull(tag2, "Second tag should be created");

        // Reset the system
        GaletteSymbolicator.reset();

        // After reset, we should be able to create new tags (including reusing old labels)
        Tag newTag = GaletteSymbolicator.makeSymbolicInt("reset_test1", 42);
        assertNotNull(newTag, "New tag should be created after reset");
    }

    @Test
    public void testTagOperationIntegration() {
        // Integration test showing tags work in a simple symbolic execution scenario
        Tag x = GaletteSymbolicator.makeSymbolicInt("x", 5);
        Tag y = GaletteSymbolicator.makeSymbolicInt("y", 10);

        assertNotNull(x, "X tag should be created");
        assertNotNull(y, "Y tag should be created");

        // Verify both tags can be converted to expressions
        Expression exprX = GaletteGreenBridge.tagToExpression(x);
        Expression exprY = GaletteGreenBridge.tagToExpression(y);

        assertNotNull(exprX, "X expression should be created");
        assertNotNull(exprY, "Y expression should be created");

        assertTrue(exprX instanceof IntVariable, "X should be IntVariable");
        assertTrue(exprY instanceof IntVariable, "Y should be IntVariable");

        assertEquals("x", ((IntVariable) exprX).getName());
        assertEquals("y", ((IntVariable) exprY).getName());
    }

    @Test
    public void testNullAndEmptyTagHandling() {
        // Test edge cases with null and empty inputs
        // These should be handled gracefully
        try {
            Tag nullLabelTag = GaletteSymbolicator.makeSymbolicInt(null, 42);
            // If no exception is thrown, the method should return null or handle gracefully
        } catch (IllegalArgumentException e) {
            // This is acceptable behavior for null labels
            assertTrue(e.getMessage().contains("null"), "Error message should mention null");
        }

        try {
            Tag emptyLabelTag = GaletteSymbolicator.makeSymbolicInt("", 42);
            // If no exception is thrown, the method should return null or handle gracefully
        } catch (IllegalArgumentException e) {
            // This is acceptable behavior for empty labels
            assertTrue(e.getMessage().contains("empty"), "Error message should mention empty");
        }
    }

    @Test
    public void testTagMemoryManagement() {
        // Test that creating many tags doesn't cause memory issues
        final int NUM_TAGS = 100; // Reduced to avoid performance issues

        for (int i = 0; i < NUM_TAGS; i++) {
            Tag tag = GaletteSymbolicator.makeSymbolicInt("memory_test_" + i, i);
            assertNotNull(tag, "Tag " + i + " should be created");

            Expression expr = GaletteGreenBridge.tagToExpression(tag);
            assertNotNull(expr, "Expression " + i + " should be created");
        }

        // System should still be functional after creating many tags
        Tag finalTag = GaletteSymbolicator.makeSymbolicInt("final_test", 99999);
        assertNotNull(finalTag, "Final tag should be created");
    }

    @Test
    public void testTagLabelsAndValues() {
        // Test that tag labels and values are properly associated
        String label = "value_test";
        int value = 42;

        Tag tag = GaletteSymbolicator.makeSymbolicInt(label, value);
        assertNotNull(tag, "Tag should be created");

        // Verify the tag contains the expected label
        Object[] labels = tag.getLabels();
        assertNotNull(labels, "Labels array should not be null");
        assertTrue(labels.length > 0, "Should have at least one label");
        assertEquals(label, labels[0], "First label should match input");
    }
}
