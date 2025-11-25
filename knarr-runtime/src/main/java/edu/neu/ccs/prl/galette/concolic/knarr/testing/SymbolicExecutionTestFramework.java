package edu.neu.ccs.prl.galette.concolic.knarr.testing;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.*;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import za.ac.sun.cs.green.expr.*;

/**
 * Testing framework for symbolic execution components.
 *
 * Provides utilities for testing array symbolic execution, string symbolic execution,
 * coverage tracking, and constraint generation. Supports both unit testing and
 * integration testing scenarios.
 *
 * Key capabilities:
 * - Symbolic execution test scenarios
 * - Coverage validation
 * - Constraint verification
 * - Performance benchmarking
 * - Test case generation
 *
 * @purpose JUnit integration for symbolic execution tests
 * @feature Automatic path exploration in tests
 * @feature Test annotations for symbolic inputs
 *
 * @author Symbolic Execution Test Framework
 */
public class SymbolicExecutionTestFramework {

    private final List<TestCase> testCases = new ArrayList<>();
    private final AtomicInteger testCounter = new AtomicInteger(0);
    private final Map<String, Object> testContext = new HashMap<>();

    /**
     * Test execution results.
     */
    public static class TestResults {
        public int totalTests = 0;
        public int passedTests = 0;
        public int failedTests = 0;
        private final List<String> failures = new ArrayList<>();
        private final Map<String, Long> timings = new HashMap<>();

        public void recordTest(String testName, boolean passed, long durationMs, String failureMessage) {
            totalTests++;
            if (passed) {
                passedTests++;
            } else {
                failedTests++;
                failures.add(testName + ": " + failureMessage);
            }
            timings.put(testName, durationMs);
        }

        public double getPassRate() {
            return totalTests > 0 ? (double) passedTests / totalTests : 0.0;
        }

        public String getSummary() {
            return String.format(
                    "Test Results: %d/%d passed (%.1f%%), %d failed",
                    passedTests, totalTests, getPassRate() * 100, failedTests);
        }

        public List<String> getFailures() {
            return new ArrayList<>(failures);
        }

        public Map<String, Long> getTimings() {
            return new HashMap<>(timings);
        }
    }

    /**
     * Individual test case.
     */
    public static class TestCase {
        private final String name;
        private final Supplier<Boolean> testFunction;
        private final String description;

        public TestCase(String name, String description, Supplier<Boolean> testFunction) {
            this.name = name;
            this.description = description;
            this.testFunction = testFunction;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Supplier<Boolean> getTestFunction() {
            return testFunction;
        }
    }

    /**
     * Add a test case to the framework.
     *
     * @param name Test name
     * @param description Test description
     * @param testFunction Test function that returns true for pass, false for fail
     */
    public void addTest(String name, String description, Supplier<Boolean> testFunction) {
        testCases.add(new TestCase(name, description, testFunction));
    }

    /**
     * Run all test cases and return results.
     *
     * @return Test execution results
     */
    public TestResults runAllTests() {
        TestResults results = new TestResults();

        System.out.println("=== Running Symbolic Execution Tests ===");
        System.out.println();

        for (TestCase testCase : testCases) {
            System.out.printf("Running: %s - %s\n", testCase.getName(), testCase.getDescription());

            long startTime = System.currentTimeMillis();
            boolean passed = false;
            String failureMessage = null;

            try {
                passed = testCase.getTestFunction().get();
                if (!passed) {
                    failureMessage = "Test function returned false";
                }
            } catch (Exception e) {
                failureMessage = "Exception: " + e.getMessage();
            }

            long duration = System.currentTimeMillis() - startTime;
            results.recordTest(testCase.getName(), passed, duration, failureMessage);

            System.out.printf("  %s (%dms)\n", passed ? "✓ PASS" : "✗ FAIL", duration);
            if (!passed && failureMessage != null) {
                System.out.printf("    %s\n", failureMessage);
            }
        }

        System.out.println();
        System.out.println(results.getSummary());

        return results;
    }

