package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathExplorer;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

/**
 * Automatic multi-variable path exploration for Vitruvius transformations.
 *
 * This class demonstrates exploring all combinations of TWO user choices,
 * leading to N × M total paths (e.g., 5 × 5 = 25 paths for two 5-choice switches).
 *
 * Example: Adding two tasks to a model, where each task has 5 type options.
 *
 * @author CocoPath
 */
public class AutomaticVitruvMultiVarPathExploration {

    private static final boolean DEBUG = Boolean.getBoolean("path.explorer.debug");

    public static void main(String[] args) {
        System.out.println("[AutomaticVitruvMultiVarPathExploration:main] CocoPath\n");

        // Verify instrumentation is working
        AutomaticVitruvPathExplorationHelper.verifyInstrumentation();

        // Initialize EMF
        AutomaticVitruvPathExplorationHelper.initializeEMF();

        try {
            // Load Vitruvius Test class
            Object testInstance = AutomaticVitruvPathExplorationHelper.loadVitruviusTestClass();

            // Setup path explorer
            PathExplorer explorer = new PathExplorer();

            // Variable names for two user choices
            List<String> variableNames = Arrays.asList("user_choice_1", "user_choice_2");

            // Initial values (start with 0, 0)
            List<Integer> initialValues = Arrays.asList(0, 0);

            // Explore all paths
            System.out.println(
                    "[AutomaticVitruvMultiVarPathExploration:main] Starting multi-variable path exploration...");

            List<PathExplorer.PathRecord> paths =
                    explorer.exploreMultipleIntegers(variableNames, initialValues, inputs -> {
                        return executeVitruvWithTwoInputs(testInstance, inputs, variableNames);
                    });

            // Display results
            System.out.println("\n[AutomaticVitruvMultiVarPathExploration:main] Results");
            System.out.println("[AutomaticVitruvMultiVarPathExploration:main] Total paths explored: " + paths.size());
            System.out.println();

            for (PathExplorer.PathRecord path : paths) {
                System.out.println(path);
            }

            // Save results to JSON
            AutomaticVitruvPathExplorationHelper.exportMultiVarResults(
                    paths, "execution_paths_multivar.json", variableNames);

            System.out.println("\n[AutomaticVitruvMultiVarPathExploration:main] Complete ");
            System.out.println(
                    "[AutomaticVitruvMultiVarPathExploration:main] Results saved to: execution_paths_multivar.json");
            System.out.println(
                    "[AutomaticVitruvMultiVarPathExploration:main] Generated models saved to: galette-output-multivar-*/");

        } catch (Exception e) {
            System.err.println("[AutomaticVitruvMultiVarPathExploration:main] Error during path exploration:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Execute Vitruvius transformation with TWO user inputs.
     *
     * This method:
     * 1. Adds domain constraints for both variables
     * 2. Invokes insertTwoTasks() method with both inputs
     * 3. Records path constraints for both variables
     * 4. Returns collected constraints
     *
     * @param testInstance Instance of Test class
     * @param inputs Map containing user_choice_1 and user_choice_2
     * @param variableNames List of variable names (for constraint generation)
     * @return Path condition with all collected constraints
     */
    private static PathConditionWrapper executeVitruvWithTwoInputs(
            Object testInstance, Map<String, Object> inputs, List<String> variableNames) {
        // Get the TAGGED values (preserve symbolic tags for constraint collection)
        Integer taggedInput1 = (Integer) inputs.get(variableNames.get(0));
        Integer taggedInput2 = (Integer) inputs.get(variableNames.get(1));

        if (taggedInput1 == null) taggedInput1 = 0;
        if (taggedInput2 == null) taggedInput2 = 0;

        // Tags are already attached to inputs from PathExplorer
        // The reactions will handle all symbolic execution concerns
        System.out.println(
                "[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Tagged inputs received from PathExplorer");
        System.out.println(
                "[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs]   Input 1: " + taggedInput1);
        System.out.println(
                "[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs]   Input 2: " + taggedInput2);

        // Extract concrete values for display/directory naming
        int input1 = taggedInput1.intValue();
        int input2 = taggedInput2.intValue();

        // Create unique working directory for this path, cleaning any stale outputs to avoid proxy issues
        Path workDir = AutomaticVitruvPathExplorationHelper.createWorkingDirectory(
                "galette-output-multivar", input1 + "_" + input2);

        // Reset path condition
        PathUtils.resetPC();

        if (DEBUG) {
            System.out.println(
                    "[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Constraints will be recorded by reactions via SymbolicExecutionHelper");
        }

        try {
            // Step 3: Execute Vitruvius transformation with BOTH inputs
            Method insertTwoTasks =
                    testInstance.getClass().getMethod("insertTwoTasks", Path.class, Integer.class, Integer.class);

            if (DEBUG) {
                System.out.println(
                        "[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Invoking insertTwoTasks("
                                + workDir + ", " + input1 + ", " + input2 + ")");
            }

            insertTwoTasks.invoke(testInstance, workDir, taggedInput1, taggedInput2);

        } catch (Exception e) {
            System.err.println(
                    "[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Error executing Vitruvius transformation:");
            e.printStackTrace();
            // Return collected constraints even if execution failed
        }

        // Step 4: Return collected constraints
        PathConditionWrapper pc = PathUtils.getCurPC();

        if (DEBUG && pc != null) {
            System.out.println("[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Collected "
                    + pc.getConstraints().size() + " total constraints");
        }

        return pc;
    }
}
