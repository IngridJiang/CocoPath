package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathExplorer;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

/**
 * Automatic Vitruvius path exploration using constraint-based input generation.
 *
 * This example demonstrates PROPER symbolic execution:
 * 1. No manual domain constraints - extracted from switch statements
 * 2. Automatic constraint collection via bytecode instrumentation
 * 3. Constraint-based input generation to explore all feasible paths
 * 4. No hardcoded test inputs - the solver finds them!
 *
 * @purpose Automatic path exploration for Vitruvius VSUM
 * @feature Explores all user dialog choices
 * @feature Generates test inputs for transformations
 *
 */
public class AutomaticVitruvPathExploration {

    public static void main(String[] args) {
        // Verify instrumentation is working
        AutomaticVitruvPathExplorationHelper.verifyInstrumentation();

        // Initialize EMF
        AutomaticVitruvPathExplorationHelper.initializeEMF();

        // Load Vitruvius Test class
        Object testInstance = AutomaticVitruvPathExplorationHelper.loadVitruviusTestClass();

        // Create path explorer
        PathExplorer explorer = new PathExplorer();

        // NOTE: Domain constraints are NOW AUTOMATICALLY EXTRACTED from switch statements!
        // The bytecode instrumentation in TagPropagator.recordSwitchConstraint() will
        // automatically add domain constraints based on the switch min/max values.
        // No manual PathUtils.addIntDomainConstraint() calls needed!

        // Explore all paths automatically using constraint-based exploration!
        // PathExplorer will iterate up to MAX_ITERATIONS (default 100), but the Vitruvius
        // framework's switch statement logic should naturally constrain valid inputs to [0-4].
        // This is PROPER symbolic execution - letting constraints determine valid paths,
        //
        final Object finalTestInstance = testInstance;
        List<PathExplorer.PathRecord> paths = explorer.exploreInteger(
                0, // Initial value to start exploration
                input -> executeVitruvWithInput(finalTestInstance, input),
                "CreateAscetTaskRoutine:execute:userChoice"); // Qualified name matching the reaction

        // Export results
        AutomaticVitruvPathExplorationHelper.exportSingleVarResults(paths, "execution_paths_automatic.json");
    }

    /**
     * Execute Vitruvius transformation with given input.
     *
     * IMPORTANT: The input is a TAGGED integer value from PathExplorer.
     * We pass it directly to insertTask to preserve the tag.
     * The reaction will handle all symbolic execution concerns.
     */
    private static PathConditionWrapper executeVitruvWithInput(Object testInstance, Object input) {

        // Extract concrete value for display/directory name
        int concreteValue = (input instanceof Integer) ? (Integer) input : 0;

        System.out.println(
                "[AutomaticVitruvPathExploration:executeVitruvWithInput] → Executing with input = " + concreteValue);

        // Create output directory for this execution
        Path workDir =
                AutomaticVitruvPathExplorationHelper.createWorkingDirectory("galette-output-automatic", concreteValue);

        try {
            // Execute Vitruvius transformation first
            System.out.println(
                    "[AutomaticVitruvPathExploration:executeVitruvWithInput]   Attempting to invoke insertTask with workDir="
                            + workDir + ", input=" + input);
            Method insertTask = testInstance.getClass().getMethod("insertTask", Path.class, Integer.class);
            System.out.println("[AutomaticVitruvPathExploration:executeVitruvWithInput]   Found method: " + insertTask);

            insertTask.invoke(testInstance, workDir, input);
            System.out.println("[AutomaticVitruvPathExploration:executeVitruvWithInput]   Method invocation succeeded");

            System.out.println(
                    "[AutomaticVitruvPathExploration:executeVitruvWithInput] Vitruvius transformation executed");
            System.out.println("[AutomaticVitruvPathExploration:executeVitruvWithInput]   Constraints: "
                    + PathUtils.getCurPC().size());

        } catch (Exception e) {
            System.err.println("[AutomaticVitruvPathExploration:executeVitruvWithInput] Execution failed: "
                    + e.getClass().getName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("[AutomaticVitruvPathExploration:executeVitruvWithInput]  Cause: "
                        + e.getCause().getClass().getName() + ": "
                        + e.getCause().getMessage());
                if (e.getCause().getCause() != null) {
                    System.err.println("[AutomaticVitruvPathExploration:executeVitruvWithInput]  Root Cause: "
                            + e.getCause().getCause().getClass().getName() + ": "
                            + e.getCause().getCause().getMessage());
                }
            }
            e.printStackTrace();

            return new PathConditionWrapper();
        }

        // Return collected constraints
        PathConditionWrapper pc = PathUtils.getCurPC();
        System.out.println("[AutomaticVitruvPathExploration:executeVitruvWithInput]   Constraints: " + pc.size());
        System.out.println("[AutomaticVitruvPathExploration:executeVitruvWithInput]   Returning PC with " + pc.size()
                + " constraints");
        return pc;
    }
}