    /**
     * Create standard test suite for array symbolic execution.
     *
     * @return Configured test framework
     */
    public static SymbolicExecutionTestFramework createArrayTestSuite() {
        SymbolicExecutionTestFramework framework = new SymbolicExecutionTestFramework();

        // Test basic array read
        framework.addTest("array_read_basic", "Basic array read with symbolic index", () -> {
            ArraySymbolicTracker tracker = new ArraySymbolicTracker();
            ArraySymbolicTracker.reset();

            int[] array = {10, 20, 30, 40, 50};
            Tag indexTag = Tag.of("test_index");
            Tag[] arrayTags = new Tag[array.length];

            Tag result = tracker.handleArrayRead(array, indexTag, 2, arrayTags, array[2]);
            return result != null; // Should produce symbolic result
        });

        // Test array write
        framework.addTest("array_write_basic", "Basic array write with symbolic value", () -> {
            ArraySymbolicTracker tracker = new ArraySymbolicTracker();

            int[] array = {0, 0, 0, 0};
            Tag valueTag = Tag.of("test_value");
            Tag[] arrayTags = new Tag[array.length];

            Tag result = tracker.handleArrayWrite(array, null, 2, valueTag, arrayTags, 42);
            return result != null; // Should produce symbolic result
        });

        // Test bounds checking
        framework.addTest("array_bounds_check", "Array bounds constraint generation", () -> {
            ArraySymbolicTracker tracker = new ArraySymbolicTracker();
            PathUtils.resetPC();

            int[] array = {1, 2, 3};
            Tag indexTag = Tag.of("bounds_index");
            Tag[] arrayTags = new Tag[array.length];

            tracker.handleArrayRead(array, indexTag, 1, arrayTags, array[1]);

            // Check that path condition has constraints
            PathConditionWrapper pc = PathUtils.getCurPC();
            return pc.size() > 0; // Should have bounds constraints
        });

        // Test object arrays
        framework.addTest("object_array_support", "Object array handling", () -> {
            ArraySymbolicTracker tracker = new ArraySymbolicTracker();

            String[] stringArray = {"hello", "world", "test"};
            Tag indexTag = Tag.of("string_index");
            Tag[] arrayTags = new Tag[stringArray.length];

            try {
                Tag result = tracker.handleArrayRead(stringArray, indexTag, 1, arrayTags, stringArray[1]);
                return true; // Should not throw exception
            } catch (Exception e) {
                return false;
            }
        });

        return framework;
    }

    /**
     * Create standard test suite for string symbolic execution.
     *
     * @return Configured test framework
     */
    public static SymbolicExecutionTestFramework createStringTestSuite() {
        SymbolicExecutionTestFramework framework = new SymbolicExecutionTestFramework();

        // Test string equals
        framework.addTest("string_equals", "String equality comparison", () -> {
            StringSymbolicTracker tracker = new StringSymbolicTracker();
            StringSymbolicTracker.reset();

            String str1 = "hello";
            String str2 = "hello";
            Tag str1Tag = Tag.of("string1");

            Tag result = tracker.handleEquals(str1, str2, str1Tag, null);
            return result != null; // Should produce symbolic result
        });

        // Test string length
        framework.addTest("string_length", "String length operation", () -> {
            StringSymbolicTracker tracker = new StringSymbolicTracker();

            String str = "test";
            Tag strTag = Tag.of("test_string");

            Tag result = tracker.handleLength(str, strTag);
            return result != null; // Should produce symbolic result
        });

        // Test string charAt
        framework.addTest("string_charAt", "String character access", () -> {
            StringSymbolicTracker tracker = new StringSymbolicTracker();

            String str = "hello";
            Tag strTag = Tag.of("char_string");
            tracker.registerSymbolicString(str, strTag, 0, str.length());

            Tag result = tracker.handleCharAt(str, 2, strTag, null);
            return result != null; // Should produce symbolic result
        });

        // Test string indexOf
        framework.addTest("string_indexOf", "String indexOf operation", () -> {
            StringSymbolicTracker tracker = new StringSymbolicTracker();

            String str = "hello world";
            Tag strTag = Tag.of("search_string");

            Tag result = tracker.handleIndexOf(str, "world", 0, strTag, null);
            return result != null; // Should produce symbolic result
        });

        // Test case conversion
        framework.addTest("string_case_conversion", "String case conversion", () -> {
            StringSymbolicTracker tracker = new StringSymbolicTracker();

            String str = "Hello";
            Tag strTag = Tag.of("case_string");

            Tag result = tracker.handleCaseConversion(str.toUpperCase(), str, true, strTag);
            return result != null; // Should produce symbolic result
        });

        return framework;
    }

