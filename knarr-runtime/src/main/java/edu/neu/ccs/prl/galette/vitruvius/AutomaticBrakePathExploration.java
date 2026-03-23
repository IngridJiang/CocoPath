package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.BrakePathExecutor;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathExplorer;
import java.util.Arrays;
import java.util.List;

/*
 * NOTE: This class is instrumented by Galette at load time. Complex bytecode patterns
 * (lambdas, invokedynamic) can produce VerifyError due to Galette's COMPUTE_MAXS not
 * generating correct stack map frames. Keep the main method simple — delegate complex
 * logic to the executor class which lives in the excluded concolic/ package.
 */

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

        // Use BrakePathExecutor (in the excluded concolic/ package) to avoid
        // Galette instrumentation of the lambda/complex execution logic.
        List<PathExplorer.PathRecord> paths =
                explorer.exploreMultipleIntegers(initialValues, new BrakePathExecutor(testInstance));

        BrakePathExecutor.printAndExportResults(paths);
    }
}
