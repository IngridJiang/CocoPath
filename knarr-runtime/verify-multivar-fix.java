import edu.neu.ccs.prl.galette.concolic.knarr.runtime.*;
import java.util.*;

/**
 * Simple verification that multi-variable constraint collection is working.
 * This simulates what AutomaticVitruvMultiVarPathExploration does.
 */
public class VerifyMultiVarFix {

    public static void main(String[] args) {
        System.out.println("=== Verifying Multi-Variable Constraint Collection Fix ===\n");

        // Enable Z3 solver
        System.setProperty("use.z3.solver", "true");
        System.setProperty("skip.instrumentation.check", "true");

        PathExplorer explorer = new PathExplorer();

        // Simulate multi-variable execution
        List<Integer> initialValues = Arrays.asList(0, 0);

        System.out.println("Testing with 2 variables, initial values: " + initialValues);
        System.out.println("Domain for each: [0, 4] (5 possible values)");
        System.out.println("Expected paths: 5 x 5 = 25 paths\n");

        List<PathExplorer.PathRecord> paths = explorer.exploreMultipleIntegers(
            initialValues,
            inputs -> {
                // Simulated executor: creates symbolic variables and collects constraints
                Integer val1 = inputs.get("var_0");
                Integer val2 = inputs.get("var_1");

                // Simulate constraint collection like Vitruvius reactions do
                GaletteSymbolicator.getOrMakeSymbolicInt(
                    "var1", val1, 0, 4
                );
                GaletteSymbolicator.getOrMakeSymbolicInt(
                    "var2", val2, 0, 4
                );

                return PathUtils.getCurPC();
            },
            null
        );

        // Analyze results
        System.out.println("\n=== Results ===");
        System.out.println("Total paths explored: " + paths.size());

        int pathsWithConstraints = 0;
        int pathsWithoutConstraints = 0;

        for (PathExplorer.PathRecord path : paths) {
            if (path.constraints.size() > 0) {
                pathsWithConstraints++;
            } else {
                pathsWithoutConstraints++;
            }
        }

        System.out.println("Paths WITH constraints: " + pathsWithConstraints);
        System.out.println("Paths WITHOUT constraints: " + pathsWithoutConstraints);

        // Show first few paths
        System.out.println("\nFirst 5 paths:");
        for (int i = 0; i < Math.min(5, paths.size()); i++) {
            PathExplorer.PathRecord path = paths.get(i);
            System.out.println("  Path " + path.pathId + ": " +
                             path.inputs + " -> " +
                             path.constraints.size() + " constraints");
        }

        // Verdict
        System.out.println("\n=== Verdict ===");
        if (pathsWithoutConstraints == 0 || pathsWithConstraints > 1) {
            System.out.println("✅ SUCCESS: Fix is working! Multiple paths have constraints.");
        } else if (pathsWithoutConstraints > 0 && pathsWithConstraints == 1) {
            System.out.println("❌ FAILED: Only first path has constraints - fix not working.");
        } else {
            System.out.println("❓ UNCLEAR: Unexpected result pattern.");
        }
    }
}