    /**
     * Create test suite for coverage tracking.
     *
     * @return Configured test framework
     */
    public static SymbolicExecutionTestFramework createCoverageTestSuite() {
        SymbolicExecutionTestFramework framework = new SymbolicExecutionTestFramework();

        // Test basic code coverage
        framework.addTest("coverage_code_basic", "Basic code coverage tracking", () -> {
            CoverageTracker tracker = new CoverageTracker();
            tracker.reset();

            tracker.recordCodeCoverage(100);
            tracker.recordCodeCoverage(200);
            tracker.recordCodeCoverage(100); // Duplicate

            String stats = tracker.getCoverageStatistics();
            return stats.contains("blocks") && tracker.getCodeCoverageRatio() > 0;
        });

        // Test path coverage
        framework.addTest("coverage_path_basic", "Basic path coverage tracking", () -> {
            CoverageTracker tracker = new CoverageTracker();
            tracker.reset();

            tracker.recordPathCoverage(50);
            tracker.recordPathCoverage(75);

            return tracker.getPathCoverageRatio() > 0;
        });

        // Test branch coverage
        framework.addTest("coverage_branch", "Branch coverage tracking", () -> {
            CoverageTracker tracker = new CoverageTracker();
            tracker.reset();

            int branch1 = tracker.recordBranchCoverage(10, true);
            int branch2 = tracker.recordBranchCoverage(10, false);

            Map<String, CoverageTracker.BranchCoverage> branches = tracker.getBranchCoverageData();
            return branches.size() > 0 && branches.get("branch_10").isBothPathsCovered();
        });

        // Test method coverage
        framework.addTest("coverage_method", "Method coverage tracking", () -> {
            CoverageTracker tracker = new CoverageTracker();
            tracker.reset();

            tracker.recordMethodEntry("TestClass", "testMethod", "()V");
            tracker.recordMethodEntry("TestClass", "testMethod", "()V"); // Duplicate

            Map<String, Integer> methods = tracker.getMethodHitCounts();
            return methods.size() == 1 && methods.get("TestClass.testMethod()V") == 2;
        });

        // Test coverage export/import
        framework.addTest("coverage_serialization", "Coverage export and import", () -> {
            CoverageTracker tracker = new CoverageTracker();
            tracker.reset();

            tracker.recordCodeCoverage(42);
            tracker.recordMethodEntry("Test", "method", "()V");

            try {
                String filename = "/tmp/test_coverage.dat";
                tracker.exportCoverage(filename);
                CoverageTracker loaded = CoverageTracker.loadCoverage(filename);

                return loaded.getCoverageStatistics().contains("42")
                        || loaded.getMethodHitCounts().containsKey("Test.method()V");
            } catch (Exception e) {
                return false;
            }
        });

        return framework;
    }

