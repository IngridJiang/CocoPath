package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.compat.Symbolicator;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Array operation integration tests for Galette-based Knarr runtime.
 *
 * These tests are migrated from the original Knarr test suite and validate
 * array operations, symbolic array indices, and array manipulation
 * functionality using Galette's concolic execution infrastructure.
 *
 * @author [Anne Koziolek](https://github.com/AnneKoziolek)
 */
public class ArrayOpITCase {

    @BeforeEach
    public void setUp() {
        // Reset symbolicator state before each test
        Symbolicator.reset();
    }

    @Test
    public void testArrayCopy() throws Exception {
        byte tainted[] = new byte[1];
        byte source[] = new byte[1];

        tainted[0] = Symbolicator.symbolic("a2", (byte) 'a');
        source[0] = 'b';

        assertEquals(tainted[0], 'a');

        System.arraycopy(source, 0, tainted, 0, 1);

        assertNotEquals(tainted[0], 'a');

        assertFalse(Symbolicator.dumpConstraints().isEmpty());
    }

    @Test
    public void testArrayWriteOnConstantIndex() throws Exception {
        byte tainted[] = new byte[1];

        tainted[0] = Symbolicator.symbolic("a", (byte) 'a');

        if (tainted[0] == 'a') {
            tainted[0] = 'b';
        }

        assertNotEquals(tainted[0], 'a');

        assertFalse(Symbolicator.dumpConstraints().isEmpty());
    }

    @Test
    public void testArrayWriteOnVariableIndex() throws Exception {
        byte tainted[] = new byte[1];

        tainted[0] = Symbolicator.symbolic("a1", (byte) 'a');

        for (int i = 0; i < tainted.length; i++) {
            tainted[i] = (byte) (((int) tainted[i]) ^ ((int) 'b'));
        }

        if (tainted[0] != (((int) 'a') ^ ((int) 'b'))) {
            throw new Error();
        }

        ArrayList<SimpleEntry<String, Object>> solution = Symbolicator.dumpConstraints();
        assertFalse(solution.isEmpty());
        for (SimpleEntry<String, Object> e : solution) {
            switch (e.getKey()) {
                case "a1":
                    // The value should be the original character value as int
                    assertEquals((int) 'a', e.getValue());
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    }

    @Test
    public void testArrayWriteTaintedIdx() throws Exception {
        byte arr[] = new byte[2];

        arr[0] = 'a';
        arr[1] = 'b';

        int taintedIdx = Symbolicator.symbolic("idx", 0);

        if (arr[taintedIdx] == 'a') {
            arr[taintedIdx] = 'b';
        }

        assertNotEquals(arr[taintedIdx], 'a');

        ArrayList<SimpleEntry<String, Object>> solution = Symbolicator.dumpConstraints();
        assertFalse(solution.isEmpty());
        for (SimpleEntry<String, Object> e : solution) {
            switch (e.getKey()) {
                case "idx":
                    assertEquals(0, e.getValue());
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    }

    @Test
    public void testArrayIndex() {
        int[] arr = new int[5];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i * 2;
        }

        int symbolicIndex = Symbolicator.symbolic("index", 2);
        int value = arr[symbolicIndex];

        assertEquals(4, value); // arr[2] = 2 * 2 = 4

        // Should have constraints for the symbolic index
        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        boolean hasIndex = false;
        for (SimpleEntry<String, Object> constraint : constraints) {
            if ("index".equals(constraint.getKey())) {
                hasIndex = true;
                assertEquals(2, constraint.getValue());
                break;
            }
        }

        assertTrue(hasIndex, "Missing symbolic index constraint");
    }

    @Test
    public void testMultiDimensionalArray() {
        int[][] matrix = new int[3][3];

        // Initialize matrix
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix[i][j] = i * 3 + j;
            }
        }

        int row = Symbolicator.symbolic("row", 1);
        int col = Symbolicator.symbolic("col", 2);

        int value = matrix[row][col];
        assertEquals(5, value); // matrix[1][2] = 1 * 3 + 2 = 5

        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        boolean hasRow = false, hasCol = false;
        for (SimpleEntry<String, Object> constraint : constraints) {
            switch (constraint.getKey()) {
                case "row":
                    hasRow = true;
                    assertEquals(1, constraint.getValue());
                    break;
                case "col":
                    hasCol = true;
                    assertEquals(2, constraint.getValue());
                    break;
            }
        }

        assertTrue(hasRow, "Missing symbolic row constraint");
        assertTrue(hasCol, "Missing symbolic col constraint");
    }

    @Test
    public void testArrayLength() {
        int[] arr = new int[Symbolicator.symbolic("length", 5)];

        assertEquals(5, arr.length);

        // Test accessing elements within bounds
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }

        assertEquals(0, arr[0]);
        assertEquals(4, arr[4]);

        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        boolean hasLength = false;
        for (SimpleEntry<String, Object> constraint : constraints) {
            if ("length".equals(constraint.getKey())) {
                hasLength = true;
                assertEquals(5, constraint.getValue());
                break;
            }
        }

        assertTrue(hasLength, "Missing symbolic length constraint");
    }

    @Test
    public void testArrayOfDifferentTypes() {
        // Test byte array
        byte[] byteArr = new byte[3];
        byteArr[0] = Symbolicator.symbolic("byte_val", (byte) 42);
        assertEquals(42, byteArr[0]);

        // Test char array
        char[] charArr = new char[3];
        charArr[0] = Symbolicator.symbolic("char_val", 'X');
        assertEquals('X', charArr[0]);

        // Test short array
        short[] shortArr = new short[3];
        shortArr[0] = Symbolicator.symbolic("short_val", (short) 1000);
        assertEquals(1000, shortArr[0]);

        // Test long array
        long[] longArr = new long[3];
        longArr[0] = Symbolicator.symbolic("long_val", 123456789L);
        assertEquals(123456789L, longArr[0]);

        // Test float array
        float[] floatArr = new float[3];
        floatArr[0] = Symbolicator.symbolic("float_val", 3.14f);
        assertEquals(3.14f, floatArr[0], 0.001f);

        // Test double array
        double[] doubleArr = new double[3];
        doubleArr[0] = Symbolicator.symbolic("double_val", 2.71828);
        assertEquals(2.71828, doubleArr[0], 0.001);

        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        // Should have constraints for all symbolic values
        assertTrue(constraints.size() >= 6, "Expected at least 6 symbolic values");
    }

    @Test
    public void testArrayBounds() {
        int[] arr = new int[3];
        arr[0] = 10;
        arr[1] = 20;
        arr[2] = 30;

        int index = Symbolicator.symbolic("bounds_index", 1);

        // Test valid access
        int value = arr[index];
        assertEquals(20, value);

        // Test conditional based on array value
        if (arr[index] == 20) {
            arr[index] = 25;
        }

        assertEquals(25, arr[index]);

        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        boolean hasIndex = false;
        for (SimpleEntry<String, Object> constraint : constraints) {
            if ("bounds_index".equals(constraint.getKey())) {
                hasIndex = true;
                assertEquals(1, constraint.getValue());
                break;
            }
        }

        assertTrue(hasIndex, "Missing bounds_index constraint");
    }

    @Test
    public void testArraySort() {
        int[] arr = new int[5];
        arr[0] = Symbolicator.symbolic("val0", 5);
        arr[1] = Symbolicator.symbolic("val1", 2);
        arr[2] = Symbolicator.symbolic("val2", 8);
        arr[3] = Symbolicator.symbolic("val3", 1);
        arr[4] = Symbolicator.symbolic("val4", 9);

        // Simple bubble sort to test array operations
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }

        // Array should be sorted
        assertEquals(1, arr[0]);
        assertEquals(2, arr[1]);
        assertEquals(5, arr[2]);
        assertEquals(8, arr[3]);
        assertEquals(9, arr[4]);

        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        // Should have all 5 symbolic values
        assertEquals(5, constraints.size());
    }

    @Test
    public void testNullArray() {
        int[] arr = null;

        try {
            int length = arr.length;
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }

        try {
            arr[0] = 5;
            fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
            // Expected
        }
    }
}
