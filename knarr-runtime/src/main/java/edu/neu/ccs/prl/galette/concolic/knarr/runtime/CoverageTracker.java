package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Galette-compatible coverage tracking system.
 *
 * Migrated from Knarr's Coverage.java to provide code coverage and path coverage
 * tracking for concolic execution. Tracks both basic block coverage and
 * path-sensitive coverage for constraint solving.
 *
 * Key capabilities:
 * - Code coverage tracking at basic block level
 * - Path coverage tracking for branch sequences
 * - Thread-safe coverage collection
 * - Coverage export and analysis
 * - Integration with symbolic execution
 * @purpose Path and branch coverage tracking for symbolic execution
 * @feature Basic block coverage
 * @feature Branch coverage with true/false tracking
 * @feature Path coverage with unique path identification
 * @feature Coverage reports in JSON format
 *
 */
public class CoverageTracker implements Serializable {

    private static final long serialVersionUID = -6059233792632965508L;

    /**
     * Size of coverage arrays (1MB bit arrays).
     */
    public static int SIZE = 1 << 20;

    /**
     * Code coverage tracking - basic block execution.
     */
    private final int[] codeCoverage = new int[SIZE];

    /**
     * Path coverage tracking - branch transition sequences.
     */
    private final int[] pathCoverage = new int[SIZE];

    /**
     * Global coverage instance.
     */
    public static final CoverageTracker instance = new CoverageTracker();

    /**
     * Coverage enablement flag.
     */
    private static boolean enabled = true;

    /**
     * Class whitelist for selective coverage.
     */
    private static Optional<String[]> whitelist = Optional.empty();

    /**
     * Coverage configuration from system properties.
     */
    static {
        configureCoverage();
    }

    /**
     * Thread-local path tracking for path-sensitive coverage.
     */
    private static final ThreadLocal<Integer> lastPathId = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    /**
     * Coverage statistics tracking.
     */
    private final AtomicInteger totalBasicBlocks = new AtomicInteger(0);

    private final AtomicInteger coveredBasicBlocks = new AtomicInteger(0);
    private final AtomicInteger totalPaths = new AtomicInteger(0);
    private final AtomicInteger coveredPaths = new AtomicInteger(0);

    /**
     * Method coverage tracking.
     */
    private final Set<String> coveredMethods = ConcurrentHashMap.newKeySet();

    private final Map<String, Integer> methodHitCounts = new ConcurrentHashMap<>();

    /**
     * Branch coverage tracking.
     */
    private final Map<String, BranchCoverage> branchCoverage = new ConcurrentHashMap<>();

    /**
     * Configure coverage from system properties.
     */
    private static void configureCoverage() {
        String coverageConfig = System.getProperty("galette.coverage", "true");
        String whitelistConfig = System.getProperty("galette.coverage.whitelist");

        if ("false".equals(coverageConfig)) {
            enabled = false;
            whitelist = Optional.empty();
        } else if (whitelistConfig != null && !whitelistConfig.isEmpty()) {
            enabled = true;
            String[] packages = whitelistConfig.split(":");
            whitelist = Optional.of(packages);
        } else {
            enabled = true;
            whitelist = Optional.empty();
        }
    }

