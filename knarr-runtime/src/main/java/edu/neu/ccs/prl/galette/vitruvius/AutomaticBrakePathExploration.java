package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathExplorer;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Automatic path exploration for TinyBrakeVSUM: single-disc scenario.
 *
 * <p>Uses two symbolic continuous integer variables per brake disc:
 *
 * <ul>
 *   <li>profileChoice: continuous [-1, 100]
 *       <ul>
 *         <li>&lt; 0 → skip (no AxleControlUnit created)
 *         <li>0–33 → off_road (multiplier 0.70)
 *         <li>34–66 → comfort (multiplier 0.85)
 *         <li>67–100 → sport (multiplier 1.20)
 *       </ul>
 *   <li>calibChoice: continuous [0, 100]
 *       <ul>
 *         <li>0–32 → conservative (offset −0.5)
 *         <li>33–66 → standard (offset 0.0)
 *         <li>67–100 → track (offset +0.5)
 *       </ul>
 * </ul>
 *
 * <p>Constraints are collected via explicit PathUtils.addIfComparisonConstraint calls in
 * the reactions (mirroring Amalthea's addSwitchConstraint pattern, but for if-interval
 * branches); Galette agent instrumentation handles tag propagation via getOrMakeSymbolicInt.
 *
 * <p>Expected: up to 10 distinct behavioural paths (4 profile intervals × 3 calibration
 * intervals, minus 2 because the skip path has no calibration branch).
 *
 * @see AutomaticBrakeMultiVarPathExploration for two-disc (4-variable) exploration
 */
public class AutomaticBrakePathExploration {

    public static void main(String[] args) {
        System.out.println("[AutomaticBrakePathExploration:main] CocoPath - TinyBrake single-disc\n");

        // Verify instrumentation is working
        AutomaticVitruvPathExplorationHelper.verifyInstrumentation();

        // Initialize EMF
        AutomaticVitruvPathExplorationHelper.initializeEMF();

        // Load TinyBrake Test class
        Object testInstance = AutomaticVitruvPathExplorationHelper.loadVitruviusTestClass();

        // Create path explorer
        PathExplorer explorer = new PathExplorer();

        // Two symbolic continuous variables:
        //   profileChoice in [-1, 100]: < 0 → skip, 0-33 → off_road, 34-66 → comfort, 67-100 → sport
        //   calibChoice   in [ 0, 100]: 0-32 → conservative, 33-66 → standard, 67-100 → track
        // Start at profileChoice=-1 (skip path) so the first explored path is the degenerate case.
        List<Integer> initialValues = Arrays.asList(-1, 0);

        System.out.println("[AutomaticBrakePathExploration:main] Starting single-disc brake path exploration...");
        System.out.println(
                "[AutomaticBrakePathExploration:main] Variables: profileChoice (-1 to 100), calibChoice (0 to 100)");
        System.out.println(
                "[AutomaticBrakePathExploration:main] Expected paths: up to 10 (4 profile intervals × 3 calib intervals; skip has no calib)");

        final Object finalTestInstance = testInstance;
        List<PathExplorer.PathRecord> paths = explorer.exploreMultipleIntegers(initialValues, inputs -> {
            return executeWithInputs(finalTestInstance, inputs);
        });

        // Display results
        System.out.println("\n[AutomaticBrakePathExploration:main] Results");
        System.out.println("[AutomaticBrakePathExploration:main] Total paths explored: " + paths.size());
        System.out.println();
        for (PathExplorer.PathRecord path : paths) {
            System.out.println(path);
        }

        // Export results
        AutomaticVitruvPathExplorationHelper.exportMultiVarResults(paths, "execution_paths_brake.json");

        System.out.println("\n[AutomaticBrakePathExploration:main] Complete");
        System.out.println("[AutomaticBrakePathExploration:main] Results saved to: execution_paths_brake.json");
        System.out.println("[AutomaticBrakePathExploration:main] Generated models saved to: galette-output-brake-*/");
    }

    /**
     * Execute TinyBrake transformation with two symbolic inputs.
     *
     * <p>Calls insertBrakeDisc(Path, int profileChoice, int calibChoice). Constraint collection
     * happens inside the reactions via GaletteSymbolicator.getOrMakeSymbolicInt() and
     * PathUtils.addIfComparisonConstraint().
     */
    private static PathConditionWrapper executeWithInputs(Object testInstance, Map<String, Object> inputs) {
        // Extract values in sorted key order (var_0 = profileChoice, var_1 = calibChoice)
        List<String> sortedKeys = new ArrayList<>(inputs.keySet());
        Collections.sort(sortedKeys);

        List<Integer> values = new ArrayList<>();
        for (String key : sortedKeys) {
            values.add((Integer) inputs.get(key));
        }
        while (values.size() < 2) {
            values.add(0);
        }

        int profileChoice = values.get(0);
        int calibChoice = values.get(1);

        System.out.println("[AutomaticBrakePathExploration:execute] profileChoice=" + profileChoice + ", calibChoice="
                + calibChoice);

        // Create output directory for this execution (cleaned if stale)
        Path workDir = AutomaticVitruvPathExplorationHelper.createWorkingDirectory(
                "galette-output-brake", profileChoice + "_" + calibChoice);

        // Reset path condition (preserves GaletteSymbolicator label→tag mappings for tag reuse)
        PathUtils.resetPC();

        try {
            // insertBrakeDisc takes (Path, int, int) - use int.class for reflection lookup
            Method insertBrakeDisc =
                    testInstance.getClass().getMethod("insertBrakeDisc", Path.class, int.class, int.class);
            insertBrakeDisc.invoke(testInstance, workDir, profileChoice, calibChoice);
            System.out.println("[AutomaticBrakePathExploration:execute] Vitruvius transformation executed");
        } catch (Exception e) {
            System.err.println("[AutomaticBrakePathExploration:execute] Error: "
                    + e.getClass().getName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause: " + e.getCause().getClass().getName() + ": "
                        + e.getCause().getMessage());
            }
            e.printStackTrace();
        }

        PathConditionWrapper pc = PathUtils.getCurPCWithNativeConstraints();
        System.out.println("[AutomaticBrakePathExploration:execute] Constraints collected: " + pc.size());
        return pc;
    }
}
