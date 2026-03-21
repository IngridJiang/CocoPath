package edu.neu.ccs.prl.galette.internal.runtime;

import java.util.*;

/**
 * Runtime path constraint collection using replacement strategy for bytecode operations.
 * Provides instrumented versions of comparison operations that collect constraints
 * for symbolic execution and path analysis.
 *
 * This class implements the replacement strategy from the comparison interception plan,
 * avoiding complex stack manipulation in favor of direct method replacement.
 *
 * @author Implementation based on claude-copilot-combined-comparison-interception-plan-3.md
 */
public final class PathUtils {

    // ===== CONFIGURATION =====

    private static volatile boolean shutdownInProgress = false;

    // Thread-local recursion guard to prevent infinite loops
    // Use lazy initialization to avoid early access issues
    private static ThreadLocal<Integer> RECURSION_DEPTH;

    private static final int MAX_RECURSION_DEPTH = 10;

    private static ThreadLocal<Integer> getRecursionDepth() {
        if (RECURSION_DEPTH == null) {
            synchronized (PathUtils.class) {
                if (RECURSION_DEPTH == null) {
                    RECURSION_DEPTH = new ThreadLocal<Integer>() {
                        @Override
                        protected Integer initialValue() {
                            return 0;
                        }
                    };
                }
            }
        }
        return RECURSION_DEPTH;
    }

    private static void enterMethod() {
        try {
            ThreadLocal<Integer> depth = getRecursionDepth();
            if (depth != null) {
                Integer val = depth.get();
                depth.set(val + 1);
            }
        } catch (Throwable t) {
            // Ignore - may happen during very early JVM initialization
        }
    }

    private static void exitMethod() {
        try {
            ThreadLocal<Integer> depth = getRecursionDepth();
            if (depth != null) {
                Integer val = depth.get();
                if (val > 0) {
                    depth.set(val - 1);
                }
            }
        } catch (Throwable t) {
            // Ignore - may happen during very early JVM initialization
        }
    }

    // HARDCODED: Always enabled to eliminate system property dependency during transformation
    private static boolean isEnabled() {
        if (shutdownInProgress) {
            return false;
        }

        // Check recursion depth to prevent stack overflow
        try {
            ThreadLocal<Integer> depthTL = getRecursionDepth();
            if (depthTL != null) {
                Integer depth = depthTL.get();
                if (depth >= MAX_RECURSION_DEPTH) {
                    // Detected deep recursion - likely String.hashCode loop
                    if (depth == MAX_RECURSION_DEPTH) {
                        // Only print once per thread
                        System.err.println("⚠️ PathUtils: Detected recursion at depth " + depth
                                + " - disabling interception for this call chain");
                        try {
                            throw new Exception("Stack trace for recursion detection");
                        } catch (Exception e) {
                            // Print first few frames to identify the loop
                            StackTraceElement[] frames = e.getStackTrace();
                            for (int i = 0; i < Math.min(15, frames.length); i++) {
                                System.err.println("  at " + frames[i]);
                            }
                        }
                    }
                    return false;
                }
            }
        } catch (Throwable t) {
            // Ignore - may happen during early initialization
        }

        return true;
    }

    static {
        try {
            System.out.println("🔧 PathUtils static initializer: isEnabled() = " + isEnabled() + " (HARDCODED)");
            System.out.println("🔧 System property galette.concolic.interception.enabled = "
                    + System.getProperty("galette.concolic.interception.enabled") + " (IGNORED)");

            // Add shutdown hook to disable interception during shutdown
            // This prevents infinite recursion when String.hashCode is used during shutdown
            Runtime.getRuntime()
                    .addShutdownHook(new Thread(
                            () -> {
                                shutdownInProgress = true;
                                // Don't print during shutdown to avoid more recursion
                            },
                            "PathUtils-Shutdown"));
        } catch (Throwable t) {
            System.err.println("❌ PathUtils static initializer failed: " + t);
            t.printStackTrace();
        }
    }

    private static final boolean DEBUG = Boolean.getBoolean("galette.concolic.interception.debug");

    // ===== THREAD-LOCAL STORAGE =====

    private static final ThreadLocal<List<Constraint>> PATH_CONDITIONS = new ThreadLocal<List<Constraint>>() {
        @Override
        protected List<Constraint> initialValue() {
            return new ArrayList<>();
        }
    };

    // ===== SYMBOLIC VALUE DETECTION =====

    /**
     * Cache for user-defined symbolic labels.
     * This is populated by the concolic execution framework when creating symbolic values.
     */
    private static final Set<Object> USER_SYMBOLIC_LABELS = new HashSet<>();

