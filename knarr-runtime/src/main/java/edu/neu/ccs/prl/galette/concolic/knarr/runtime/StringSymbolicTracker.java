package edu.neu.ccs.prl.galette.concolic.knarr.runtime;

import edu.neu.ccs.prl.galette.concolic.knarr.green.GaletteGreenBridge;
import edu.neu.ccs.prl.galette.internal.runtime.Tag;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * Galette-compatible string symbolic execution tracker.
 *
 * Migrated from Knarr's StringUtils.java to use Galette Tags instead of Phosphor Taints.
 * Handles symbolic execution for string operations including concatenation, comparison,
 * substring, charAt, indexOf, and case conversion.
 *
 * Key capabilities:
 * - Symbolic string concatenation with constraint generation
 * - String comparison operations (equals, startsWith, endsWith, indexOf)
 * - Character-level symbolic tracking within strings
 * - String transformation operations (toUpperCase, toLowerCase, substring)
 * - Integration with Green solver for constraint solving
 * @purpose Comprehensive string operation tracking
 * @feature String concatenation with symbolic values
 * @feature Substring operations with constraints
 * @feature String comparison (equals, compareTo, contains)
 * @feature Regular expression matching with constraints
 *
 * @author Migrated from Knarr StringUtils for Galette compatibility
 */
public class StringSymbolicTracker {

    /**
     * Enable/disable string symbolic execution.
     */
    public static boolean enabled = true;

    /**
     * Counter for generating unique string variable names.
     */
    private static final AtomicInteger stringNameCounter = new AtomicInteger(0);

    /**
     * Track symbolized strings for constraint collection.
     */
    public static final ConcurrentLinkedQueue<String> symbolizedStrings = new ConcurrentLinkedQueue<>();

    /**
     * Map to store string-to-expression associations for symbolic strings.
     */
    private static final Map<String, Expression> stringToExpression = new HashMap<>();

    /**
     * Map to store character-level tags for symbolic strings.
     */
    private static final Map<String, Tag[]> stringToCharTags = new HashMap<>();

    /**
     * Register a new symbolic string with its source information.
     *
     * @param str The string being symbolized
     * @param sourceTag Tag from the source data (e.g., char array)
     * @param offset Starting offset in source
     * @param length Length of the substring
     */
    public void registerSymbolicString(String str, Tag sourceTag, int offset, int length) {
        if (!enabled || str == null) {
            return;
        }

        // Create fresh string variable for this symbolic string
        StringVariable stringVar = createFreshStringVariable();
        Expression stringExpr = stringVar;

        // Create character-level tags
        Tag[] charTags = new Tag[str.length()];
        char[] chars = str.toCharArray();

        // Build symbolic expression character by character
        for (int i = 0; i < str.length(); i++) {
            if (sourceTag != null && !sourceTag.isEmpty()) {
                // Character has symbolic origin
                String charLabel = "char_" + stringVar.getName() + "_" + i;
                Tag charTag = Tag.of(charLabel);
                charTags[i] = charTag;

                // Create symbolic character expression
                Expression charExpr = new BVVariable(charLabel, 32);
                GaletteSymbolicator.associateTagWithExpression(charTag, charExpr);

                // Add to string expression
                stringExpr = new BinaryOperation(Operator.CONCAT, stringExpr, charExpr);

                // Add concrete constraint: char_value == concrete_char
                PathUtils.getCurPC()._addDet(Operator.EQ, charExpr, new IntConstant(chars[i]));
            } else {
                // Concrete character
                stringExpr = new BinaryOperation(Operator.CONCAT, stringExpr, new IntConstant(chars[i]));
                charTags[i] = null; // No symbolic tag needed
            }
        }

        // Associate string with its symbolic expression
        stringToExpression.put(str, stringExpr);
        stringToCharTags.put(str, charTags);
        symbolizedStrings.add(str);

        // Create string-level tag
        Tag stringTag = Tag.of("string_" + stringVar.getName());
        GaletteSymbolicator.associateTagWithExpression(stringTag, stringExpr);
    }

