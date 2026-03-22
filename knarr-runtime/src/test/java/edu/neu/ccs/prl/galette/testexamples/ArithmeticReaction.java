package edu.neu.ccs.prl.galette.testexamples;

/**
 * Simulates a Vitruvius reaction that performs arithmetic on a symbolic input
 * before branching. This exercises Knarr-style expression propagation.
 *
 * Unlike {@link SimpleReaction} which branches directly on the symbolic value,
 * this class computes derived values and branches on those — requiring full
 * expression propagation to recover the compound predicate.
 *
 * Examples:
 *   input=10 → score = 10*2 + 5 = 25 → "low"
 *   input=40 → score = 40*2 + 5 = 85 → "high"
 *   input=25 → score = 25*2 + 5 = 55 → "medium"
 */
public class ArithmeticReaction {

    /**
     * Computes a derived score from the input and branches on it.
     * score = input * 2 + 5
     *
     * Expected branch predicates (with expression propagation):
     *   GT(ADD(MUL(var(input), const(2)), const(5)), const(70)) for "high"
     *   LT(ADD(MUL(var(input), const(2)), const(5)), const(30)) for "low"
     */
    public static String executeWithArithmetic(int input) {
        int score = input * 2 + 5;

        if (score > 70) {
            return "high";
        } else if (score < 30) {
            return "low";
        } else {
            return "medium";
        }
    }

    /**
     * Branches on the difference of two symbolic inputs.
     * diff = a - b
     *
     * Expected: GT(SUB(var(a), var(b)), const(0)) for "a_greater"
     */
    public static String executeWithTwoInputs(int a, int b) {
        int diff = a - b;

        if (diff > 0) {
            return "a_greater";
        } else if (diff < 0) {
            return "b_greater";
        } else {
            return "equal";
        }
    }

    /**
     * Complex expression: ((a + b) * c) - threshold
     * Models a brake force calculation where multiple parameters combine.
     */
    public static String executeBrakeForceCalc(int pistonArea, int pressure, int friction) {
        int force = (pistonArea + pressure) * friction;

        if (force > 1000) {
            return "high_force";
        } else if (force > 500) {
            return "medium_force";
        } else {
            return "low_force";
        }
    }
}