    /**
     * Add a user-defined symbolic label to track.
     */
    public static void addUserSymbolicLabel(Object label) {
        if (label != null) {
            USER_SYMBOLIC_LABELS.add(label);
            if (DEBUG) {
                System.out.println("🏷️ PathUtils: Added user symbolic label: " + label);
            }
        }
    }

    /**
     * Clear all user-defined symbolic labels.
     */
    public static void clearUserSymbolicLabels() {
        USER_SYMBOLIC_LABELS.clear();
    }

    /**
     * Check if values might be symbolic by examining their tags' labels.
     * Since Tainter.getTag() is a placeholder that returns null without instrumentation,
     * we need to pass the tags directly from the instrumented comparison operations.
     */
    private static boolean mightBeSymbolic(Tag tag1, Tag tag2) {
        boolean hasUserTag1 = isUserSymbolicTag(tag1);
        boolean hasUserTag2 = isUserSymbolicTag(tag2);
        boolean result = hasUserTag1 || hasUserTag2;

        if (DEBUG) {
            System.out.println("🔍 mightBeSymbolic(tag1=" + tag1 + ", tag2=" + tag2 + ") -> hasUserTag1="
                    + hasUserTag1 + ", hasUserTag2=" + hasUserTag2 + ", result="
                    + result);
        }

        return result;
    }

