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
 * Automatic path exploration for TinyBrakeVSUM: two-disc scenario.
 *
 * <p>Uses four symbolic continuous integer variables (two per disc):
 *
 * <ul>
 *   <li>disc1 profileChoice: continuous [-1, 100] (see {@link AutomaticBrakePathExploration})
 *   <li>disc1 calibChoice: continuous [0, 100] (0-32 conservative, 33-66 standard, 67-100 track)
 *   <li>disc2 profileChoice: continuous [-1, 100]
 *   <li>disc2 calibChoice: continuous [0, 100]
 * </ul>
 *
 * <p>Constraints are collected automatically via Galette bytecode instrumentation of if-comparisons.
 * Discs have different diameters (300.0 vs 350.0) so their qualified names differ, enabling
 * independent constraint tracking.
 *
 * <p>Expected: 81 distinct behavioural paths (3^4: each disc has 3 non-skip profile intervals
 * × 3 calibration intervals; skip is excluded because initial values are [0, 0, 0, 0]).
 *
 * <p>Initial values {@code [0, 0, 0, 0]} start both discs in the off_road/conservative interval,
 * ensuring all four symbolic variables appear on the first execution and their domain constraints
 * are captured immediately.
 *
 * @see AutomaticBrakePathExploration for single-disc (2-variable) exploration
 */
public class AutomaticBrakeMultiVarPathExploration {

    private static final boolean DEBUG = Boolean.getBoolean("path.explorer.debug");

    public static void main(String[] args) {
        System.out.println("[AutomaticBrakeMultiVarPathExploration:main] CocoPath - TinyBrake two-disc\n");

        // Verify instrumentation is working
        AutomaticVitruvPathExplorationHelper.verifyInstrumentation();

        // Initialize EMF
        AutomaticVitruvPathExplorationHelper.initializeEMF();

        try {
            // Load TinyBrake Test class
            Object testInstance = AutomaticVitruvPathExplorationHelper.loadVitruviusTestClass();

            // Create path explorer
            PathExplorer explorer = new PathExplorer();

            // Four symbolic variables: disc1Profile, disc1Calib, disc2Profile, disc2Calib
            List<Integer> initialValues = Arrays.asList(0, 0, 0, 0);

            System.out.println(
                    "[AutomaticBrakeMultiVarPathExploration:main] Starting two-disc brake path exploration...");
            System.out.println(
                    "[AutomaticBrakeMultiVarPathExploration:main] Variables: disc1Profile (-1 to 100), disc1Calib (0 to 100), disc2Profile (-1 to 100), disc2Calib (0 to 100)");
            System.out.println("[AutomaticBrakeMultiVarPathExploration:main] Expected paths: 81 (3^4: 3 profiles x 3 calibs per disc, 2 discs)");

            List<PathExplorer.PathRecord> paths = explorer.exploreMultipleIntegers(initialValues, inputs -> {
                return executeWithInputs(testInstance, inputs);
            });

            // Display results
            System.out.println("\n[AutomaticBrakeMultiVarPathExploration:main] Results");
            System.out.println("[AutomaticBrakeMultiVarPathExploration:main] Total paths explored: " + paths.size());
            System.out.println();
            for (PathExplorer.PathRecord path : paths) {
                System.out.println(path);
            }

            // Export results
            AutomaticVitruvPathExplorationHelper.exportMultiVarResults(paths, "execution_paths_brake_multivar.json");

            System.out.println("\n[AutomaticBrakeMultiVarPathExploration:main] Complete");
            System.out.println(
                    "[AutomaticBrakeMultiVarPathExploration:main] Results saved to: execution_paths_brake_multivar.json");
            System.out.println(
                    "[AutomaticBrakeMultiVarPathExploration:main] Generated models saved to: galette-output-brake-multivar-*/");

        } catch (Exception e) {
            System.err.println("[AutomaticBrakeMultiVarPathExploration:main] Error during path exploration:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Execute TinyBrake transformation with four symbolic inputs (two discs).
     *
     * <p>Calls insertTwoDiscs(Path, int p1, int c1, int p2, int c2). The reactions for each disc
     * use distinct qualified names keyed by disc diameter (300.0 vs 350.0), enabling independent
     * symbolic constraint tracking.
     */
    private static PathConditionWrapper executeWithInputs(Object testInstance, Map<String, Object> inputs) {
        // Extract values in sorted key order (var_0..var_3)
        List<String> sortedKeys = new ArrayList<>(inputs.keySet());
        Collections.sort(sortedKeys);

        List<Integer> values = new ArrayList<>();
        for (String key : sortedKeys) {
            values.add((Integer) inputs.get(key));
        }
        while (values.size() < 4) {
            values.add(0);
        }

        int disc1Profile = values.get(0);
        int disc1Calib = values.get(1);
        int disc2Profile = values.get(2);
        int disc2Calib = values.get(3);

        System.out.println("[AutomaticBrakeMultiVarPathExploration:execute] disc1=(" + disc1Profile + "," + disc1Calib
                + ") disc2=(" + disc2Profile + "," + disc2Calib + ")");

        // Create unique working directory for this path
        Path workDir = AutomaticVitruvPathExplorationHelper.createWorkingDirectory(
                "galette-output-brake-multivar",
                disc1Profile + "_" + disc1Calib + "_" + disc2Profile + "_" + disc2Calib);

        // Reset path condition (preserves GaletteSymbolicator label→tag mappings for tag reuse)
        PathUtils.resetPC();

        try {
            // insertTwoDiscs takes (Path, int, int, int, int) - use int.class for reflection lookup
            Method insertTwoDiscs = testInstance
                    .getClass()
                    .getMethod("insertTwoDiscs", Path.class, int.class, int.class, int.class, int.class);
            insertTwoDiscs.invoke(testInstance, workDir, disc1Profile, disc1Calib, disc2Profile, disc2Calib);

            if (DEBUG) {
                System.out.println("[AutomaticBrakeMultiVarPathExploration:execute] Vitruvius transformation executed");
            }
        } catch (Exception e) {
            System.err.println("[AutomaticBrakeMultiVarPathExploration:execute] Error: "
                    + e.getClass().getName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause: " + e.getCause().getClass().getName() + ": "
                        + e.getCause().getMessage());
            }
            e.printStackTrace();
        }

        PathConditionWrapper pc = PathUtils.getCurPC();
        System.out.println("[AutomaticBrakeMultiVarPathExploration:execute] Constraints collected: " + pc.size());
        return pc;
    }
}