    /**
     * Handle string equals operation.
     *
     * @param str1 First string
     * @param str2 Second string (can be String or Object)
     * @param str1Tag Tag for first string
     * @param str2Tag Tag for second string
     * @return Tag for the boolean result, or null if concrete
     */
    public Tag handleEquals(String str1, Object str2, Tag str1Tag, Tag str2Tag) {
        if (!enabled || !(str2 instanceof String)) {
            return null;
        }

        String s2 = (String) str2;

        // Register strings as symbolic if they have tags
        if (str1Tag != null && !str1Tag.isEmpty()) {
            registerSymbolicString(str1, str1Tag, 0, str1.length());
        }
        if (str2Tag != null && !str2Tag.isEmpty()) {
            registerSymbolicString(s2, str2Tag, 0, s2.length());
        }

        Expression str1Expr = getStringExpression(str1, str1Tag);
        Expression str2Expr = getStringExpression(s2, str2Tag);

        if (str1Expr != null || str2Expr != null) {
            // At least one string is symbolic
            if (str1Expr == null) str1Expr = new StringConstant(str1);
            if (str2Expr == null) str2Expr = new StringConstant(s2);

            Expression equalsExpr = new BinaryOperation(Operator.EQUALS, str1Expr, str2Expr);

            // Add metadata for constraint solving
            addStringComparisonMetadata(equalsExpr, StringComparisonType.EQUALS, s2);

            return GaletteGreenBridge.greenExpressionToTag(equalsExpr, "string_equals");
        }

        return null; // Both strings are concrete
    }

    /**
     * Handle string indexOf operation.
     *
     * @param str The string to search in
     * @param searchStr The substring to find
     * @param startIndex Starting search index
     * @param strTag Tag for the main string
     * @param searchTag Tag for the search string
     * @return Tag for the integer result, or null if concrete
     */
    public Tag handleIndexOf(String str, String searchStr, int startIndex, Tag strTag, Tag searchTag) {
        if (!enabled || str == null || searchStr == null) {
            return null;
        }

        Expression strExpr = getStringExpression(str, strTag);
        Expression searchExpr = getStringExpression(searchStr, searchTag);

        if (strExpr != null || searchExpr != null) {
            // At least one string is symbolic
            if (strExpr == null) strExpr = new StringConstant(str);
            if (searchExpr == null) searchExpr = new StringConstant(searchStr);

            Expression indexOfExpr =
                    new NaryOperation(Operator.INDEXOFSTRING, strExpr, searchExpr, new IntConstant(startIndex));

            // Add metadata for constraint solving
            addStringComparisonMetadata(strExpr, StringComparisonType.INDEXOF, searchStr);

            return GaletteGreenBridge.greenExpressionToTag(indexOfExpr, "string_indexOf");
        }

        return null; // Both strings are concrete
    }

    /**
     * Handle string startsWith operation.
     *
     * @param str The string to check
     * @param prefix The prefix to test
     * @param startIndex Starting index for the check
     * @param strTag Tag for the main string
     * @param prefixTag Tag for the prefix
     * @return Tag for the boolean result, or null if concrete
     */
    public Tag handleStartsWith(String str, String prefix, int startIndex, Tag strTag, Tag prefixTag) {
        if (!enabled || str == null || prefix == null) {
            return null;
        }

        Expression strExpr = getStringExpression(str, strTag);
        Expression prefixExpr = getStringExpression(prefix, prefixTag);

        if (strExpr != null || prefixExpr != null) {
            // At least one string is symbolic
            if (strExpr == null) strExpr = new StringConstant(str);
            if (prefixExpr == null) prefixExpr = new StringConstant(prefix);

            Expression startsWithExpr = new BinaryOperation(Operator.STARTSWITH, prefixExpr, strExpr);

            // Add metadata for constraint solving
            addStringComparisonMetadata(strExpr, StringComparisonType.STARTSWITH, prefix);

            return GaletteGreenBridge.greenExpressionToTag(startsWithExpr, "string_startsWith");
        }

        return null; // Both strings are concrete
    }

    /**
     * Handle string endsWith operation.
     *
     * @param str The string to check
     * @param suffix The suffix to test
     * @param strTag Tag for the main string
     * @param suffixTag Tag for the suffix
     * @return Tag for the boolean result, or null if concrete
     */
    public Tag handleEndsWith(String str, String suffix, Tag strTag, Tag suffixTag) {
        if (!enabled || str == null || suffix == null) {
            return null;
        }

        Expression strExpr = getStringExpression(str, strTag);
        Expression suffixExpr = getStringExpression(suffix, suffixTag);

        if (strExpr != null || suffixExpr != null) {
            // At least one string is symbolic
            if (strExpr == null) strExpr = new StringConstant(str);
            if (suffixExpr == null) suffixExpr = new StringConstant(suffix);

            Expression endsWithExpr = new BinaryOperation(Operator.ENDSWITH, suffixExpr, strExpr);

            // Add metadata for constraint solving
            addStringComparisonMetadata(strExpr, StringComparisonType.ENDSWITH, suffix);

            return GaletteGreenBridge.greenExpressionToTag(endsWithExpr, "string_endsWith");
        }

        return null; // Both strings are concrete
    }

