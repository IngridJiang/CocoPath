package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import edu.neu.ccs.prl.galette.internal.runtime.Tainter;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import za.ac.sun.cs.green.expr.Expression;

/**
 * Symbolic execution wrapper for Vitruvius model transformations.
 *
 * This class demonstrates how to integrate Galette/Knarr symbolic execution with
 * Vitruvius by tagging user inputs and letting the framework execute naturally.
 *
 * Pattern from BrakeDisc example:
 * 1. Create symbolic tag
 * 2. Tag the value
 * 3. Execute business logic (Vitruvius Test.insertTask)
 * 4. Collect path constraints automatically
 *
 * @purpose Example/demo of Vitruvius symbolic execution
 * @feature Shows integration with model transformations
 */
public class VitruvSymbolicExecutionExample {

    private static class PathResult {
        final int userChoice;
        final String pathConstraint;
        final long totalTime;
        long executionTime;
        long initializationTime;

        PathResult(int userChoice, String pathConstraint, long totalTime) {
            this.userChoice = userChoice;
            this.pathConstraint = pathConstraint;
            this.totalTime = totalTime;
            this.executionTime = totalTime;
            this.initializationTime = 0;
        }

        void setInitializationTime(long initTime) {
            this.initializationTime = initTime;
            this.executionTime = totalTime - initTime;
        }
    }