    /**
     * Check if a tag contains any user-defined symbolic labels.
     */
    private static boolean isUserSymbolicTag(Tag tag) {
        if (tag == null || tag.isEmpty()) {
            return false;
        }

        Object[] labels = Tag.getLabels(tag);
        if (labels == null || labels.length == 0) {
            return false;
        }

        // Check if any label in the tag matches our user-defined symbolic labels
        for (Object label : labels) {
            if (USER_SYMBOLIC_LABELS.contains(label)) {
                if (DEBUG) {
                    System.out.println("🏷️ Found user symbolic label: " + label);
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Temporary workaround: Since we can't access tags from values without proper
     * instrumentation context, we'll collect all constraints for now.
     * TODO: Implement proper tag-based filtering when we have the infrastructure.
     */
    private static boolean mightBeSymbolic(Object value1, Object value2) {
        // For now, collect all constraints to get the system working
        // This was the original behavior that worked
        if (DEBUG) {
            System.out.println("🔍 mightBeSymbolic(Object " + value1 + ", " + value2 + ") -> true (collecting all)");
        }
        return true; // Collect all constraints temporarily
    }

    /**
     * Temporary workaround: Since we can't access tags from values without proper
     * instrumentation context, we'll collect all constraints for now.
     * TODO: Implement proper tag-based filtering when we have the infrastructure.
     */
    private static boolean mightBeSymbolic(double value1, double value2) {
        // For now, collect all constraints to get the system working
        // This was the original behavior that worked
        if (DEBUG) {
            System.out.println("🔍 mightBeSymbolic(double " + value1 + ", " + value2 + ") -> true (collecting all)");
        }
        return true; // Collect all constraints temporarily
    }

    // ===== INSTRUMENTED COMPARISON OPERATIONS =====

    /**
     * Instrumented version of LCMP instruction.
     */
    public static int instrumentedLcmp(long value1, long value2) {
        enterMethod();
        try {
            int result = Long.compare(value1, value2);

            if (isEnabled() && mightBeSymbolic(value1, value2)) {
                try {
                    List<Constraint> conditions = PATH_CONDITIONS.get();
                    if (conditions == null) {
                        System.err.println("❌ LCMP: PATH_CONDITIONS.get() returned null! Initializing new list...");
                        conditions = new ArrayList<>();
                        PATH_CONDITIONS.set(conditions);
                    }
                    conditions.add(new Constraint(value1, value2, "LCMP", result));
                } catch (Exception e) {
                    System.err.println("❌ LCMP error adding constraint: " + e);
                    e.printStackTrace();
                }

                if (DEBUG) {
                    System.out.println("PathUtils: " + value1 + " LCMP " + value2 + " -> " + result);
                }
            }

            return result;
        } finally {
            exitMethod();
        }
    }

    /**
     * Instrumented version of FCMPL instruction.
     */
    public static int instrumentedFcmpl(float value1, float value2) {
        enterMethod();
        try {
            int result;
            if (Float.isNaN(value1) || Float.isNaN(value2)) {
                result = -1; // FCMPL returns -1 for NaN
            } else {
                result = Float.compare(value1, value2);
            }

            if (isEnabled() && mightBeSymbolic(value1, value2)) {
                try {
                    List<Constraint> conditions = PATH_CONDITIONS.get();
                    if (conditions == null) {
                        System.err.println("❌ FCMPL: PATH_CONDITIONS.get() returned null! Initializing new list...");
                        conditions = new ArrayList<>();
                        PATH_CONDITIONS.set(conditions);
                    }
                    conditions.add(new Constraint(value1, value2, "FCMPL", result));
                } catch (Exception e) {
                    System.err.println("❌ FCMPL error adding constraint: " + e);
                    e.printStackTrace();
                }

                if (DEBUG) {
                    System.out.println("PathUtils: " + value1 + " FCMPL " + value2 + " -> " + result);
                }
            }

            return result;
        } finally {
            exitMethod();
        }
    }

    /**
     * Instrumented version of FCMPG instruction.
     */
    public static int instrumentedFcmpg(float value1, float value2) {
        enterMethod();
        try {
            int result;
            if (Float.isNaN(value1) || Float.isNaN(value2)) {
                result = 1; // FCMPG returns 1 for NaN
            } else {
                result = Float.compare(value1, value2);
            }

            if (isEnabled() && mightBeSymbolic(value1, value2)) {
                try {
                    List<Constraint> conditions = PATH_CONDITIONS.get();
                    if (conditions == null) {
                        System.err.println("❌ FCMPG: PATH_CONDITIONS.get() returned null! Initializing new list...");
                        conditions = new ArrayList<>();
                        PATH_CONDITIONS.set(conditions);
                    }
                    conditions.add(new Constraint(value1, value2, "FCMPG", result));
                } catch (Exception e) {
                    System.err.println("❌ FCMPG error adding constraint: " + e);
                    e.printStackTrace();
                }

                if (DEBUG) {
                    System.out.println("PathUtils: " + value1 + " FCMPG " + value2 + " -> " + result);
                }
            }

            return result;
        } finally {
            exitMethod();
        }
    }

    /**
     * Instrumented version of DCMPL instruction.
     */
    public static int instrumentedDcmpl(double value1, double value2) {
        enterMethod();

        try {
            // Debug output removed - can cause recursion
            int result;
            if (Double.isNaN(value1) || Double.isNaN(value2)) {
                result = -1; // DCMPL returns -1 for NaN
            } else {
                result = Double.compare(value1, value2);
            }

            if (isEnabled() && mightBeSymbolic(value1, value2)) {
                try {
                    List<Constraint> conditions = PATH_CONDITIONS.get();
                    if (conditions == null) {
                        System.err.println("❌ DCMPL: PATH_CONDITIONS.get() returned null! Initializing new list...");
                        conditions = new ArrayList<>();
                        PATH_CONDITIONS.set(conditions);
                    }
                    conditions.add(new Constraint(value1, value2, "DCMPL", result));
                    System.out.println("✅ DCMPL constraint added: " + value1 + " DCMPL " + value2 + " -> " + result);
                } catch (Exception e) {
                    System.err.println("❌ DCMPL error adding constraint: " + e);
                    e.printStackTrace();
                }

                if (DEBUG) {
                    System.out.println("PathUtils: " + value1 + " DCMPL " + value2 + " -> " + result);
                }
            } else {
                System.out.println("⚠️ DCMPL not enabled or not symbolic: ENABLED=" + isEnabled() + ", mightBeSymbolic="
                        + mightBeSymbolic(value1, value2));
            }

            return result;
        } finally {
            exitMethod();
        }
    }

    /**
     * Instrumented version of DCMPG instruction.
     */
    public static int instrumentedDcmpg(double value1, double value2) {
        enterMethod();
        try {
            // Debug output removed - can cause recursion
            int result;
            if (Double.isNaN(value1) || Double.isNaN(value2)) {
                result = 1; // DCMPG returns 1 for NaN
            } else {
                result = Double.compare(value1, value2);
            }

            if (isEnabled() && mightBeSymbolic(value1, value2)) {
                try {
                    List<Constraint> conditions = PATH_CONDITIONS.get();
                    if (conditions == null) {
                        System.err.println("❌ DCMPG: PATH_CONDITIONS.get() returned null! Initializing new list...");
                        conditions = new ArrayList<>();
                        PATH_CONDITIONS.set(conditions);
                    }
                    conditions.add(new Constraint(value1, value2, "DCMPG", result));
                    System.out.println("✅ DCMPG constraint added: " + value1 + " DCMPG " + value2 + " -> " + result);
                } catch (Exception e) {
                    System.err.println("❌ DCMPG error adding constraint: " + e);
                    e.printStackTrace();
                }

                if (DEBUG) {
                    System.out.println("PathUtils: " + value1 + " DCMPG " + value2 + " -> " + result);
                }
            } else {
                System.out.println("⚠️ DCMPG not enabled or not symbolic: ENABLED=" + isEnabled() + ", mightBeSymbolic="
                        + mightBeSymbolic(value1, value2));
            }

            return result;
        } finally {
            exitMethod();
        }
    }

    /**
     * Instrumented version of IF_ICMP* instructions.
     */
    public static boolean instrumentedIcmpJump(int value1, int value2, String operation) {
        enterMethod();
        try {
            boolean result;

            switch (operation) {
                case "EQ":
                    result = value1 == value2;
                    break;
                case "NE":
                    result = value1 != value2;
                    break;
                case "LT":
                    result = value1 < value2;
                    break;
                case "GE":
                    result = value1 >= value2;
                    break;
                case "GT":
                    result = value1 > value2;
                    break;
                case "LE":
                    result = value1 <= value2;
                    break;
                default:
                    result = false;
            }

            if (isEnabled() && mightBeSymbolic(value1, value2)) {
                List<Constraint> conditions = PATH_CONDITIONS.get();
                conditions.add(new Constraint(value1, value2, operation, result ? 1 : 0));

                if (DEBUG) {
                    System.out.println("PathUtils: " + value1 + " " + operation + " " + value2 + " -> " + result);
                }
            }

            return result;
        } finally {
            exitMethod();
        }
    }

    /**
     * Instrumented version of IF_ACMP* instructions.
     */
    public static boolean instrumentedAcmpJump(Object value1, Object value2, String operation) {
        enterMethod();
        try {
            boolean result;

            switch (operation) {
                case "ACMP_EQ":
                    result = value1 == value2;
                    break;
                case "ACMP_NE":
                    result = value1 != value2;
                    break;
                default:
                    result = false;
            }

            if (isEnabled() && mightBeSymbolic(value1, value2)) {
                List<Constraint> conditions = PATH_CONDITIONS.get();
                conditions.add(new Constraint(value1, value2, operation, result ? 1 : 0));

                if (DEBUG) {
                    System.out.println("PathUtils: " + value1 + " " + operation + " " + value2 + " -> " + result);
                }
            }

            return result;
        } finally {
            exitMethod();
        }
    }

    // ===== ACCESS METHODS =====

    /**
     * Retrieve and clear all collected path conditions.
     */
    public static List<Constraint> flush() {
        List<Constraint> constraints = new ArrayList<>(PATH_CONDITIONS.get());
        PATH_CONDITIONS.get().clear();
        return constraints;
    }

    /**
     * Clear all path conditions and reset state.
     */
    public static void reset() {
        PATH_CONDITIONS.get().clear();
    }

    /**
     * Get current path conditions without clearing.
     */
    public static List<Constraint> getCurrent() {
        return new ArrayList<>(PATH_CONDITIONS.get());
    }

    /**
     * Get the count of collected constraints.
     */
    public static int getConstraintCount() {
        return PATH_CONDITIONS.get().size();
    }

    // ===== DATA STRUCTURES =====

    public static class Constraint {
        public final Object value1;
        public final Object value2;
        public final String operation;
        public final int result;
        public final long timestamp;

        public Constraint(Object value1, Object value2, String operation, int result) {
            this.value1 = value1;
            this.value2 = value2;
            this.operation = operation;
            this.result = result;
            this.timestamp = System.nanoTime();
        }

        @Override
        public String toString() {
            // Avoid String.format to prevent recursion during shutdown
            // Use simple concatenation which is less likely to trigger comparisons
            return "Constraint{" + value1 + " " + operation + " " + value2 + " -> " + result + "}";
        }

        /**
         * Convert to human-readable constraint expression.
         */
        public String toExpression() {
            switch (operation) {
                case "EQ":
                    return value1 + " == " + value2;
                case "NE":
                    return value1 + " != " + value2;
                case "LT":
                    return value1 + " < " + value2;
                case "GE":
                    return value1 + " >= " + value2;
                case "GT":
                    return value1 + " > " + value2;
                case "LE":
                    return value1 + " <= " + value2;
                case "LCMP":
                    return value1 + " cmp " + value2 + " = " + result;
                case "FCMPL":
                case "FCMPG":
                    return value1 + " fcmp " + value2 + " = " + result;
                case "DCMPL":
                case "DCMPG":
                    return value1 + " dcmp " + value2 + " = " + result;
                case "ACMP_EQ":
                    return value1 + " === " + value2;
                case "ACMP_NE":
                    return value1 + " !== " + value2;
                default:
                    return value1 + " " + operation + " " + value2 + " -> " + result;
            }
        }
    }
}
