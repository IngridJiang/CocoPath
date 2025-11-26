package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration test for automatic constraint collection from real switch statements.
 *
 * <p>This test demonstrates end-to-end symbolic execution with automatic
 * path constraint collection, simulating the Vitruvius use case.
 *
 * <p><b>Note</b>: For full bytecode instrumentation testing, this must run with:
 * <ul>
 *   <li>-javaagent:galette-agent.jar</li>
 *   <li>-Dgalette.symbolic.enabled=true</li>
 * </ul>
 */
class IntegrationSwitchTest {

    /**
     * Simulates the Vitruvius user interaction pattern.
     * User selects a task type, and the appropriate creation method is called.
     */
    enum TaskType {
        INTERRUPT,
        PERIODIC,
        SOFTWARE,
        TIMETABLE,
        DECIDE_LATER
    }

    /** Result holder for testing */
    private static TaskType createdTaskType;

    @BeforeEach
    void setUp() {
        PathUtils.reset();
        GaletteSymbolicator.reset();
        createdTaskType = null;
    }

    @AfterEach
    void tearDown() {
        PathUtils.reset();
        GaletteSymbolicator.reset();
    }

    /**
     * Test manual constraint collection with switch statement.
     * This simulates the OLD way of doing things (before automatic extraction).
     *
     * NOTE: With the new TagPropagator bytecode instrumentation, BOTH domain constraints
     * and switch constraints are collected automatically! This test shows the manual approach
     * for comparison purposes only.
     */
    @Test
    void testManualSwitchConstraints() {
        // Create symbolic choice
        Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt("user_choice", 0);

        // Manual domain constraint (OLD WAY - now automatic via TagPropagator!)
        PathUtils.addIntDomainConstraint("user_choice", 0, 5);

        // Simulate user selection - case 0
        int choice = 0; // This would normally come from symbolicTag.getValue()

        // Manual constraint recording (OLD WAY - now automatic via TagPropagator!)
        PathUtils.addSwitchConstraint("user_choice", choice);

        // Execute switch
        processUserChoice(choice);

        // Verify the correct task was created
        assertEquals(TaskType.INTERRUPT, createdTaskType);

        // Verify constraint was collected
        PathConditionWrapper pc = PathUtils.getCurPC();
        assertFalse(pc.isEmpty(), "Path constraints should be collected");
        assertTrue(pc.size() >= 2, "Should have domain + switch constraints");
    }

    /**
     * Test automatic constraint collection (requires bytecode instrumentation).
     *
     * With -Dgalette.symbolic.enabled=true and proper instrumentation,
     * BOTH domain and switch constraints are collected automatically via TagPropagator!
     *
     * The TagPropagator.recordSwitchConstraint() method will:
     * 1. Call addIntDomainConstraintAuto(tag, min, max+1) to set the domain
     * 2. Call recordSwitchConstraintAuto(tag, caseValue) for the selected case
     */
    @Test
    void testAutomaticSwitchConstraints_Case0() {
        // Create symbolic choice
        Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt("user_choice", 0);

        // Simulate user selection - case 0
        int choice = 0;

        // No manual constraint recording needed!
        // (Would be automatic with full instrumentation)

        // For this unit test, simulate what the TagPropagator bytecode instrumentation would do:
        // 1. First, it adds the domain constraint from the switch min/max
        PathUtils.addIntDomainConstraintAuto(symbolicTag, 0, 5); // Domain: [0, 5)
        // 2. Then, it records the specific case constraint
        PathUtils.recordSwitchConstraintAuto(symbolicTag, choice);

        // Execute switch
        processUserChoice(choice);

        // Verify the correct task was created
        assertEquals(TaskType.INTERRUPT, createdTaskType);

        // Verify constraints were automatically collected
        PathConditionWrapper pc = PathUtils.getCurPC();
        assertFalse(pc.isEmpty(), "Path constraints should be collected automatically");
        assertTrue(pc.size() >= 2, "Should have domain + switch constraints (both automatic!)");
    }

