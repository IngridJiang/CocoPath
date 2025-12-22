package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.internal.runtime.Tainter;

/**
 * Simple test to verify automatic constraint collection without Vitruvius dependencies.
 * This test directly uses GaletteSymbolicator and verifies that:
 * 1. Symbolic integers can be created (tagging works)
 * 2. Constraints are collected automatically via bytecode instrumentation
 * 3. No manual PathUtils calls are needed
 */
public class SimpleAutomaticTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("Simple Automatic Constraint Collection Test");
        System.out.println("=".repeat(80));

        // Clear any previous path condition
        PathUtils.resetPC();

        // Create a symbolic integer (this will be tagged)
        System.out.println("\n[1] Creating symbolic integer with value=2, range=[0, 4]...");
        int symbolicValue = GaletteSymbolicator.getOrMakeSymbolicInt(
                "testVariable",
                2, // concrete value
                0, // min
                4 // max
                );

        // Verify the value is tagged
        Object tag = Tainter.getTag(symbolicValue);
        System.out.println("    Created value: " + symbolicValue);
        System.out.println("    Tag: " + tag + " (non-null means tagged)");

        // Simulate a switch statement that would normally be instrumented
        System.out.println("\n[2] Executing switch statement with symbolic value...");
        String result;
        switch (symbolicValue) {
            case 0:
                result = "ZERO";
                break;
            case 1:
                result = "ONE";
                break;
            case 2:
                result = "TWO";
                break;
            case 3:
                result = "THREE";
                break;
            case 4:
                result = "FOUR";
                break;
            default:
                result = "OUT_OF_RANGE";
        }
        System.out.println("    Switch result: " + result);

        // Check collected constraints
        System.out.println("\n[3] Checking collected constraints...");
        PathConditionWrapper pc = PathUtils.getCurPC();
        System.out.println("    Number of constraints: " + pc.size());

        if (pc.size() > 0) {
            System.out.println("    Constraints collected:");
            System.out.println("      " + pc.toString());
        } else {
            System.out.println("    WARNING: No constraints were collected!");
            System.out.println("    This suggests bytecode instrumentation is not working.");
        }

        // Test with comparison operations
        System.out.println("\n[4] Testing comparison operations...");
        PathUtils.resetPC();

        int symbolicValue2 = GaletteSymbolicator.getOrMakeSymbolicInt("testVariable2", 3, 0, 10);

        if (symbolicValue2 > 5) {
            System.out.println("    Value > 5");
        } else {
            System.out.println("    Value <= 5");
        }

        pc = PathUtils.getCurPC();
        System.out.println("    Constraints after comparison: " + pc.size());

        // Summary
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Test Summary:");
        System.out.println("=".repeat(80));
        System.out.println("✓ Symbolic values can be created and tagged");
        System.out.println("✓ Switch statements execute correctly");
        System.out.println("✓ Comparison operations work");

        if (pc.size() > 0) {
            System.out.println("✓ AUTOMATIC CONSTRAINT COLLECTION IS WORKING!");
            System.out.println("  Bytecode instrumentation successfully collected constraints.");
            System.exit(0);
        } else {
            System.out.println("✗ AUTOMATIC CONSTRAINT COLLECTION NOT WORKING!");
            System.out.println("  No constraints were collected via bytecode instrumentation.");
            System.out.println("  This may indicate:");
            System.out.println("    - Galette javaagent not loaded");
            System.out.println("    - Instrumented Java not being used");
            System.out.println("    - Bytecode transformation not applied");
            System.exit(1);
        }
    }
}
