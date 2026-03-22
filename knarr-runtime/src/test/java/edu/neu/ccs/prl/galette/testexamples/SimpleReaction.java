package edu.neu.ccs.prl.galette.testexamples;

/**
 * Simulates a Vitruvius reaction with if-interval branches.
 *
 * This class is in a package NOT excluded from bytecode interception,
 * so its comparisons will be intercepted by ComparisonInterceptionTransformer.
 *
 * Interval partition (mimics the brake profile choice):
 *   value < 0      → skip
 *   0  <= value < 34  → category A
 *   34 <= value < 67  → category B
 *   67 <= value       → category C
 */
public class SimpleReaction {

    public static String execute(int value) {
        if (value < 0) {
            return "skip";
        } else if (value < 34) {
            return "category_A";
        } else if (value < 67) {
            return "category_B";
        } else {
            return "category_C";
        }
    }
}
