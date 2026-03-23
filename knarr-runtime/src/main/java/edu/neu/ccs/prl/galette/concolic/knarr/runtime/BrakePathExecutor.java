package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.vitruvius.AutomaticVitruvPathExplorationHelper;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Executor for TinyBrake single-disc path exploration.
 *
 * <p>This class lives in the {@code concolic/} package which is excluded from Galette's
 * bytecode instrumentation (in both the jlink image and the runtime agent). Complex
 * logic like reflection calls, try-catch, and collection manipulation is placed here
 * to avoid VerifyError from Galette's COMPUTE_MAXS producing invalid stack map frames.
 */
public class BrakePathExecutor implements PathExplorer.MultiVarPathExecutor {

    private final Object testInstance;

    public BrakePathExecutor(Object testInstance) {
        this.testInstance = testInstance;
    }

    @Override
    public PathConditionWrapper execute(Map<String, Object> inputs) {
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

        System.out.println(
                "[BrakePathExecutor:execute] profileChoice=" + profileChoice + ", calibChoice=" + calibChoice);

        Path workDir = AutomaticVitruvPathExplorationHelper.createWorkingDirectory(
                "galette-output-brake", profileChoice + "_" + calibChoice);

        PathUtils.resetPC();

        try {
            Method insertBrakeDisc =
                    testInstance.getClass().getMethod("insertBrakeDisc", Path.class, int.class, int.class);
            insertBrakeDisc.invoke(testInstance, workDir, profileChoice, calibChoice);
            System.out.println("[BrakePathExecutor:execute] Vitruvius transformation executed");
        } catch (Exception e) {
            System.err.println(
                    "[BrakePathExecutor:execute] Error: " + e.getClass().getName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause: " + e.getCause().getClass().getName() + ": "
                        + e.getCause().getMessage());
            }
            e.printStackTrace();
        }

        PathConditionWrapper pc = PathUtils.getCurPCWithNativeConstraints();
        System.out.println("[BrakePathExecutor:execute] Constraints collected: " + pc.size());
        return pc;
    }

    /**
     * Print and export exploration results.
     */
    public static void printAndExportResults(List<PathExplorer.PathRecord> paths) {
        System.out.println("\n[AutomaticBrakePathExploration] Results");
        System.out.println("[AutomaticBrakePathExploration] Total paths explored: " + paths.size());
        System.out.println();
        for (PathExplorer.PathRecord path : paths) {
            System.out.println(path);
        }
        AutomaticVitruvPathExplorationHelper.exportMultiVarResults(paths, "execution_paths_brake.json");
        System.out.println("\n[AutomaticBrakePathExploration] Complete");
        System.out.println("[AutomaticBrakePathExploration] Results saved to: execution_paths_brake.json");
    }
}
