package edu.neu.ccs.prl.galette.vitruvius;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathExplorer;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import edu.neu.ccs.prl.galette.internal.runtime.Tainter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Helper class for Vitruvius path exploration utilities.
 * Provides common functionality for both single and multi-variable path exploration.
 */
public class AutomaticVitruvPathExplorationHelper {

    /**
     * Verify that Galette instrumentation is working.
     * Exits the program if instrumentation is not functional.
     */
    public static void verifyInstrumentation() {
        System.out.println("[PathExplorationHelper] Checking instrumentation...");
        try {
            Integer testValue = 42;
            Tag testTag = Tag.of("test");
            Integer taggedTest = Tainter.setTag(testValue, testTag);
            Tag retrievedTag = Tainter.getTag(taggedTest);

            if (retrievedTag == null) {
                System.err.println("ERROR: Instrumentation is NOT working! Tainter.getTag() returned null.");
                System.err.println("The program must be run with the instrumented JVM and Galette agent.");
                System.err.println("Exiting - symbolic execution will not work without instrumentation.");
                System.exit(1);
            } else {
                System.out.println("✓ Instrumentation is working. Tag retrieved: "
                        + retrievedTag.getLabels()[0]);
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to verify instrumentation: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initialize EMF resource factories for XMI handling.
     */
    public static void initializeEMF() {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
    }

    /**
     * Load and instantiate the Vitruvius Test class.
     *
     * @return Instance of the Test class
     * @throws RuntimeException if class cannot be loaded
     */
    public static Object loadVitruviusTestClass() {
        try {
            Class<?> testClass = Class.forName("tools.vitruv.methodologisttemplate.vsum.Test");
            Object testInstance = testClass.getDeclaredConstructor().newInstance();
            System.out.println("[PathExplorationHelper] Loaded Vitruvius Test class");
            return testInstance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Vitruvius Test class: " + e.getMessage(), e);
        }
    }

    /**
     * Create a unique working directory for output.
     * Deletes existing directory if present to avoid conflicts.
     *
     * @param prefix Directory prefix (e.g., "galette-output-automatic")
     * @param suffixes Additional path components
     * @return Path to created directory
     */
    public static Path createWorkingDirectory(String prefix, Object... suffixes) {
        StringBuilder pathName = new StringBuilder(prefix);
        for (Object suffix : suffixes) {
            pathName.append("-").append(suffix);
        }

        Path workDir = Paths.get(pathName.toString());

        try {
            if (Files.exists(workDir)) {
                deleteDirectory(workDir);
            }
            Files.createDirectories(workDir);
        } catch (IOException e) {
            System.err.println("Warning: Could not create working directory: " + e.getMessage());
        }

        return workDir;
    }

    /**
     * Recursively delete a directory and all its contents.
     *
     * @param dir Directory to delete
     */
    public static void deleteDirectory(Path dir) {
        if (Files.exists(dir)) {
            try {
                Files.walk(dir).sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Ignore errors during deletion
                    }
                });
            } catch (IOException e) {
                System.err.println("Warning: Could not delete directory: " + e.getMessage());
            }
        }
    }

    /**
     * Export single-variable path exploration results to JSON.
     *
     * @param paths List of explored paths
     * @param filename Output filename
     */
    public static void exportSingleVarResults(List<PathExplorer.PathRecord> paths, String filename) {
        System.out.println("\n[PathExplorationHelper:exportResultsToJson] 📄 Results exported to: " + filename);

        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println("[");

            for (int i = 0; i < paths.size(); i++) {
                PathExplorer.PathRecord path = paths.get(i);

                writer.println("  {");
                writer.println("    \"pathId\": " + (i + 1) + ",");
                writer.println("    \"symbolicInputs\": {");

                // Write inputs
                int inputCount = 0;
                for (Map.Entry<String, Object> entry : path.inputs.entrySet()) {
                    writer.print("      \"" + entry.getKey() + "\": ");
                    if (entry.getValue() instanceof String) {
                        writer.print("\"" + escapeJson(entry.getValue().toString()) + "\"");
                    } else {
                        writer.print(entry.getValue());
                    }

                    if (++inputCount < path.inputs.size()) {
                        writer.println(",");
                    } else {
                        writer.println();
                    }
                }

                writer.println("    },");
                writer.println("    \"pathConstraints\": [");

                // Write constraints
                for (int j = 0; j < path.constraints.size(); j++) {
                    writer.print("      \"" + escapeJson(path.constraints.get(j).toString()) + "\"");
                    if (j < path.constraints.size() - 1) {
                        writer.println(",");
                    } else {
                        writer.println();
                    }
                }

                writer.println("    ],");
                writer.println("    \"numConstraints\": " + path.constraints.size() + ",");
                writer.println("    \"executionTimeMs\": " + path.executionTimeMs);
                writer.print("  }");

                if (i < paths.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }

            writer.println("]");
            writer.flush();

        } catch (IOException e) {
            System.err.println("Error writing results to JSON: " + e.getMessage());
        }
    }

    /**
     * Export multi-variable path exploration results to JSON.
     * Variable names are extracted from the path records.
     *
     * @param paths List of explored paths
     * @param filename Output filename
     */
    public static void exportMultiVarResults(List<PathExplorer.PathRecord> paths, String filename) {
        try {
            // Extract variable names from the first path record
            List<String> variableNames = new java.util.ArrayList<>();
            if (!paths.isEmpty() && paths.get(0).inputs != null) {
                variableNames.addAll(paths.get(0).inputs.keySet());
                java.util.Collections.sort(variableNames); // Sort for consistent ordering
            }

            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"total_paths\": ").append(paths.size()).append(",\n");
            json.append("  \"num_variables\": ").append(variableNames.size()).append(",\n");
            json.append("  \"variable_names\": [");

            for (int i = 0; i < variableNames.size(); i++) {
                json.append("\"").append(variableNames.get(i)).append("\"");
                if (i < variableNames.size() - 1) json.append(", ");
            }
            json.append("],\n");

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

            System.out.println(
                    "\n[PathExplorationHelper:exportMultiVarResults] Saved results to: " + outputPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println(
                    "[PathExplorationHelper:exportMultiVarResults] Error saving JSON results: " + e.getMessage());
        }
    }

    /**
     * Escape special characters for JSON output.
     *
     * @param str String to escape
     * @return Escaped string
     */
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
