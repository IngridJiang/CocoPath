package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.compat.Symbolicator;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * String operation integration tests for Galette-based Knarr runtime.
 *
 * These tests are migrated from the original Knarr test suite and validate
 * string operations, character manipulation, and string-based symbolic execution
 * functionality using Galette's concolic execution infrastructure.
 *
 * @author [Anne Koziolek](https://github.com/AnneKoziolek)
 */
public class StringOpITCase {

    @BeforeEach
    public void setUp() {
        // Reset symbolicator state before each test
        Symbolicator.reset();
    }

    @Test
    public void testLength() throws Exception {
        String test = "This is a test";
        char tainted[] = new char[test.length()];

        for (int i = 0; i < tainted.length; i++) {
            tainted[i] = Symbolicator.symbolic("length_" + i, test.charAt(i));
        }

        String taintedString = new String(tainted, 0, tainted.length);

        for (int i = 0; i < taintedString.length(); i++) {
            assertNotEquals(20, i);
        }

        ArrayList<SimpleEntry<String, Object>> solution = Symbolicator.dumpConstraints("lol");

        assertTrue(solution != null && !solution.isEmpty());
    }

    @Test
    public void testCharAt() throws Exception {
        String test = "This is a test";
        char tainted[] = new char[test.length()];

        for (int i = 0; i < tainted.length; i++) tainted[i] = Symbolicator.symbolic("charAt_" + i, test.charAt(i));

        String taintedString = new String(tainted, 0, tainted.length);

        char c0 = taintedString.charAt(0);
        char cN = taintedString.charAt(4);

        assertTrue((c0 <= 'Z' && c0 >= 'A') || (c0 <= 'z' && c0 >= 'a'));
        assertFalse((cN <= 'Z' && cN >= 'A') || (cN <= 'z' && cN >= 'a'));

        ArrayList<SimpleEntry<String, Object>> solution = Symbolicator.dumpConstraints();

        assertTrue(solution != null && !solution.isEmpty());

        for (SimpleEntry<String, Object> e : solution) {
            char c = (char) (int) e.getValue();
            switch (e.getKey()) {
                case "charAt_0":
                    assertTrue((c <= 'Z' && c >= 'A') || (c <= 'z' && c >= 'a'));
                    break;
                case "charAt_4":
                    assertFalse((c <= 'Z' && c >= 'A') || (c <= 'z' && c >= 'a'));
                    break;
            }
        }
    }

    @Test
    public void testTaintedEquals() throws Exception {
        String test = "This is a test";
        char tainted[] = new char[test.length()];

        for (int i = 0; i < tainted.length; i++) tainted[i] = Symbolicator.symbolic("equals_" + i, test.charAt(i));

        String taintedString = new String(tainted, 0, tainted.length);

        String same = new String(test);
        String notSame = "This is not a test";

        assertEquals(taintedString, same);
        assertNotEquals(taintedString, notSame);

        ArrayList<SimpleEntry<String, Object>> solution = Symbolicator.dumpConstraints();

        assertTrue(solution != null && !solution.isEmpty());

        Pattern p = Pattern.compile("equals_([0-9][0-9]*)");

        for (SimpleEntry<String, Object> e : solution) {
            Matcher m = p.matcher(e.getKey());
            if (m.matches()) {
                int i = Integer.parseInt(m.group(1));
                assertEquals(test.charAt(i), (char) (int) e.getValue());
            }
        }
    }

    @Test
    public void testEqualsTainted() throws Exception {
        String test = "This is a test";
        char tainted[] = new char[test.length()];

        for (int i = 0; i < tainted.length; i++) tainted[i] = Symbolicator.symbolic("equalsT_" + i, test.charAt(i));

        String taintedString = new String(tainted, 0, tainted.length);

        String same = new String(test);
        String notSame = "This is not a test";

        assertEquals(same, taintedString);
        assertNotEquals(notSame, taintedString);

        ArrayList<SimpleEntry<String, Object>> solution = Symbolicator.dumpConstraints();

        assertTrue(solution != null && !solution.isEmpty());

        Pattern p = Pattern.compile("equalsT_([0-9][0-9]*)");

        for (SimpleEntry<String, Object> e : solution) {
            Matcher m = p.matcher(e.getKey());
            if (m.matches()) {
                int i = Integer.parseInt(m.group(1));
                assertEquals(test.charAt(i), (char) (int) e.getValue());
            }
        }
    }

    @Test
    public void testToLowerUpper() throws Exception {
        String test = "This is a test";
        char tainted[] = new char[test.length()];

        for (int i = 0; i < tainted.length; i++)
            tainted[i] = Symbolicator.symbolic("toUpperLowerVar_" + i, test.charAt(i));

        String taintedString = new String(tainted, 0, tainted.length);

        String upper = taintedString.toUpperCase();
        String lower = taintedString.toLowerCase();

        assertEquals(test.toUpperCase(), upper);
        assertEquals(test.toLowerCase(), lower);

        ArrayList<SimpleEntry<String, Object>> solution = Symbolicator.dumpConstraints();
        System.out.println(solution);

        assertTrue(solution != null && !solution.isEmpty());

        Pattern p = Pattern.compile("toUpperLowerVar_([0-9][0-9]*)");

        for (SimpleEntry<String, Object> e : solution) {
            Matcher m = p.matcher(e.getKey());
            if (m.matches()) {
                int i = Integer.parseInt(m.group(1));
                assertEquals(test.toLowerCase().charAt(i), Character.toLowerCase((char) (int) e.getValue()));
            }
        }
    }

    @Test
    public void testStartsWith() throws Exception {
        String test = "This is a test";
        char tainted[] = new char[test.length()];

        for (int i = 0; i < tainted.length; i++)
            tainted[i] = Symbolicator.symbolic("startsWithVar_" + i, test.charAt(i));

        String taintedString = new String(tainted, 0, tainted.length);

        assertTrue(taintedString.startsWith("Th"));
        assertFalse(taintedString.startsWith("That"));
        assertFalse(taintedString.startsWith("Those"));

        ArrayList<SimpleEntry<String, Object>> solution = Symbolicator.dumpConstraints();
        assertTrue(solution != null && !solution.isEmpty());

        for (SimpleEntry<String, Object> e : solution) {
            switch (e.getKey()) {
                case "startsWithVar_0":
                    assertEquals('T', (char) (int) e.getValue());
                    break;
                case "startsWithVar_1":
                    assertEquals('h', (char) (int) e.getValue());
                    break;
                case "startsWithVar_2":
                    assertEquals('i', (char) (int) e.getValue());
                    break;
                case "startsWithVar_3":
                    assertEquals('s', (char) (int) e.getValue());
                    break;
                case "startsWithVar_4":
                    assertEquals(' ', (char) (int) e.getValue());
                    break;
            }
        }
    }

    @Test
    public void testStringOps() throws Exception {
        String MyStr = Symbolicator.symbolic("MyStr", new String("MyStr"));
        assertEquals("MyStr", MyStr);
        assertEquals("MyStrZ", MyStr.concat("Z"));
        assertEquals('M', MyStr.charAt(0));
        Symbolicator.dumpConstraints();
    }

    @Test
    public void testNoCodepoints() {
        String test = "No fancy codepoints here";
        char[] codepoints = test.toCharArray();

        for (int i = 0; i < codepoints.length; i++) codepoints[i] = Symbolicator.symbolic("nocp_" + i, codepoints[i]);

        String s = new String(codepoints, 0, codepoints.length);

        // Verify the string was constructed correctly
        assertEquals(test, s);

        // Should have constraints for the symbolic characters
        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());
        assertEquals(test.length(), constraints.size());
    }

    @Test
    public void testCodepoints() {
        String test = "I \uD800\uDF1E UTF-16";
        int[] codepoints = test.codePoints().toArray();

        for (int i = 0; i < codepoints.length; i++) codepoints[i] = Symbolicator.symbolic("utf16_" + i, codepoints[i]);

        String s = new String(codepoints, 0, codepoints.length);

        // Verify the string was constructed correctly
        assertEquals(test, s);

        // Should have constraints for the symbolic codepoints
        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());
        assertTrue(constraints.size() >= codepoints.length);
    }

    @Test
    public void testStringComparison() {
        String base = "hello";
        String symbolic1 = Symbolicator.symbolic("str1", "hello");
        String symbolic2 = Symbolicator.symbolic("str2", "world");

        // Test equality
        assertEquals(base, symbolic1);
        assertNotEquals(base, symbolic2);

        // Test comparison
        assertTrue(symbolic1.compareTo(symbolic2) < 0);
        assertEquals(0, symbolic1.compareTo(base));

        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        boolean hasStr1 = false, hasStr2 = false;
        for (SimpleEntry<String, Object> constraint : constraints) {
            switch (constraint.getKey()) {
                case "str1":
                    hasStr1 = true;
                    assertEquals("hello", constraint.getValue());
                    break;
                case "str2":
                    hasStr2 = true;
                    assertEquals("world", constraint.getValue());
                    break;
            }
        }

        assertTrue(hasStr1, "Missing str1 constraint");
        assertTrue(hasStr2, "Missing str2 constraint");
    }

    @Test
    public void testStringSubstring() {
        String test = "Hello World";
        String symbolicStr = Symbolicator.symbolic("substr_input", test);

        String sub1 = symbolicStr.substring(0, 5);
        String sub2 = symbolicStr.substring(6);

        assertEquals("Hello", sub1);
        assertEquals("World", sub2);

        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        boolean hasInput = false;
        for (SimpleEntry<String, Object> constraint : constraints) {
            if ("substr_input".equals(constraint.getKey())) {
                hasInput = true;
                assertEquals(test, constraint.getValue());
                break;
            }
        }

        assertTrue(hasInput, "Missing substr_input constraint");
    }

    @Test
    public void testStringContains() {
        String test = "Hello World";
        String symbolicStr = Symbolicator.symbolic("contains_input", test);

        assertTrue(symbolicStr.contains("Hello"));
        assertTrue(symbolicStr.contains("World"));
        assertFalse(symbolicStr.contains("xyz"));

        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        boolean hasInput = false;
        for (SimpleEntry<String, Object> constraint : constraints) {
            if ("contains_input".equals(constraint.getKey())) {
                hasInput = true;
                assertEquals(test, constraint.getValue());
                break;
            }
        }

        assertTrue(hasInput, "Missing contains_input constraint");
    }

    @Test
    public void testStringReplace() {
        String test = "Hello World";
        String symbolicStr = Symbolicator.symbolic("replace_input", test);

        String replaced = symbolicStr.replace("World", "Universe");
        assertEquals("Hello Universe", replaced);

        String replaced2 = symbolicStr.replace('o', 'a');
        assertEquals("Hella Warld", replaced2);

        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        boolean hasInput = false;
        for (SimpleEntry<String, Object> constraint : constraints) {
            if ("replace_input".equals(constraint.getKey())) {
                hasInput = true;
                assertEquals(test, constraint.getValue());
                break;
            }
        }

        assertTrue(hasInput, "Missing replace_input constraint");
    }
}