    @Test
    void testAutomaticSwitchConstraints_Case1() {
        Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt("user_choice", 1);
        int choice = 1;

        // Automatic constraint collection (simulated)
        PathUtils.recordSwitchConstraintAuto(symbolicTag, choice);

        processUserChoice(choice);

        assertEquals(TaskType.PERIODIC, createdTaskType);
        assertFalse(PathUtils.getCurPC().isEmpty());
    }

    @Test
    void testAutomaticSwitchConstraints_Case2() {
        Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt("user_choice", 2);
        int choice = 2;

        PathUtils.recordSwitchConstraintAuto(symbolicTag, choice);

        processUserChoice(choice);

        assertEquals(TaskType.SOFTWARE, createdTaskType);
        assertFalse(PathUtils.getCurPC().isEmpty());
    }

    @Test
    void testAutomaticSwitchConstraints_Case3() {
        Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt("user_choice", 3);
        int choice = 3;

        PathUtils.recordSwitchConstraintAuto(symbolicTag, choice);

        processUserChoice(choice);

        assertEquals(TaskType.TIMETABLE, createdTaskType);
        assertFalse(PathUtils.getCurPC().isEmpty());
    }

    @Test
    void testAutomaticSwitchConstraints_Case4() {
        Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt("user_choice", 4);
        int choice = 4;

        PathUtils.recordSwitchConstraintAuto(symbolicTag, choice);

        processUserChoice(choice);

        assertEquals(TaskType.DECIDE_LATER, createdTaskType);
        assertFalse(PathUtils.getCurPC().isEmpty());
    }

    /**
     * Test that explores multiple paths in sequence.
     * This simulates what PathExplorer would do automatically.
     */
    @Test
    void testMultiplePathExploration() {
        int totalPaths = 5; // 5 different task types

        for (int path = 0; path < totalPaths; path++) {
            // Reset state for each path
            PathUtils.reset();
            createdTaskType = null;

            // Create symbolic variable for this path
            Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt("user_choice", path);

            // Automatic constraint collection
            PathUtils.recordSwitchConstraintAuto(symbolicTag, path);

            // Execute the switch
            processUserChoice(path);

            // Verify correct path was taken
            assertNotNull(createdTaskType, "A task should be created for path " + path);

            // Verify constraint was collected
            PathConditionWrapper pc = PathUtils.getCurPC();
            assertFalse(pc.isEmpty(), "Constraints should be collected for path " + path);

            if (GaletteSymbolicator.DEBUG) {
                System.out.println("Path " + path + ": " + createdTaskType + " - Constraints: " + pc);
            }
        }
    }

    /**
     * Simulates the Vitruvius transformation logic.
     * This is the business logic that should NOT need modification for symbolic execution.
     */
    private void processUserChoice(int userChoice) {
        // This switch statement would be automatically instrumented
        // to collect path constraints when symbolic execution is enabled

        switch (userChoice) {
            case 0:
                createInterruptTask();
                break;
            case 1:
                createPeriodicTask();
                break;
            case 2:
                createSoftwareTask();
                break;
            case 3:
                createTimeTableTask();
                break;
            case 4:
                decideLater();
                break;
            default:
                throw new IllegalArgumentException("Invalid choice: " + userChoice);
        }
    }

    // Simulated task creation methods (representing Vitruvius reactions)
    private void createInterruptTask() {
        createdTaskType = TaskType.INTERRUPT;
        if (GaletteSymbolicator.DEBUG) {
            System.out.println("Created InterruptTask");
        }
    }

    private void createPeriodicTask() {
        createdTaskType = TaskType.PERIODIC;
        if (GaletteSymbolicator.DEBUG) {
            System.out.println("Created PeriodicTask");
        }
    }

    private void createSoftwareTask() {
        createdTaskType = TaskType.SOFTWARE;
        if (GaletteSymbolicator.DEBUG) {
            System.out.println("Created SoftwareTask");
        }
    }

    private void createTimeTableTask() {
        createdTaskType = TaskType.TIMETABLE;
        if (GaletteSymbolicator.DEBUG) {
            System.out.println("Created TimeTableTask");
        }
    }

    private void decideLater() {
        createdTaskType = TaskType.DECIDE_LATER;
        if (GaletteSymbolicator.DEBUG) {
            System.out.println("Decided to choose later");
        }
    }
}
