package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

/**
 * Simple test to verify bytecode interception of comparisons works.
 * This test will have native Java comparisons that should be automatically
 * intercepted when run with the instrumented Java and Galette agent.
 */
public class BytecodeInterceptionTest {

    public static void main(String[] args) {
        System.out.println("=== Bytecode Interception Test ===");
        System.out.println("Testing automatic interception of native Java comparisons");
        System.out.println();

        // Test 1: Simple integer comparison
        System.out.println("Test 1: Integer comparison");
        int a = 10;
        int b = 20;
        if (a < b) {
            System.out.println("  Result: 10 < 20 is true");
        } else {
            System.out.println("  Result: 10 < 20 is false");
        }

        // Test 2: Double comparison (should trigger DCMPL)
        System.out.println("\nTest 2: Double comparison (DCMPL)");
        double x = 12.5;
        double y = 10.0;
        if (x > y) {
            System.out.println("  Result: 12.5 > 10.0 is true");
        } else {
            System.out.println("  Result: 12.5 > 10.0 is false");
        }

        // Test 3: Long comparison (should trigger LCMP)
        System.out.println("\nTest 3: Long comparison (LCMP)");
        long l1 = 1000000L;
        long l2 = 999999L;
        if (l1 > l2) {
            System.out.println("  Result: 1000000 > 999999 is true");
        } else {
            System.out.println("  Result: 1000000 > 999999 is false");
        }

        // Test 4: Float comparison (should trigger FCMPL)
        System.out.println("\nTest 4: Float comparison (FCMPL)");
        float f1 = 3.14f;
        float f2 = 2.71f;
        if (f1 > f2) {
            System.out.println("  Result: 3.14 > 2.71 is true");
        } else {
            System.out.println("  Result: 3.14 > 2.71 is false");
        }

        // Test 5: Switch statement
        System.out.println("\nTest 5: Switch statement");
        int choice = 2;
        switch (choice) {
            case 0:
                System.out.println("  Choice was 0");
                break;
            case 1:
                System.out.println("  Choice was 1");
                break;
            case 2:
                System.out.println("  Choice was 2");
                break;
            default:
                System.out.println("  Choice was something else");
        }

        System.out.println("\n=== Expected Output with Interception ===");
        System.out.println("When run with instrumented Java and Galette agent, you should see:");
        System.out.println("- Debug output from ComparisonInterceptorVisitor");
        System.out.println("- PathUtils.instrumentedDcmpl/Lcmp/Fcmpl messages");
        System.out.println("- Constraint collection messages");
        System.out.println();
        System.out.println("Without interception, only the result messages will appear.");
    }
}
