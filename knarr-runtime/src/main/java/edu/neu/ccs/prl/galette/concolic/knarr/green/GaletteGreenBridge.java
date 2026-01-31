package edu.neu.ccs.prl.galette.concolic.knarr.green;

import edu.neu.ccs.prl.galette.concolic.knarr.runtime.GaletteSymbolicator;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import za.ac.sun.cs.green.expr.*;

/**
 * Bridge between Galette Tags and Green solver expressions.
 *
 * <p>This class handles the conversion from Galette's taint tracking
 * to Green solver's symbolic expressions for constraint solving.
 *
 * @origin KNARR_GALETTE - New class integrating Knarr concepts with Galette.
 *         This class is entirely new, created to bridge the gap between:
 *         <ul>
 *           <li>Galette's Tag-based taint tracking (from neu-se/galette)</li>
 *           <li>Green solver's Expression-based constraint representation (from Knarr)</li>
 *         </ul>
 *         The original Knarr stored expressions directly in Phosphor's Taint objects.
 *         In Galette, Tags store labels (strings) not expressions, so this bridge
 *         maintains a separate mapping between tag labels and Green expressions.
 * @see edu.gmu.swe.knarr.runtime.ExpressionTaint Original Knarr approach (stores Expression in Taint)
 */
public class GaletteGreenBridge {

    /**
     * Map to track symbolic variables created from tags.
     */
    private static final Map<Object, Variable> labelToVariable = new HashMap<>();

    /**
     * Counter for generating unique variable names.
     */
    private static final AtomicInteger variableCounter = new AtomicInteger(0);

    /**
     * Convert a Galette Tag and its associated value to a Green expression.
     *
     * @param tag The Galette tag containing symbolic labels
     * @param value The concrete value associated with the tag
     * @return Green Expression representing the symbolic value, or null if not symbolic
     */
    public static Expression tagToGreenExpression(Tag tag, Object value) {
        if (tag == null || tag.isEmpty()) {
            // No symbolic information, return concrete constant
            return createConstantExpression(value);
        }

        // Get the first label from the tag (for simplicity)
        Object[] labels = tag.getLabels();
        if (labels.length == 0) {
            return createConstantExpression(value);
        }

        // Use the first label to create/retrieve the symbolic variable
        Object primaryLabel = labels[0];
        Variable variable = getOrCreateVariable(primaryLabel, value);

        // If there are multiple labels, we might need to create a more complex expression
        if (labels.length > 1) {
            // For now, just use the first variable
            // TODO: Handle multiple labels properly (union/intersection semantics)
            return variable;
        }

        return variable;
    }

    /**
     * Get or create a Green Variable for a given label.
     *
     * @param label The symbolic label
     * @param sampleValue A sample value to determine the variable type
     * @return Green Variable representing this symbolic value
     */
    private static Variable getOrCreateVariable(Object label, Object sampleValue) {
        Variable existing = labelToVariable.get(label);
        if (existing != null) {
            return existing;
        }

        // Create a new variable based on the type of the sample value
        Variable newVar = createVariableForType(label.toString(), sampleValue);
        labelToVariable.put(label, newVar);
        return newVar;
    }