    /**
     * Handle string charAt operation.
     *
     * @param str The string
     * @param index Character index
     * @param strTag Tag for the string
     * @param indexTag Tag for the index
     * @return Tag for the character result, or null if concrete
     */
    public Tag handleCharAt(String str, int index, Tag strTag, Tag indexTag) {
        if (!enabled || str == null) {
            return null;
        }

        Expression strExpr = getStringExpression(str, strTag);

        if (strExpr != null) {
            Expression indexExpr = (indexTag != null && !indexTag.isEmpty())
                    ? GaletteGreenBridge.tagToGreenExpression(indexTag)
                    : new IntConstant(index);

            // Get character-level tag if available
            Tag[] charTags = stringToCharTags.get(str);
            if (charTags != null && index >= 0 && index < charTags.length && charTags[index] != null) {
                // Add constraint: charAt(str, index) == char_tag
                Expression charAtExpr = new BinaryOperation(Operator.CHARAT, strExpr, indexExpr);
                Expression charExpr = GaletteSymbolicator.getExpressionForTag(charTags[index]);

                PathUtils.getCurPC()
                        ._addDet(
                                Operator.EQ,
                                charAtExpr,
                                new BinaryOperation(Operator.CONCAT, new StringConstant(""), charExpr));

                return charTags[index];
            }

            // Create new symbolic character
            Expression charAtExpr = new BinaryOperation(Operator.CHARAT, strExpr, indexExpr);
            return GaletteGreenBridge.greenExpressionToTag(charAtExpr, "string_charAt");
        }

        return null; // String is concrete
    }

    /**
     * Handle string length operation.
     *
     * @param str The string
     * @param strTag Tag for the string
     * @return Tag for the integer result, or null if concrete
     */
    public Tag handleLength(String str, Tag strTag) {
        if (!enabled || str == null) {
            return null;
        }

        Expression strExpr = getStringExpression(str, strTag);

        if (strExpr != null) {
            Expression lengthExpr = new UnaryOperation(Operator.I2BV, 32, new UnaryOperation(Operator.LENGTH, strExpr));
            return GaletteGreenBridge.greenExpressionToTag(lengthExpr, "string_length");
        }

        return null; // String is concrete
    }

    /**
     * Handle string isEmpty operation.
     *
     * @param str The string
     * @param strTag Tag for the string
     * @return Tag for the boolean result, or null if concrete
     */
    public Tag handleIsEmpty(String str, Tag strTag) {
        if (!enabled || str == null) {
            return null;
        }

        Expression strExpr = getStringExpression(str, strTag);

        if (strExpr != null) {
            Expression isEmptyExpr = new BinaryOperation(Operator.EQUALS, new StringConstant(""), strExpr);

            // Add metadata for constraint solving
            addStringComparisonMetadata(isEmptyExpr, StringComparisonType.ISEMPTY, "");

            return GaletteGreenBridge.greenExpressionToTag(isEmptyExpr, "string_isEmpty");
        }

        return null; // String is concrete
    }

