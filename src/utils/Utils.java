package utils;

import java.util.Random;

public class Utils {
    private static final Random random = new Random();

    // Suppress default constructor for noninstantiability
    private Utils() {
        throw new AssertionError("Cannot instantiate " + this.getClass());
    }
    
    public static double round(double number, int digits) {
        double factor = Math.pow(10, digits);
        return Math.round(number * factor) / factor;
    }
    
    public static String format(double number, int digits) {
        return String.valueOf(round(number, digits));
    }
    
    public static double random(double a, double b) {
        return a + random.nextDouble() * (b - a);
    }
    
    public static double normal(double mean, double sd) {
        return mean + random.nextGaussian() * sd;
    }
    
    public static String timeFormat(double seconds) {
        if (seconds < 60) return String.valueOf((int) seconds) + "s";
        else {
            int m = (int) Math.floor(seconds / 60);
            int s = (int) seconds % 60;
            if (m < 60) return String.valueOf(m) + "min" + s + "s";
            else {
                int h = m / 60;
                m = m % 60;
                return String.valueOf(h) + "h" + m + "min" + s + "s";
            }
        }
    }
    
}
