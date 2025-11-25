package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.Tag;
import edu.neu.ccs.prl.galette.Tainter;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathConditionWrapper;
import edu.neu.ccs.prl.galette.concolic.knarr.runtime.PathUtils;
import org.junit.jupiter.api.Test;
import za.ac.sun.cs.green.expr.Expression;

/**
 * Test that switch statements collect path constraints when operating on tagged values.
 */
public class SwitchConstraintTest {

    @Test
    public void testSwitchCollectsConstraints() {
        // Reset state
        GaletteSymbolicator.reset();
        PathUtils.resetPC();

        // Create symbolic value
        Tag symbolicTag = GaletteSymbolicator.makeSymbolicInt("test_value", 2);
        int taggedValue = Tainter.setTag(2, symbolicTag);

        // Verify tag was applied
        Tag verify = Tainter.getTag(taggedValue);
        assertNotNull(verify, "Tag should be applied");

        // Execute switch statement with tagged value
        String result = executeSwitch(taggedValue);

        // Collect constraints
        PathConditionWrapper pc = PathUtils.getCurPC();

        System.out.println("Result: " + result);
        System.out.println("Constraints collected: " + (pc != null && !pc.isEmpty()));

        if (pc != null && !pc.isEmpty()) {
            System.out.println("Number of constraints: " + pc.size());
            for (Expression expr : pc.getConstraints()) {
                System.out.println("  • " + expr);
            }
        }

        // Verify constraints were collected
        assertNotNull(pc, "PathCondition should not be null");
        assertFalse(pc.isEmpty(), "Constraints should be collected from switch statement");
    }

    private String executeSwitch(int value) {
        switch (value) {
            case 0:
                return "zero";
            case 1:
                return "one";
            case 2:
                return "two";
            case 3:
                return "three";
            default:
                return "other";
        }
    }
}
