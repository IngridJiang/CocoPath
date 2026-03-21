public class TestAgent {
    public static void main(String[] args) {
        System.out.println("TestAgent running...");
        System.out.println("galette.concolic.interception.enabled = " + 
            System.getProperty("galette.concolic.interception.enabled"));
        
        // Do a simple comparison that should be intercepted
        double a = 10.0;
        double b = 20.0;
        boolean result = a > b;
        System.out.println("Comparison result: " + a + " > " + b + " = " + result);
        
        System.out.println("TestAgent completed.");
    }
}