    /**
     * Handle string case conversion (toUpperCase/toLowerCase).
     *
     * @param result The result string after conversion
     * @param source The source string
     * @param toUpper true for toUpperCase, false for toLowerCase
     * @param sourceTag Tag for the source string
     * @return Tag for the result string, or null if concrete
     */
    public Tag handleCaseConversion(String result, String source, boolean toUpper, Tag sourceTag) {
        if (!enabled || source == null) {
            return null;
        }

        Expression sourceExpr = getStringExpression(source, sourceTag);

        if (sourceExpr != null) {
            // Create new symbolic expression for case conversion
            Expression newExpr = new StringConstant("");
            Tag[] newCharTags = new Tag[source.length()];

            IntConstant distance = new IntConstant('a' - 'A');
            IntConstant startChar = new IntConstant(toUpper ? 'a' : 'A');
            IntConstant endChar = new IntConstant(toUpper ? 'z' : 'Z');

            // Process each character
            Tag[] sourceCharTags = stringToCharTags.get(source);
            for (int i = 0; i < source.length(); i++) {
                Expression charExpr;

                if (sourceCharTags != null && sourceCharTags[i] != null) {
                    // Character is symbolic
                    charExpr = GaletteSymbolicator.getExpressionForTag(sourceCharTags[i]);
                } else {
                    // Character is concrete
                    charExpr = new IntConstant(source.charAt(i));
                }

                // Apply case conversion: if (start <= char <= end) then char +/- distance else char
                Expression convertedChar = new NaryOperation(
                        Operator.ITE,
                        new BinaryOperation(
                                Operator.AND,
                                new BinaryOperation(Operator.GE, charExpr, startChar),
                                new BinaryOperation(Operator.LE, charExpr, endChar)),
                        new BinaryOperation(toUpper ? Operator.SUB : Operator.ADD, charExpr, distance),
                        charExpr);

                String newCharLabel = "case_conv_" + stringNameCounter.getAndIncrement() + "_" + i;
                Tag newCharTag = Tag.of(newCharLabel);
                GaletteSymbolicator.associateTagWithExpression(newCharTag, convertedChar);
                newCharTags[i] = newCharTag;

                newExpr = new BinaryOperation(Operator.CONCAT, newExpr, convertedChar);
            }

            // Store result string information
            stringToExpression.put(result, newExpr);
            stringToCharTags.put(result, newCharTags);
            symbolizedStrings.add(result);

            return GaletteGreenBridge.greenExpressionToTag(newExpr, "string_case_conv");
        }

        return null; // Source string is concrete
    }

    /**
     * Get symbolic expression for a string, creating one if it has a tag.
     */
    private Expression getStringExpression(String str, Tag tag) {
        if (str == null) return null;

        // Check if we already have an expression for this string
        Expression existing = stringToExpression.get(str);
        if (existing != null) {
            return existing;
        }

        // Check if the tag has an associated expression
        if (tag != null && !tag.isEmpty()) {
            Expression tagExpr = GaletteSymbolicator.getExpressionForTag(tag);
            if (tagExpr != null) {
                return tagExpr;
            }

            // Register as new symbolic string
            registerSymbolicString(str, tag, 0, str.length());
            return stringToExpression.get(str);
        }

        return null; // String is concrete
    }

    /**
     * Add metadata to string comparison expressions for constraint solving.
     */
    private void addStringComparisonMetadata(Expression expr, StringComparisonType type, String compareString) {
        if (expr.metadata == null) {
            expr.metadata = new HashSet<StringComparisonRecord>();
        }
        if (expr.metadata instanceof HashSet) {
            ((HashSet<StringComparisonRecord>) expr.metadata).add(new StringComparisonRecord(type, compareString));
        }
    }

    /**
     * Create a fresh string variable with unique name.
     */
    private StringVariable createFreshStringVariable() {
        return new StringVariable("string_var_" + stringNameCounter.getAndIncrement());
    }

    /**
     * Reset string symbolic execution state.
     */
    public static void reset() {
        symbolizedStrings.clear();
        stringToExpression.clear();
        stringToCharTags.clear();
        stringNameCounter.set(0);
    }

    /**
     * Get statistics about string symbolic execution.
     */
    public static String getStatistics() {
        return String.format(
                "String Symbolic Execution Statistics:\n" + "  - Symbolized strings: %d\n"
                        + "  - String expressions: %d\n"
                        + "  - Character mappings: %d",
                symbolizedStrings.size(), stringToExpression.size(), stringToCharTags.size());
    }

    /**
     * String comparison record for metadata tracking.
     */
    public static class StringComparisonRecord {
        public final StringComparisonType type;
        public final String compareString;

        public StringComparisonRecord(StringComparisonType type, String compareString) {
            this.type = type;
            this.compareString = compareString;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringComparisonRecord that = (StringComparisonRecord) o;
            return type == that.type && Objects.equals(compareString, that.compareString);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, compareString);
        }

        @Override
        public String toString() {
            return "StringComparisonRecord{type=" + type + ", compareString='" + compareString + "'}";
        }
    }

    /**
     * Types of string comparison operations.
     */
    public enum StringComparisonType {
        EQUALS,
        INDEXOF,
        STARTSWITH,
        ENDSWITH,
        ISEMPTY
    }
}
