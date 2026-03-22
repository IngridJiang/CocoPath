package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.testexamples.SimpleReaction;

/**
 * Standalone test for native bytecode interception with path exploration.
 * No EMF/Vitruvius dependencies — tests the full interception pipeline:
 *   tagging → bytecode interception → constraint collection → bridge → solver
 *
 * Mimics the brake reaction pattern: a symbolic integer drives if-interval branches.
 *
 * Run with:
 *   target/galette/java/bin/java \
 *     -cp target/classes:target/test-classes:$(cat cp.txt) \
 *     -Xbootclasspath/a:../galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar \
 *     -javaagent:../galette-agent/target/galette-agent-1.0.0-SNAPSHOT.jar \
 *     -Dgalette.concolic.interception.enabled=true \
 *     edu.neu.ccs.prl.galette.concolic.knarr.runtime.NativeInterceptionExplorationTest
 */
public class NativeInterceptionExplorationTest {

    /**
     * Simple transformation that branches on an integer input.
     * This class is in the concolic package (excluded from Galette instrumentation)
     * so the comparisons here won't be intercepted. The actual comparisons happen
     * in SimpleReaction which simulates a reaction class.
     */
    public static void main(String[] args) {
        System.out.println("=== Native Interception Exploration Test ===");
        System.out.println();

        // Step 1: Check if interception is enabled
        boolean interceptionEnabled = Boolean.getBoolean("galette.concolic.interception.enabled");
        System.out.println("Interception enabled: " + interceptionEnabled);
        System.out.println("Bridge available: " + GalettePathConstraintBridge.isAvailable());
        System.out.println();

        // Step 2: Test simple execution with a symbolic integer
        System.out.println("--- Test 1: Single execution with value 50 ---");
        PathUtils.resetPC();
        GalettePathConstraintBridge.clearSymbolicRegistries();
        GalettePathConstraintBridge.resetGaletteConstraints();

        // Create a symbolic integer (mimics getOrMakeSymbolicInt in reactions)
        int testValue = 50;
        String varName = "testVar";
        Integer symbolicValue = GaletteSymbolicator.getOrMakeSymbolicInt(varName, testValue, 0, 100);
        System.out.println("Created symbolic int: " + varName + " = " + symbolicValue);

        // Execute the "reaction" — the comparisons happen in SimpleReaction
        String result = SimpleReaction.execute(symbolicValue);
        System.out.println("Result: " + result);

        // Check what was collected
        PathConditionWrapper pc = PathUtils.getCurPCWithNativeConstraints();
        System.out.println(
                "Constraints from explicit calls: " + PathUtils.getCurPC().size());
        System.out.println("Total constraints (with native): " + pc.size());
        System.out.println();

        // Step 3: Test PathExplorer with multiple iterations
        System.out.println("--- Test 2: Path exploration ---");
        GaletteSymbolicator.reset();
        PathExplorer explorer = new PathExplorer();

        try {
            java.util.List<PathExplorer.PathRecord> paths = explorer.exploreInteger(0, input -> {
                int inputVal = ((Number) input).intValue();
                PathUtils.resetPC();
                Integer symVal = GaletteSymbolicator.getOrMakeSymbolicInt("choice", inputVal, 0, 100);
                String res = SimpleReaction.execute(symVal);
                System.out.println("  Input=" + inputVal + " -> " + res);
                return PathUtils.getCurPCWithNativeConstraints();
            });

            System.out.println();
            System.out.println("=== Results ===");
            System.out.println("Total paths explored: " + paths.size());
            for (int i = 0; i < paths.size(); i++) {
                PathExplorer.PathRecord p = paths.get(i);
                System.out.println("  Path " + i + ": input=" + p.inputs + " constraints=" + p.constraints.size());
            }
        } catch (Exception e) {
            System.out.println("Error during exploration: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("=== Test Complete ===");
    }
}