    /**
     * Create comprehensive integration test suite.
     *
     * @return Configured test framework
     */
    public static SymbolicExecutionTestFramework createIntegrationTestSuite() {
        SymbolicExecutionTestFramework framework = new SymbolicExecutionTestFramework();

        // Test array-string integration
        framework.addTest("integration_array_string", "Array and string symbolic execution integration", () -> {
            ArraySymbolicTracker arrayTracker = new ArraySymbolicTracker();
            StringSymbolicTracker stringTracker = new StringSymbolicTracker();

            ArraySymbolicTracker.reset();
            StringSymbolicTracker.reset();

            String[] stringArray = {"hello", "world"};
            Tag[] arrayTags = new Tag[stringArray.length];
            Tag indexTag = Tag.of("array_index");

            // Array read with symbolic index
            Tag readResult = arrayTracker.handleArrayRead(stringArray, indexTag, 1, arrayTags, stringArray[1]);

            // String operation on array element
            Tag lengthResult = stringTracker.handleLength(stringArray[1], readResult);

            return readResult != null && lengthResult != null;
        });

        // Test coverage integration
        framework.addTest("integration_coverage", "Coverage tracking integration", () -> {
            CoverageTracker coverage = new CoverageTracker();
            coverage.reset();

            // Simulate symbolic execution with coverage
            coverage.recordMethodEntry("TestClass", "symbolicMethod", "()V");
            coverage.recordCodeCoverage(100);
            coverage.recordBranchCoverage(50, true);

            String stats = coverage.getCoverageStatistics();
            return stats.contains("Method Coverage: 1") && stats.contains("Branch Coverage: 1");
        });

        // Test constraint generation
        framework.addTest("integration_constraints", "Constraint generation integration", () -> {
            PathUtils.resetPC();
            ArraySymbolicTracker arrayTracker = new ArraySymbolicTracker();

            int[] array = {1, 2, 3, 4, 5};
            Tag indexTag = Tag.of("constraint_index");
            Tag[] arrayTags = new Tag[array.length];

            arrayTracker.handleArrayRead(array, indexTag, 2, arrayTags, array[2]);

            PathConditionWrapper pc = PathUtils.getCurPC();
            return pc.size() >= 2; // Should have bounds constraints
        });

        return framework;
    }

    /**
     * Run comprehensive test suite covering all components.
     *
     * @return Combined test results
     */
    public static TestResults runComprehensiveTests() {
        System.out.println("=== Comprehensive Symbolic Execution Test Suite ===");
        System.out.println();

        List<TestResults> allResults = new ArrayList<>();

        // Run array tests
        System.out.println("--- Array Symbolic Execution Tests ---");
        SymbolicExecutionTestFramework arrayTests = createArrayTestSuite();
        allResults.add(arrayTests.runAllTests());
        System.out.println();

        // Run string tests
        System.out.println("--- String Symbolic Execution Tests ---");
        SymbolicExecutionTestFramework stringTests = createStringTestSuite();
        allResults.add(stringTests.runAllTests());
        System.out.println();

        // Run coverage tests
        System.out.println("--- Coverage Tracking Tests ---");
        SymbolicExecutionTestFramework coverageTests = createCoverageTestSuite();
        allResults.add(coverageTests.runAllTests());
        System.out.println();

        // Run integration tests
        System.out.println("--- Integration Tests ---");
        SymbolicExecutionTestFramework integrationTests = createIntegrationTestSuite();
        allResults.add(integrationTests.runAllTests());
        System.out.println();

        // Combine results
        TestResults combined = new TestResults();
        for (TestResults result : allResults) {
            combined.totalTests += result.totalTests;
            combined.passedTests += result.passedTests;
            combined.failedTests += result.failedTests;
            combined.failures.addAll(result.failures);
            combined.timings.putAll(result.timings);
        }

        System.out.println("=== Overall Test Results ===");
        System.out.println(combined.getSummary());

        if (!combined.failures.isEmpty()) {
            System.out.println("\nFailures:");
            for (String failure : combined.failures) {
                System.out.println("  " + failure);
            }
        }

        return combined;
    }
}
