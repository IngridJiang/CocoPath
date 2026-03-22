package edu.neu.ccs.prl.galette.interception;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal runtime for bytecode-level comparison interception.
 *
 * This class is referenced by ComparisonInterceptorVisitor at the bytecode level.
 * It must be jlink-safe: no complex static initializers, no System.out, no shutdown hooks.
 *
 * Collected constraints are retrieved via flush() by GalettePathConstraintBridge in knarr-runtime.
 */
public final class InterceptionPathUtils {

    private static volatile boolean enabled =
            "true".equals(System.getProperty("galette.concolic.interception.enabled"));

    // No static initializer side effects — must be jlink-safe

    private static final ThreadLocal<List<Constraint>> PATH_CONDITIONS = ThreadLocal.withInitial(ArrayList::new);

    // Recursion guard
    private static final ThreadLocal<Integer> RECURSION_DEPTH = ThreadLocal.withInitial(() -> 0);
    private static final int MAX_RECURSION_DEPTH = 10;

    private static void enterMethod() {
        RECURSION_DEPTH.set(RECURSION_DEPTH.get() + 1);
    }

    private static void exitMethod() {
        int d = RECURSION_DEPTH.get();
        if (d > 0) RECURSION_DEPTH.set(d - 1);
    }

    private static boolean isEnabled() {
        return enabled && RECURSION_DEPTH.get() < MAX_RECURSION_DEPTH;
    }

    // ===== Instrumented comparison operations =====

    public static int instrumentedLcmp(long v1, long v2) {
        enterMethod();
        try {
            int result = Long.compare(v1, v2);
            if (isEnabled()) {
                PATH_CONDITIONS.get().add(new Constraint(v1, v2, "LCMP", result));
            }
            return result;
        } finally {
            exitMethod();
        }
    }

    public static int instrumentedFcmpl(float v1, float v2) {
        enterMethod();
        try {
            int result = Float.isNaN(v1) || Float.isNaN(v2) ? -1 : Float.compare(v1, v2);
            if (isEnabled()) {
                PATH_CONDITIONS.get().add(new Constraint(v1, v2, "FCMPL", result));
            }
            return result;
        } finally {
            exitMethod();
        }
    }

    public static int instrumentedFcmpg(float v1, float v2) {
        enterMethod();
        try {
            int result = Float.isNaN(v1) || Float.isNaN(v2) ? 1 : Float.compare(v1, v2);
            if (isEnabled()) {
                PATH_CONDITIONS.get().add(new Constraint(v1, v2, "FCMPG", result));
            }
            return result;
        } finally {
            exitMethod();
        }
    }

    public static int instrumentedDcmpl(double v1, double v2) {
        enterMethod();
        try {
            int result = Double.isNaN(v1) || Double.isNaN(v2) ? -1 : Double.compare(v1, v2);
            if (isEnabled()) {
                PATH_CONDITIONS.get().add(new Constraint(v1, v2, "DCMPL", result));
            }
            return result;
        } finally {
            exitMethod();
        }
    }

    public static int instrumentedDcmpg(double v1, double v2) {
        enterMethod();
        try {
            int result = Double.isNaN(v1) || Double.isNaN(v2) ? 1 : Double.compare(v1, v2);
            if (isEnabled()) {
                PATH_CONDITIONS.get().add(new Constraint(v1, v2, "DCMPG", result));
            }
            return result;
        } finally {
            exitMethod();
        }
    }

    /**
     * Record an integer comparison without affecting the result.
     * Called via DUP2 before the original IF_ICMP* instruction,
     * so the stack layout is preserved exactly.
     */
    public static void recordIcmp(int v1, int v2, String op) {
        if (!enabled) return;
        enterMethod();
        try {
            if (isEnabled()) {
                // Compute the result based on the operation
                int result;
                switch (op) {
                    case "EQ":
                        result = (v1 == v2) ? 1 : 0;
                        break;
                    case "NE":
                        result = (v1 != v2) ? 1 : 0;
                        break;
                    case "LT":
                        result = (v1 < v2) ? 1 : 0;
                        break;
                    case "GE":
                        result = (v1 >= v2) ? 1 : 0;
                        break;
                    case "GT":
                        result = (v1 > v2) ? 1 : 0;
                        break;
                    case "LE":
                        result = (v1 <= v2) ? 1 : 0;
                        break;
                    default:
                        result = 0;
                }
                PATH_CONDITIONS.get().add(new Constraint(v1, v2, op, result));
            }
        } finally {
            exitMethod();
        }
    }

    public static boolean instrumentedIcmpJump(int v1, int v2, String op) {
        enterMethod();
        try {
            boolean result;
            switch (op) {
                case "EQ":
                    result = v1 == v2;
                    break;
                case "NE":
                    result = v1 != v2;
                    break;
                case "LT":
                    result = v1 < v2;
                    break;
                case "GE":
                    result = v1 >= v2;
                    break;
                case "GT":
                    result = v1 > v2;
                    break;
                case "LE":
                    result = v1 <= v2;
                    break;
                default:
                    result = false;
            }
            if (isEnabled()) {
                PATH_CONDITIONS.get().add(new Constraint(v1, v2, op, result ? 1 : 0));
            }
            return result;
        } finally {
            exitMethod();
        }
    }

    public static boolean instrumentedAcmpJump(Object v1, Object v2, String op) {
        enterMethod();
        try {
            boolean result;
            switch (op) {
                case "ACMP_EQ":
                    result = v1 == v2;
                    break;
                case "ACMP_NE":
                    result = v1 != v2;
                    break;
                default:
                    result = false;
            }
            if (isEnabled()) {
                PATH_CONDITIONS.get().add(new Constraint(v1, v2, op, result ? 1 : 0));
            }
            return result;
        } finally {
            exitMethod();
        }
    }

    // ===== Access methods (called by GalettePathConstraintBridge via PathConstraintAPI) =====

    public static List<Constraint> flush() {
        List<Constraint> result = new ArrayList<>(PATH_CONDITIONS.get());
        PATH_CONDITIONS.get().clear();
        return result;
    }

    public static List<Constraint> getCurrent() {
        return new ArrayList<>(PATH_CONDITIONS.get());
    }

    public static void reset() {
        PATH_CONDITIONS.get().clear();
    }

    public static int getConstraintCount() {
        return PATH_CONDITIONS.get().size();
    }

    // ===== Constraint data class =====

    public static class Constraint {
        public final Object value1;
        public final Object value2;
        public final String operation;
        public final int result;

        public Constraint(Object value1, Object value2, String operation, int result) {
            this.value1 = value1;
            this.value2 = value2;
            this.operation = operation;
            this.result = result;
        }
    }
}
