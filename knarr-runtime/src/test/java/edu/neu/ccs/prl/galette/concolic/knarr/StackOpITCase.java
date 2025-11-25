package edu.neu.ccs.prl.galette.concolic.knarr;

import static org.junit.jupiter.api.Assertions.*;

import edu.neu.ccs.prl.galette.concolic.knarr.compat.Symbolicator;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Stack operation integration tests for Galette-based Knarr runtime.
 *
 * These tests are migrated from the original Knarr test suite and validate
 * basic arithmetic operations, conditional jumps, and symbolic execution
 * functionality using Galette's concolic execution infrastructure.
 *
 * @author [Anne Koziolek](https://github.com/AnneKoziolek)
 */
public class StackOpITCase {

    @BeforeEach
    public void setUp() {
        // Reset symbolicator state before each test
        Symbolicator.reset();
    }

    @Test
    public void testSimpleJump() throws Exception {
        int Z = Symbolicator.symbolic("Z", 5);

        if (Z > 0) {
            System.out.println("Z>0");
        } else {
            System.out.println("Z<=0");
        }

        // Should have generated path constraints for the conditional
        assertFalse(Symbolicator.dumpConstraints().isEmpty());
    }

    @Test
    public void testIinc() {
        int sum = Symbolicator.symbolic("sum", 0);

        for (int i = Symbolicator.symbolic("inc", 0); i < 4; i++) {
            sum += i;
        }

        assertEquals(6, sum);

        ArrayList<SimpleEntry<String, Object>> ret = Symbolicator.dumpConstraints("lol");
        assertFalse(ret.isEmpty());

        for (SimpleEntry<String, Object> e : ret) {
            switch (e.getKey()) {
                case "inc":
                    assertEquals(0, e.getValue());
                    break;
                case "sum":
                    assertEquals(0, e.getValue());
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    }

    @Test
    public void testInt() {
        int a;
        int b = 6;
        int c;
        int d;
        int res1, res2, res3, res4, res5, res6, res7, res8, res9, res10, res11;

        // Test addition
        {
            a = Symbolicator.symbolic("a1", 5);
            c = a + b;
            int res = (res1 = 11);
            d = Symbolicator.symbolic("d1", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test subtraction
        {
            a = Symbolicator.symbolic("a2", 5);
            c = a - b;
            int res = (res2 = -1);
            d = Symbolicator.symbolic("d2", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test multiplication
        {
            a = Symbolicator.symbolic("a3", 5);
            c = a * b;
            int res = (res3 = 30);
            d = Symbolicator.symbolic("d3", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test division
        {
            a = Symbolicator.symbolic("a4", 5);
            c = a / b;
            int res = (res4 = 0);
            d = Symbolicator.symbolic("d4", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test modulo
        {
            a = Symbolicator.symbolic("a5", 5);
            c = a % b;
            int res = (res5 = 5);
            d = Symbolicator.symbolic("d5", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test bitwise AND
        {
            a = Symbolicator.symbolic("a6", 5);
            c = a & b;
            int res = (res6 = 4);
            d = Symbolicator.symbolic("d6", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test bitwise OR
        {
            a = Symbolicator.symbolic("a7", 5);
            c = a | b;
            int res = (res7 = 7);
            d = Symbolicator.symbolic("d7", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test bitwise XOR
        {
            a = Symbolicator.symbolic("a8", 5);
            c = a ^ b;
            int res = (res8 = 3);
            d = Symbolicator.symbolic("d8", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test left shift
        {
            a = Symbolicator.symbolic("a9", 5);
            c = a << 1;
            int res = (res9 = 10);
            d = Symbolicator.symbolic("d9", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test right shift
        {
            a = Symbolicator.symbolic("a10", 10);
            c = a >> 1;
            int res = (res10 = 5);
            d = Symbolicator.symbolic("d10", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test unsigned right shift
        {
            a = Symbolicator.symbolic("a11", -10);
            c = a >>> 1;
            int res = (res11 = Integer.MAX_VALUE - 4);
            d = Symbolicator.symbolic("d11", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }
    }

    @Test
    public void testLong() {
        long a;
        long b = 6L;
        long c;
        long d;
        long res1, res2, res3, res4, res5, res6, res7, res8, res9, res10, res11;

        // Test addition
        {
            a = Symbolicator.symbolic("la1", 5L);
            c = a + b;
            long res = (res1 = 11L);
            d = Symbolicator.symbolic("ld1", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test subtraction
        {
            a = Symbolicator.symbolic("la2", 5L);
            c = a - b;
            long res = (res2 = -1L);
            d = Symbolicator.symbolic("ld2", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test multiplication
        {
            a = Symbolicator.symbolic("la3", 5L);
            c = a * b;
            long res = (res3 = 30L);
            d = Symbolicator.symbolic("ld3", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test division
        {
            a = Symbolicator.symbolic("la4", 5L);
            c = a / b;
            long res = (res4 = 0L);
            d = Symbolicator.symbolic("ld4", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test modulo
        {
            a = Symbolicator.symbolic("la5", 5L);
            c = a % b;
            long res = (res5 = 5L);
            d = Symbolicator.symbolic("ld5", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }
    }

    @Test
    public void testFloat() {
        float a;
        float b = 6.0f;
        float c;
        float d;
        float res1, res2, res3, res4;

        // Test addition
        {
            a = Symbolicator.symbolic("fa1", 5.0f);
            c = a + b;
            float res = (res1 = 11.0f);
            d = Symbolicator.symbolic("fd1", res);
            assertEquals(c, res, 0.001f);
            assertEquals(c, d, 0.001f);
        }

        // Test subtraction
        {
            a = Symbolicator.symbolic("fa2", 5.0f);
            c = a - b;
            float res = (res2 = -1.0f);
            d = Symbolicator.symbolic("fd2", res);
            assertEquals(c, res, 0.001f);
            assertEquals(c, d, 0.001f);
        }

        // Test multiplication
        {
            a = Symbolicator.symbolic("fa3", 5.0f);
            c = a * b;
            float res = (res3 = 30.0f);
            d = Symbolicator.symbolic("fd3", res);
            assertEquals(c, res, 0.001f);
            assertEquals(c, d, 0.001f);
        }

        // Test division
        {
            a = Symbolicator.symbolic("fa4", 5.0f);
            c = a / b;
            float res = (res4 = 5.0f / 6.0f);
            d = Symbolicator.symbolic("fd4", res);
            assertEquals(c, res, 0.001f);
            assertEquals(c, d, 0.001f);
        }
    }

    @Test
    public void testDouble() {
        double a;
        double b = 6.0;
        double c;
        double d;
        double res1, res2, res3, res4;

        // Test addition
        {
            a = Symbolicator.symbolic("da1", 5.0);
            c = a + b;
            double res = (res1 = 11.0);
            d = Symbolicator.symbolic("dd1", res);
            assertEquals(c, res, 0.001);
            assertEquals(c, d, 0.001);
        }

        // Test subtraction
        {
            a = Symbolicator.symbolic("da2", 5.0);
            c = a - b;
            double res = (res2 = -1.0);
            d = Symbolicator.symbolic("dd2", res);
            assertEquals(c, res, 0.001);
            assertEquals(c, d, 0.001);
        }

        // Test multiplication
        {
            a = Symbolicator.symbolic("da3", 5.0);
            c = a * b;
            double res = (res3 = 30.0);
            d = Symbolicator.symbolic("dd3", res);
            assertEquals(c, res, 0.001);
            assertEquals(c, d, 0.001);
        }

        // Test division
        {
            a = Symbolicator.symbolic("da4", 5.0);
            c = a / b;
            double res = (res4 = 5.0 / 6.0);
            d = Symbolicator.symbolic("dd4", res);
            assertEquals(c, res, 0.001);
            assertEquals(c, d, 0.001);
        }
    }

    @Test
    public void testByte() {
        byte a;
        byte b = 6;
        byte c;
        byte d;
        byte res1, res2, res3, res4, res5;

        // Test addition
        {
            a = Symbolicator.symbolic("ba1", (byte) 5);
            c = (byte) (a + b);
            byte res = (res1 = 11);
            d = Symbolicator.symbolic("bd1", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test subtraction
        {
            a = Symbolicator.symbolic("ba2", (byte) 5);
            c = (byte) (a - b);
            byte res = (res2 = -1);
            d = Symbolicator.symbolic("bd2", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test multiplication
        {
            a = Symbolicator.symbolic("ba3", (byte) 5);
            c = (byte) (a * b);
            byte res = (res3 = 30);
            d = Symbolicator.symbolic("bd3", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test division
        {
            a = Symbolicator.symbolic("ba4", (byte) 5);
            c = (byte) (a / b);
            byte res = (res4 = 0);
            d = Symbolicator.symbolic("bd4", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test modulo
        {
            a = Symbolicator.symbolic("ba5", (byte) 5);
            c = (byte) (a % b);
            byte res = (res5 = 5);
            d = Symbolicator.symbolic("bd5", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }
    }

    @Test
    public void testChar() {
        char a;
        char b = 6;
        char c;
        char d;

        // Test basic char operations
        {
            a = Symbolicator.symbolic("ca1", (char) 5);
            c = (char) (a + b);
            char res = (char) 11;
            d = Symbolicator.symbolic("cd1", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test with actual character values
        {
            a = Symbolicator.symbolic("ca2", 'A');
            int result = a + 1;
            char expected = 'B';
            assertEquals(expected, (char) result);
        }
    }

    @Test
    public void testShort() {
        short a;
        short b = 6;
        short c;
        short d;
        short res1, res2, res3;

        // Test addition
        {
            a = Symbolicator.symbolic("sa1", (short) 5);
            c = (short) (a + b);
            short res = (res1 = 11);
            d = Symbolicator.symbolic("sd1", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test subtraction
        {
            a = Symbolicator.symbolic("sa2", (short) 5);
            c = (short) (a - b);
            short res = (res2 = -1);
            d = Symbolicator.symbolic("sd2", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }

        // Test multiplication
        {
            a = Symbolicator.symbolic("sa3", (short) 5);
            c = (short) (a * b);
            short res = (res3 = 30);
            d = Symbolicator.symbolic("sd3", res);
            assertEquals(c, res);
            assertEquals(c, d);
        }
    }

    @Test
    public void testComplexExpression() {
        int x = Symbolicator.symbolic("x", 10);
        int y = Symbolicator.symbolic("y", 5);
        int z = Symbolicator.symbolic("z", 2);

        // Complex arithmetic expression
        int result = (x + y) * z - (x % y);
        int expected = (10 + 5) * 2 - (10 % 5);

        assertEquals(expected, result);

        // Should have constraints for the symbolic operations
        ArrayList<SimpleEntry<String, Object>> constraints = Symbolicator.dumpConstraints();
        assertFalse(constraints.isEmpty());

        // Verify we have the expected symbolic variables
        boolean hasX = false, hasY = false, hasZ = false;
        for (SimpleEntry<String, Object> constraint : constraints) {
            switch (constraint.getKey()) {
                case "x":
                    hasX = true;
                    assertEquals(10, constraint.getValue());
                    break;
                case "y":
                    hasY = true;
                    assertEquals(5, constraint.getValue());
                    break;
                case "z":
                    hasZ = true;
                    assertEquals(2, constraint.getValue());
                    break;
            }
        }

        assertTrue(hasX, "Missing symbolic variable x");
        assertTrue(hasY, "Missing symbolic variable y");
        assertTrue(hasZ, "Missing symbolic variable z");
    }
}
