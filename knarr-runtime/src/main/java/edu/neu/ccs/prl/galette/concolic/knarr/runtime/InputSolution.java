package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Container for constraint solver input solutions.
 *
 * This class represents a solution from the constraint solver,
 * mapping symbolic variable labels to their concrete values.
 *
 * @author [Anne Koziolek](https://github.com/AnneKoziolek)
 */
public class InputSolution implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Map from variable labels to their values.
     */
    private final Map<String, Object> values = new HashMap<>();

    /**
     * Whether this solution is satisfiable.
     */
    private boolean satisfiable = true;

    /**
     * Create a new empty solution.
     */
    public InputSolution() {
        // Empty constructor
    }

    /**
     * Create a solution with satisfiability status.
     *
     * @param satisfiable Whether the constraints are satisfiable
     */
    public InputSolution(boolean satisfiable) {
        this.satisfiable = satisfiable;
    }

    /**
     * Set a value for a symbolic variable.
     *
     * @param label The variable label
     * @param value The concrete value
     */
    public void setValue(String label, Object value) {
        if (label != null) {
            values.put(label, value);
        }
    }

    /**
     * Get the value for a symbolic variable.
     *
     * @param label The variable label
     * @return The concrete value, or null if not found
     */
    public Object getValue(String label) {
        return values.get(label);
    }

    /**
     * Get an integer value.
     *
     * @param label The variable label
     * @param defaultValue Default value if not found or not an integer
     * @return The integer value
     */
    public int getIntValue(String label, int defaultValue) {
        Object value = getValue(label);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    /**
     * Get a long value.
     *
     * @param label The variable label
     * @param defaultValue Default value if not found or not a long
     * @return The long value
     */
    public long getLongValue(String label, long defaultValue) {
        Object value = getValue(label);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }

    /**
     * Get a double value.
     *
     * @param label The variable label
     * @param defaultValue Default value if not found or not a double
     * @return The double value
     */
    public double getDoubleValue(String label, double defaultValue) {
        Object value = getValue(label);
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    /**
     * Get a string value.
     *
     * @param label The variable label
     * @param defaultValue Default value if not found
     * @return The string value
     */
    public String getStringValue(String label, String defaultValue) {
        Object value = getValue(label);
        return (value instanceof String) ? (String) value : defaultValue;
    }

    /**
     * Get all variable labels in this solution.
     *
     * @return Set of variable labels
     */
    public Set<String> getLabels() {
        return values.keySet();
    }

    /**
     * Get all values in this solution.
     *
     * @return Map of label to value
     */
    public Map<String, Object> getValues() {
        return new HashMap<>(values);
    }

    /**
     * Check if this solution is satisfiable.
     *
     * @return True if satisfiable, false otherwise
     */
    public boolean isSatisfiable() {
        return satisfiable;
    }

    /**
     * Set the satisfiability status.
     *
     * @param satisfiable Whether the solution is satisfiable
     */
    public void setSatisfiable(boolean satisfiable) {
        this.satisfiable = satisfiable;
    }

    /**
     * Check if the solution is empty.
     *
     * @return True if no values are set
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Get the number of variables in this solution.
     *
     * @return Number of variables
     */
    public int size() {
        return values.size();
    }

    /**
     * Clear all values from the solution.
     */
    public void clear() {
        values.clear();
        satisfiable = true;
    }

    /**
     * Merge another solution into this one.
     *
     * @param other The other solution to merge
     */
    public void merge(InputSolution other) {
        if (other != null) {
            values.putAll(other.values);
            satisfiable = satisfiable && other.satisfiable;
        }
    }

    @Override
    public String toString() {
        if (!satisfiable) {
            return "InputSolution[UNSATISFIABLE]";
        }
        if (values.isEmpty()) {
            return "InputSolution[EMPTY]";
        }
        return "InputSolution" + values;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        InputSolution other = (InputSolution) obj;
        return satisfiable == other.satisfiable && values.equals(other.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode() + (satisfiable ? 1 : 0);
    }
}
