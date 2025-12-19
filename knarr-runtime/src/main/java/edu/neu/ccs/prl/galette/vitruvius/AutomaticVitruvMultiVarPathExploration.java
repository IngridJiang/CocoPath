package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.*;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import edu.neu.ccs.prl.galette.internal.runtime.Tainter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import za.ac.sun.cs.green.expr.Expression;

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

        // Register XMI resource factory (CRITICAL for EMF resource creation)
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());

        try {
            // Load Vitruvius Test class
            Class<?> testClass = Class.forName("tools.vitruv.methodologisttemplate.vsum.Test");
            Object testInstance = testClass.getDeclaredConstructor().newInstance();

            // Setup path explorer
            PathExplorer explorer = new PathExplorer();

            // Variable names for two user choices
            List<String> variableNames = Arrays.asList("user_choice_1", "user_choice_2");

            // Initial values (start with 0, 0)
            List<Integer> initialValues = Arrays.asList(0, 0);

            // Explore all paths
            System.out.println("[AutomaticVitruvMultiVarPathExploration:main] Starting multi-variable path exploration...");

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
            saveResultsToJson(paths, "execution_paths_multivar.json");

            System.out.println("\n[AutomaticVitruvMultiVarPathExploration:main] Complete ");
            System.out.println("[AutomaticVitruvMultiVarPathExploration:main] Results saved to: execution_paths_multivar.json");
            System.out.println("[AutomaticVitruvMultiVarPathExploration:main] Generated models saved to: galette-output-multivar-*/");

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

        // NEW: Try to read tags from inputs to get variable names and expressions dynamically
        // First try Tainter.getTag (works with javaagent)
        Tag tag1 = Tainter.getTag(taggedInput1);
        Tag tag2 = Tainter.getTag(taggedInput2);

        // Fallback 1: try PathExplorer.getTagForVariable (works without javaagent, uses ThreadLocal)
        if (tag1 == null && variableNames.size() > 0) {
            tag1 = PathExplorer.getTagForVariable(variableNames.get(0));
        }
        if (tag2 == null && variableNames.size() > 1) {
            tag2 = PathExplorer.getTagForVariable(variableNames.get(1));
        }

        // Fallback 2: try GaletteSymbolicator.getTagForValue (may not work with duplicate values)
        if (tag1 == null) {
            tag1 = GaletteSymbolicator.getTagForValue(taggedInput1);
        }
        if (tag2 == null) {
            tag2 = GaletteSymbolicator.getTagForValue(taggedInput2);
        }

        // Use variable names from parameter, falling back to defaults if not provided
        String varName1 = variableNames.size() > 0 ? variableNames.get(0) : "default1";
        String varName2 = variableNames.size() > 1 ? variableNames.get(1) : "default2";
        Expression symbolicExpr1 = null;
        Expression symbolicExpr2 = null;

        // test whether tags survive boxing and unboxing
        validateTagAfterRebox(taggedInput1);

        if (tag1 != null && tag1.size() > 0) {
            // Tag-aware mode: extract variable name and expression from tag
            Object[] labels1 = tag1.getLabels();
            String label1 = labels1[0].toString();

            // Extract variable name from label (remove _iterN suffix)
            // Label format: "user_choice_1_iter0" -> extract "user_choice_1"
            if (label1.contains("_iter")) {
                varName1 = label1.substring(0, label1.lastIndexOf("_iter"));
            } else {
                varName1 = label1;
            }

            // Get the symbolic expression associated with this tag
            symbolicExpr1 = GaletteSymbolicator.getExpressionForTag(tag1);

            System.out.println(
                    "[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] ✓ Tag detected for input 1: label = \"" + label1 + "\", variable name = \"" + varName1 + "\"");
            if (symbolicExpr1 != null) {
                System.out.println("[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs]   Symbolic expression: " + symbolicExpr1);
            }
        } else {

            System.out.println("[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] No tag found for input 1");
        }

        if (tag2 != null && tag2.size() > 0) {
            // Tag-aware mode: extract variable name and expression from tag
            Object[] labels2 = tag2.getLabels();
            String label2 = labels2[0].toString();

            // Extract variable name from label (remove _iterN suffix)
            // Label format: "user_choice_2_iter0" -> extract "user_choice_2"
            if (label2.contains("_iter")) {
                varName2 = label2.substring(0, label2.lastIndexOf("_iter"));
            } else {
                varName2 = label2;
            }

            // Get the symbolic expression associated with this tag
            symbolicExpr2 = GaletteSymbolicator.getExpressionForTag(tag2);

            System.out.println(
                    "[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] ✓ Tag detected for input 2: label = \"" + label2 + "\", variable name = \"" + varName2 + "\"");
            if (symbolicExpr2 != null) {
                System.out.println("[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs]   Symbolic expression: " + symbolicExpr2);
            }
        } else {
            System.out.println("[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] No tag found for input 2");
        }

        // Extract concrete values for display/directory naming
        int input1 = taggedInput1.intValue();
        int input2 = taggedInput2.intValue();

        // Create unique working directory for this path, cleaning any stale outputs to avoid proxy issues
        Path workDir = Paths.get("galette-output-multivar-" + input1 + "_" + input2);

        try {
            if (Files.exists(workDir)) {
                deleteDirectory(workDir);
            }
            Files.createDirectories(workDir);
        } catch (IOException e) {
            System.err.println("[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Warning: Could not create working directory: " + e.getMessage());
        }

        // Reset path condition
        PathUtils.resetPC();

        // Step 1: Add domain constraints for BOTH variables

        PathUtils.addIntDomainConstraint(varName1, 0, 5);
        PathUtils.addIntDomainConstraint(varName2, 0, 5);

        // Step 2: Record path constraints for BOTH variables

        PathUtils.addSwitchConstraint(varName1, taggedInput1);
        PathUtils.addSwitchConstraint(varName2, taggedInput2);

        if (DEBUG) {
            System.out.println("[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Constraints will be recorded by reactions via SymbolicComparison");
        }

        try {
            // Step 3: Execute Vitruvius transformation with BOTH inputs
            Method insertTwoTasks =
                    testInstance.getClass().getMethod("insertTwoTasks", Path.class, Integer.class, Integer.class);

            if (DEBUG) {
                System.out.println(
                        "[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Invoking insertTwoTasks(" + workDir + ", " + input1 + ", " + input2 + ")");
            }

            insertTwoTasks.invoke(testInstance, workDir, taggedInput1, taggedInput2);

        } catch (Exception e) {
            System.err.println("[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Error executing Vitruvius transformation:");
            e.printStackTrace();
            // Return collected constraints even if execution failed
        }

        // Step 4: Return collected constraints
        PathConditionWrapper pc = PathUtils.getCurPC();

        if (DEBUG && pc != null) {
            System.out.println("[AutomaticVitruvMultiVarPathExploration:executeVitruvWithTwoInputs] Collected " + pc.getConstraints().size() + " total constraints");
        }

        return pc;
    }

    private static void validateTagAfterRebox(int primitiveTaggedInput) {
        // int primitiveTaggedInput = taggedInput.intValue();
        Tag tagAfterUnbox = Tainter.getTag(primitiveTaggedInput);
        if (tagAfterUnbox != null && tagAfterUnbox.size() > 0) {
            System.out.println("[AutomaticVitruvMultiVarPathExploration:validateTagAfterRebox] ✓ Tag survived unboxing for input " + primitiveTaggedInput);
        } else {
            System.out.println("[AutomaticVitruvMultiVarPathExploration:validateTagAfterRebox] ✗ Tag lost during unboxing for input " + primitiveTaggedInput);
        }

        Integer reboxedTaggedInput = Integer.valueOf(primitiveTaggedInput);
        Tag tagAfterRebox = Tainter.getTag(reboxedTaggedInput);
        if (tagAfterRebox != null && tagAfterRebox.size() > 0) {
            System.out.println("[AutomaticVitruvMultiVarPathExploration:validateTagAfterRebox] ✓ Tag survived reboxing for input " + primitiveTaggedInput);
        } else {
            System.out.println("[AutomaticVitruvMultiVarPathExploration:validateTagAfterRebox] ✗ Tag lost during reboxing for input " + primitiveTaggedInput);
        }
    }

    /**
     * Save path exploration results to JSON file (manually formatted).
     */
    private static void saveResultsToJson(List<PathExplorer.PathRecord> paths, String filename) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"total_paths\": ").append(paths.size()).append(",\n");
            json.append("  \"num_variables\": 2,\n");
            json.append("  \"variable_names\": [\"user_choice_1\", \"user_choice_2\"],\n");
            json.append("  \"paths\": [\n");

            for (int i = 0; i < paths.size(); i++) {
                PathExplorer.PathRecord path = paths.get(i);
                json.append("    {\n");
                json.append("      \"path_id\": ").append(path.pathId).append(",\n");

                // Format inputs
                json.append("      \"inputs\": {");
                boolean first = true;
                for (Map.Entry<String, Object> entry : path.inputs.entrySet()) {
                    if (!first) json.append(", ");
                    json.append("\"").append(entry.getKey()).append("\": ").append(entry.getValue());
                    first = false;
                }
                json.append("},\n");

                // Format constraints
                json.append("      \"constraints\": [");
                for (int j = 0; j < path.constraints.size(); j++) {
                    json.append("\"")
                            .append(escapeJson(path.constraints.get(j).toString()))
                            .append("\"");
                    if (j < path.constraints.size() - 1) json.append(", ");
                }
                json.append("],\n");

                json.append("      \"num_constraints\": ")
                        .append(path.constraints.size())
                        .append(",\n");
                json.append("      \"execution_time_ms\": ")
                        .append(path.executionTimeMs)
                        .append("\n");
                json.append("    }");
                if (i < paths.size() - 1) json.append(",");
                json.append("\n");
            }

            json.append("  ]\n");
            json.append("}\n");

            Path outputPath = Paths.get(filename);
            Files.write(outputPath, json.toString().getBytes());

            System.out.println("\n[AutomaticVitruvMultiVarPathExploration:saveResultsToJson] Saved results to: " + outputPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("[AutomaticVitruvMultiVarPathExploration:saveResultsToJson] Error saving JSON results: " + e.getMessage());
        }
    }

    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Recursively delete directory and its contents.
     */
    private static void deleteDirectory(Path dir) throws IOException {
        if (Files.isDirectory(dir)) {
            Files.walk(dir).sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    // Ignore
                }
            });
        }
    }
}
