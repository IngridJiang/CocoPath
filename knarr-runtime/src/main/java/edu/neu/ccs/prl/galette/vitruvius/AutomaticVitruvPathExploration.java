package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathExplorer;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Automatic Vitruvius path exploration using constraint-based input generation.
 *
 * This example demonstrates PROPER symbolic execution:
 * - Inputs generated automatically by negating constraints
 * - Iterative path exploration until all paths covered
 *
 * Compare to VitruvSymbolicExecutionExample.java which uses manual enumeration.
 * @purpose Automatic path exploration for Vitruvius VSUM
 * @feature Explores all user dialog choices
 * @feature Generates test inputs for transformations
 *
 * @author Galette-Knarr Integration
 */
public class AutomaticVitruvPathExploration {

    public static void main(String[] args) {

        // Register XMI resource factory
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

        // Load Vitruvius Test class
        Object testInstance;
        try {
            Class<?> testClass = Class.forName("tools.vitruv.methodologisttemplate.vsum.Test");
            testInstance = testClass.getDeclaredConstructor().newInstance();
            System.out.println("✓ Loaded Vitruvius Test class");
            System.out.println();
        } catch (Exception e) {
            System.err.println("❌ Failed to load Vitruvius Test class: " + e.getMessage());
            System.err.println("Make sure Amathea-acset is in the classpath!");
            return;
        }

        // Create path explorer
        PathExplorer explorer = new PathExplorer();

        // Explore all paths automatically using constraint-based exploration!
        // PathExplorer will iterate up to MAX_ITERATIONS (default 100), but the Vitruvius
        // framework's switch statement logic should naturally constrain valid inputs to [0-4].
        // This is PROPER symbolic execution - letting constraints determine valid paths,
        // not manual enumeration.
        //
        // The switch cases in Vitruvius Test.insertTask():
        //   - case 0: InterruptTask
        //   - case 1: PeriodicTask
        //   - case 2: SoftwareTask
        //   - case 3: TimeTableTask
        //   - case 4: Decide later (no action)
        //   - default: Invalid (should not be reached)
        //
        // Even though PathExplorer tries up to 100 values, the framework constraints
        // should limit exploration to meaningful paths.
        final Object finalTestInstance = testInstance;
        List<PathExplorer.PathRecord> paths = explorer.exploreInteger(
                "user_choice",
                0, // Initial value to start exploration
                input -> executeVitruvWithInput(finalTestInstance, input));

        // Export results
        exportResultsToJson(paths, "execution_paths_automatic.json");
    }

    /**
     * Execute Vitruvius transformation with given input.
     *
     * IMPORTANT: The input is a TAGGED integer value from PathExplorer.
     * We must pass it directly to insertTask WITHOUT unboxing to preserve the tag.
     */
    private static edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper executeVitruvWithInput(
            Object testInstance, Object input) {

        // Extract concrete value ONLY for display/directory name
        // DO NOT use this for execution - it loses the tag!
        int concreteValue = (input instanceof Integer) ? (Integer) input : 0;

        System.out.println("→ Executing with user_choice = " + concreteValue);

        // Create output directory for this execution
        Path workDir = Paths.get("galette-output-automatic-" + concreteValue);

        try {
            // Execute Vitruvius transformation with TAGGED value
            // The input object already has the Galette tag attached
            Method insertTask = testInstance.getClass().getMethod("insertTask", Path.class, int.class);
            insertTask.invoke(testInstance, workDir, input); // Pass tagged input directly!

            System.out.println("  ✓ Vitruvius transformation executed");

        } catch (Exception e) {
            System.err.println("  ✗ Execution failed: " + e.getMessage());
        }

        // Return collected constraints
        return PathUtils.getCurPC();
    }

    /**
     * Export results to JSON for visualization.
     */
    private static void exportResultsToJson(List<PathExplorer.PathRecord> paths, String filename) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);
            writer.println("[");

            for (int i = 0; i < paths.size(); i++) {
                PathExplorer.PathRecord path = paths.get(i);

                writer.println("  {");
                writer.println("    \"pathId\": " + (i + 1) + ",");
                writer.println("    \"symbolicInputs\": {");

                // Write inputs
                int inputCount = 0;
                for (java.util.Map.Entry<String, Object> entry : path.inputs.entrySet()) {
                    writer.print("      \"" + entry.getKey() + "\": ");
                    if (entry.getValue() instanceof String) {
                        writer.print("\"" + entry.getValue() + "\"");
                    } else {
                        writer.print(entry.getValue());
                    }
                    inputCount++;
                    if (inputCount < path.inputs.size()) {
                        writer.println(",");
                    } else {
                        writer.println();
                    }
                }

                writer.println("    },");
                writer.println("    \"constraints\": [");

                // Write constraints
                for (int j = 0; j < path.constraints.size(); j++) {
                    writer.print("      \"" + path.constraints.get(j).toString() + "\"");
                    if (j < path.constraints.size() - 1) {
                        writer.println(",");
                    } else {
                        writer.println();
                    }
                }

                writer.println("    ],");
                writer.println("    \"executionTime\": " + path.executionTimeMs);

                if (i < paths.size() - 1) {
                    writer.println("  },");
                } else {
                    writer.println("  }");
                }
            }

            writer.println("]");
            writer.close();

            System.out.println("\n📄 Results exported to: " + filename);

        } catch (Exception e) {
            System.err.println("Failed to export results: " + e.getMessage());
        }
    }
}
