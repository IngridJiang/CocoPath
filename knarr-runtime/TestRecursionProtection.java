public class TestRecursionProtection {
    public static void main(String[] args) {
        System.out.println("Starting recursion protection test...");
        
        // This will trigger String.hashCode comparisons
        String s1 = "Hello";
        String s2 = "World";
        
        // Force hashCode computation which uses comparisons internally
        int h1 = s1.hashCode();
        int h2 = s2.hashCode();
        
        // If we get here without stack overflow, recursion protection works
        System.out.println("SUCCESS: No stack overflow detected!");
        System.out.println("Hash1: " + h1 + ", Hash2: " + h2);
    }
}