    /**
     * Create a Green Variable based on the type of the value.
     *
     * @param baseName Base name for the variable
     * @param value Sample value to determine type
     * @return Appropriately typed Green Variable
     */
    private static Variable createVariableForType(String baseName, Object value) {
        // Generate unique variable name to avoid collisions between different scopes
        String varName = baseName + "_" + variableCounter.getAndIncrement();

        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            return new IntVariable(varName, null, null);
        } else if (value instanceof Long) {
            return new IntVariable(varName, null, null); // Green uses IntVariable for longs too
        } else if (value instanceof Float || value instanceof Double) {
            return new RealVariable(varName, null, null);
        } else if (value instanceof Boolean) {
            return new IntVariable(varName, null, null); // Booleans as 0/1 integers
        } else if (value instanceof Character) {
            return new IntVariable(varName, null, null); // Characters as integer values
        } else if (value instanceof String) {
            return new StringVariable(varName);
        } else {
            // For other types, treat as integer representation
            return new IntVariable(varName, null, null);
        }
    }

    /**
     * Create a Green constant expression for a concrete value.
     *
     * @param value The concrete value
     * @return Green Constant expression
     */
    private static Constant createConstantExpression(Object value) {
        if (value instanceof Integer) {
            return new IntConstant((Integer) value);
        } else if (value instanceof Long) {
            return new IntConstant(((Long) value).intValue()); // Truncate for now
        } else if (value instanceof Short) {
            return new IntConstant((Short) value);
        } else if (value instanceof Byte) {
            return new IntConstant((Byte) value);
        } else if (value instanceof Float) {
            return new RealConstant((Float) value);
        } else if (value instanceof Double) {
            return new RealConstant((Double) value);
        } else if (value instanceof Boolean) {
            return new IntConstant((Boolean) value ? 1 : 0);
        } else if (value instanceof Character) {
            return new IntConstant((Character) value);
        } else if (value instanceof String) {
            return new StringConstant((String) value);
        } else {
            // Default to integer 0 for unknown types
            return new IntConstant(0);
        }
    }

    /**
     * Create a binary operation expression.
     *
     * @param left Left operand expression
     * @param operator The operation operator
     * @param right Right operand expression
     * @return Binary operation expression
     */
    public static Expression createBinaryOp(Expression left, Operation.Operator operator, Expression right) {
        return new BinaryOperation(operator, left, right);
    }

    /**
     * Create a unary operation expression.
     *
     * @param operator The operation operator
     * @param operand The operand expression
     * @return Unary operation expression
     */
    public static Expression createUnaryOp(Operation.Operator operator, Expression operand) {
        return new UnaryOperation(operator, operand);
    }

    /**
     * Clear the variable mapping cache.
     * Useful for starting fresh constraint collection.
     */
    public static void clearVariableCache() {
        labelToVariable.clear();
        variableCounter.set(0);
    }

    /**
     * Get the current number of symbolic variables created.
     *
     * @return Number of variables in the cache
     */
    public static int getVariableCount() {
        return labelToVariable.size();
    }

    /**
     * Convert a Tag directly to a Green expression (simplified API).
     *
     * @param tag The Galette tag
     * @return Green Expression, or null if tag is empty
     */
    public static Expression tagToExpression(Tag tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }

        // First check if GaletteSymbolicator already has an expression for this tag
        Expression existing = GaletteSymbolicator.getExpressionForTag(tag);
        if (existing != null) {
            return existing;
        }

        return null; // No expression found
    }

    /**
     * Convert a Tag to a Green expression for array operations.
     *
     * @param tag The Galette tag
     * @return Green Expression representing the symbolic value
     */
    public static Expression tagToGreenExpression(Tag tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }

        // Get or create expression for this tag
        Expression expr = GaletteSymbolicator.getExpressionForTag(tag);
        if (expr != null) {
            return expr;
        }

        // Create new variable for this tag
        Object[] labels = Tag.getLabels(tag);
        if (labels != null && labels.length > 0) {
            String varName = labels[0].toString();
            return getOrCreateVariable(varName, 1); // Default to double type
        }

        return null;
    }

    /**
     * Convert a Green expression back to a Galette Tag.
     *
     * @param expression The Green expression
     * @param labelPrefix Prefix for the tag label
     * @return Galette Tag representing the expression
     */
    public static Tag greenExpressionToTag(Expression expression, String labelPrefix) {
        if (expression == null) {
            return null;
        }

        // Create unique label for this expression
        String label = labelPrefix + "_expr_" + System.identityHashCode(expression);

        // Create tag and associate with expression
        Tag tag = Tag.of(label);
        GaletteSymbolicator.associateTagWithExpression(tag, expression);

        return tag;
    }

    /**
     * Check if a tag has an associated expression.
     *
     * @param tag The Galette tag to check
     * @return true if the tag has an associated expression
     */
    public static boolean hasExpression(Tag tag) {
        if (tag == null || tag.isEmpty()) {
            return false;
        }

        Object[] labels = tag.getLabels();
        if (labels.length == 0) {
            return false;
        }

        return labelToVariable.containsKey(labels[0]);
    }

    /**
     * Get variable by label.
     *
     * @param label The symbolic label
     * @return Variable if it exists, null otherwise
     */
    public static Variable getVariable(Object label) {
        return labelToVariable.get(label);
    }
}