    /**
     * Check if coverage is enabled for a given class.
     *
     * @param className The class name to check
     * @return true if coverage should be tracked for this class
     */
    public static boolean isCoverageEnabled(String className) {
        if (!enabled) {
            return false;
        }

        if (!whitelist.isPresent()) {
            return true;
        }

        for (String prefix : whitelist.get()) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Set coverage configuration programmatically.
     *
     * @param enabled Whether coverage is enabled
     * @param packageWhitelist Optional whitelist of packages to track
     */
    public static void setCoverageConfig(boolean enabled, Optional<String[]> packageWhitelist) {
        CoverageTracker.enabled = enabled;
        CoverageTracker.whitelist = packageWhitelist;
    }

    /**
     * Record code coverage for a basic block.
     *
     * @param blockId Unique identifier for the basic block
     */
    public void recordCodeCoverage(int blockId) {
        if (!enabled) return;

        int normalizedId = Math.abs(blockId) % SIZE;
        int arrayIndex = normalizedId / 32;
        int bitIndex = normalizedId % 32;

        synchronized (this) {
            int oldValue = codeCoverage[arrayIndex];
            codeCoverage[arrayIndex] |= (1 << bitIndex);

            // Update statistics if this is a new block
            if ((oldValue & (1 << bitIndex)) == 0) {
                coveredBasicBlocks.incrementAndGet();
            }
        }

        totalBasicBlocks.set(Math.max(totalBasicBlocks.get(), normalizedId + 1));
    }

    /**
     * Record path coverage for branch transitions.
     *
     * @param branchId Unique identifier for the branch
     */
    public void recordPathCoverage(int branchId) {
        if (!enabled) return;

        int lastId = lastPathId.get();
        int pathId = Math.abs(lastId ^ branchId) % SIZE;

        int arrayIndex = pathId / 32;
        int bitIndex = pathId % 32;

        synchronized (this) {
            int oldValue = pathCoverage[arrayIndex];
            pathCoverage[arrayIndex] |= (1 << bitIndex);

            // Update statistics if this is a new path
            if ((oldValue & (1 << bitIndex)) == 0) {
                coveredPaths.incrementAndGet();
            }
        }

        totalPaths.set(Math.max(totalPaths.get(), pathId + 1));
        lastPathId.set(pathId >> 1);
    }

    /**
     * Record both code and path coverage.
     *
     * @param blockId Basic block identifier
     */
    public void recordCoverage(int blockId) {
        recordCodeCoverage(blockId);
        recordPathCoverage(blockId);
    }

    /**
     * Record branch coverage with taken/not-taken paths.
     *
     * @param branchId Branch identifier
     * @param taken Whether the branch was taken
     * @return The coverage ID for constraint solving
     */
    public int recordBranchCoverage(int branchId, boolean taken) {
        if (!enabled) return branchId;

        int takenId = taken ? branchId : (branchId + 1);
        int notTakenId = taken ? (branchId + 1) : branchId;

        recordCoverage(takenId);

        // Record branch statistics
        String branchKey = "branch_" + branchId;
        branchCoverage.computeIfAbsent(branchKey, k -> new BranchCoverage()).record(taken);

        // Calculate alternative path for symbolic execution
        int lastId = lastPathId.get();
        int altPathId = Math.abs(lastId ^ notTakenId) % SIZE;

        return altPathId;
    }

    /**
     * Record method entry for method-level coverage.
     *
     * @param className Class name
     * @param methodName Method name
     * @param methodDesc Method descriptor
     */
    public void recordMethodEntry(String className, String methodName, String methodDesc) {
        if (!enabled || !isCoverageEnabled(className)) return;

        String methodKey = className + "." + methodName + methodDesc;
        coveredMethods.add(methodKey);
        methodHitCounts.merge(methodKey, 1, Integer::sum);
    }

    /**
     * Get code coverage ratio.
     *
     * @return Ratio of covered basic blocks to total blocks
     */
    public double getCodeCoverageRatio() {
        int total = totalBasicBlocks.get();
        if (total == 0) return 0.0;
        return (double) coveredBasicBlocks.get() / total;
    }

    /**
     * Get path coverage ratio.
     *
     * @return Ratio of covered paths to total paths
     */
    public double getPathCoverageRatio() {
        int total = totalPaths.get();
        if (total == 0) return 0.0;
        return (double) coveredPaths.get() / total;
    }

    /**
     * Get coverage statistics summary.
     *
     * @return Formatted coverage statistics
     */
    public String getCoverageStatistics() {
        return String.format(
                "Coverage Statistics:\n" + "  Code Coverage: %d/%d blocks (%.2f%%)\n"
                        + "  Path Coverage: %d/%d paths (%.2f%%)\n"
                        + "  Method Coverage: %d methods\n"
                        + "  Branch Coverage: %d branches",
                coveredBasicBlocks.get(),
                totalBasicBlocks.get(),
                getCodeCoverageRatio() * 100,
                coveredPaths.get(),
                totalPaths.get(),
                getPathCoverageRatio() * 100,
                coveredMethods.size(),
                branchCoverage.size());
    }

    /**
     * Export coverage data to file.
     *
     * @param filename Output filename
     * @throws IOException If file write fails
     */
    public void exportCoverage(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        }
    }

    /**
     * Load coverage data from file.
     *
     * @param filename Input filename
     * @return Loaded coverage tracker
     * @throws IOException If file read fails
     * @throws ClassNotFoundException If deserialization fails
     */
    public static CoverageTracker loadCoverage(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (CoverageTracker) ois.readObject();
        }
    }

    /**
     * Reset all coverage data.
     */
    public void reset() {
        Arrays.fill(codeCoverage, 0);
        Arrays.fill(pathCoverage, 0);
        totalBasicBlocks.set(0);
        coveredBasicBlocks.set(0);
        totalPaths.set(0);
        coveredPaths.set(0);
        coveredMethods.clear();
        methodHitCounts.clear();
        branchCoverage.clear();
        lastPathId.remove();
    }

    /**
     * Get detailed branch coverage information.
     *
     * @return Map of branch coverage data
     */
    public Map<String, BranchCoverage> getBranchCoverageData() {
        return new HashMap<>(branchCoverage);
    }

    /**
     * Get method hit counts.
     *
     * @return Map of method hit counts
     */
    public Map<String, Integer> getMethodHitCounts() {
        return new HashMap<>(methodHitCounts);
    }

    /**
     * Check if coverage is currently enabled.
     *
     * @return true if coverage tracking is enabled
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * Branch coverage tracking data.
     */
    public static class BranchCoverage implements Serializable {
        private static final long serialVersionUID = 1L;

        private int takenCount = 0;
        private int notTakenCount = 0;

        public void record(boolean taken) {
            if (taken) {
                takenCount++;
            } else {
                notTakenCount++;
            }
        }

        public int getTakenCount() {
            return takenCount;
        }

        public int getNotTakenCount() {
            return notTakenCount;
        }

        public int getTotalCount() {
            return takenCount + notTakenCount;
        }

        public boolean isBothPathsCovered() {
            return takenCount > 0 && notTakenCount > 0;
        }

        public double getTakenRatio() {
            int total = getTotalCount();
            return total > 0 ? (double) takenCount / total : 0.0;
        }

        @Override
        public String toString() {
            return String.format(
                    "BranchCoverage{taken=%d, notTaken=%d, bothCovered=%s}",
                    takenCount, notTakenCount, isBothPathsCovered());
        }
    }
}