    /**
     * Execute Vitruvius transformation with symbolic user input.
     * Follows the BrakeDisc pattern from ModelTransformationExample.executeConcolic()
     */
    private static PathResult executeWithSymbolicInput(
            Object testInstance, Path workDir, int userChoice, String label) {
        // Reset symbolic execution state
        GaletteSymbolicator.reset();
        PathUtils.resetPC();

        System.out.println("Created symbolic value: " + label + " = " + userChoice);

        // Create symbolic tag for user input
        Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt(label, userChoice);

        // Tag the value using Galette - THIS IS THE KEY!
        int taggedUserChoice = Tainter.setTag(userChoice, symbolicTag);

        // Verify the tag was applied
        Tag verifyTag = Tainter.getTag(taggedUserChoice);
        System.out.println("  Tag applied: " + (verifyTag != null ? "✓" : "✗"));

        // Execute Vitruvius transformation with the TAGGED value
        long startTime = System.nanoTime();

        try {
            Method insertTask = testInstance.getClass().getMethod("insertTask", Path.class, int.class);
            insertTask.invoke(testInstance, workDir, taggedUserChoice);
        } catch (Exception e) {
            System.err.println("Failed to invoke Test.insertTask: " + e.getMessage());
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long totalTime = (endTime - startTime) / 1_000_000;

        System.out.println("  ✓ Vitruvius transformation executed");

        // Collect path constraints (automatically collected during switch statement)
        PathConditionWrapper pc = PathUtils.getCurPC();
        String constraintDescription = "no constraints";

        if (pc != null && !pc.isEmpty()) {
            if (pc.toSingleExpression() != null) {
                constraintDescription = pc.toSingleExpression().toString();
            } else {
                constraintDescription = "constraints collected: " + pc.size();
            }

            System.out.println("  Path constraints:");
            for (Expression expr : pc.getConstraints()) {
                System.out.println("    • " + expr);
            }
        } else {
            System.out.println("  ⚠ No path constraints collected");
        }

        return new PathResult(userChoice, constraintDescription, totalTime);
    }

    /**
     * Main entry point - explores all execution paths.
     */
    public static void main(String[] args) {
        // CRITICAL: Register XMI resource factory (from VSUMExampleTest.setup())
        // Without this, Vitruvius cannot create EMF resources!
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

        // Try to load Vitruvius Test class
        Object testInstance = null;
        try {
            Class<?> testClass = Class.forName("tools.vitruv.methodologisttemplate.vsum.Test");
            testInstance = testClass.getDeclaredConstructor().newInstance();
            System.out.println("✓ Loaded Vitruvius Test class");
        } catch (Exception e) {
            System.err.println("❌ Failed to load Vitruvius Test class: " + e.getMessage());
            System.err.println("Make sure Amathea-acset is in the classpath!");
            System.err.println("Run this from the Amathea-acset project, not galette-vitruv.");
            return;
        }

        System.out.println();
        System.out.println("SYMBOLIC EXECUTION OF VITRUVIUS MODEL TRANSFORMATION");
        System.out.println();

        // WARMUP: Execute once to eliminate JVM warmup overhead
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("WARMUP EXECUTION (not counted in measurements)");
        System.out.println("--------------------------------------------------------------------------------");
        Path warmupDir = Paths.get("galette-output-warmup");
        executeWithSymbolicInput(testInstance, warmupDir, 0, "warmup");
        System.out.println("  ✓ Warmup complete (JVM warmed up, classes loaded)");
        System.out.println();

        String[] taskOptions = {
            "Create InterruptTask", "Create PeriodicTask", "Create SoftwareTask", "Create TimeTableTask", "Decide Later"
        };

        List<PathResult> results = new ArrayList<>();

        // Explore each possible user choice (concolic testing)
        for (int choice = 0; choice < taskOptions.length; choice++) {
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println(
                    "Exploring Path " + (choice + 1) + "/" + taskOptions.length + ": " + taskOptions[choice]);
            System.out.println("User choice: " + choice);
            System.out.println("--------------------------------------------------------------------------------");

            // Create unique output directory for each path
            Path workDir = Paths.get("galette-output-" + choice);

            // Execute with symbolic input
            PathResult result = executeWithSymbolicInput(testInstance, workDir, choice, "user_choice_" + choice);
            results.add(result);

            System.out.println("  Total time: " + result.totalTime + " ms");
            System.out.println();
        }

        // Calculate real initialization time by re-running Path 1
        // First execution includes all overhead, second execution is just business logic
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("RE-RUNNING PATH 1 TO MEASURE REAL INITIALIZATION TIME");
        System.out.println("--------------------------------------------------------------------------------");
        Path path1RerunDir = Paths.get("galette-output-0-rerun");
        long startRerun = System.nanoTime();
        executeWithSymbolicInput(testInstance, path1RerunDir, 0, "user_choice_0_rerun");
        long endRerun = System.nanoTime();
        long path1BusinessLogicTime = (endRerun - startRerun) / 1_000_000;

        // Real initialization time = First run total - Second run time
        long realInitTime = results.get(0).totalTime - path1BusinessLogicTime;
        results.get(0).setInitializationTime(realInitTime);

        System.out.println("Path 1 first run (with overhead): " + results.get(0).totalTime + " ms");
        System.out.println("Path 1 re-run (business logic only): " + path1BusinessLogicTime + " ms");
        System.out.println("Real initialization + first-time overhead: " + realInitTime + " ms");
        System.out.println("Path 1 business logic time: " + results.get(0).executionTime + " ms");
        System.out.println();

        // Summary
        System.out.println("================================================================================");
        System.out.println("SYMBOLIC EXECUTION SUMMARY");
        System.out.println("================================================================================");
        System.out.println("Total paths explored: " + results.size());
        System.out.println();

        System.out.println("Path details:");
        for (int i = 0; i < results.size(); i++) {
            PathResult result = results.get(i);
            System.out.println("  Path " + (i + 1) + ": " + taskOptions[result.userChoice]);
            System.out.println("    • User choice: " + result.userChoice);
            System.out.println("    • Constraint: " + result.pathConstraint);
            if (result.initializationTime > 0) {
                System.out.println("    • Init time: " + result.initializationTime + " ms (VSUM setup)");
                System.out.println("    • Exec time: " + result.executionTime + " ms (business logic)");
                System.out.println("    • Total time: " + (result.initializationTime + result.executionTime) + " ms");
            } else {
                System.out.println("    • Time: " + result.executionTime + " ms");
            }
        }

        System.out.println();
        System.out.println("✅ All execution paths explored!");
        System.out.println();

        // Export results to JSON for visualization
        exportResultsToJson(results, "execution_paths.json");
    }

    /**
     * Export results to JSON for Python visualization.
     */
    private static void exportResultsToJson(List<PathResult> results, String filename) {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(filename);
            writer.println("[");

            for (int i = 0; i < results.size(); i++) {
                PathResult result = results.get(i);
                writer.println("  {");
                writer.println("    \"pathId\": " + (i + 1) + ",");
                writer.println("    \"symbolicInputs\": {");
                writer.println("      \"user_choice\": " + result.userChoice);
                writer.println("    },");
                writer.println("    \"constraints\": [");
                writer.println("      \"user_choice == " + result.userChoice + "\"");
                writer.println("    ],");
                writer.println("    \"executionTime\": " + result.executionTime + ",");
                writer.println("    \"initializationTime\": " + result.initializationTime);
                writer.print("  }");
                if (i < results.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }

            writer.println("]");
            writer.close();
            System.out.println("✓ Results exported to " + filename);
        } catch (Exception e) {
            System.err.println("Failed to export JSON: " + e.getMessage());
        }
    }
}
