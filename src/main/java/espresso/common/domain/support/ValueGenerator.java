
package espresso.common.domain.support;

import java.security.SecureRandom;

public class ValueGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static int generateRandomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min value must be less than or equal to max value");
        }
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    public static double generateRandomDecimal(double min, double max, int precision) {
        if (min > max) {
            throw new IllegalArgumentException("Min value must be less than or equal to max value");
        }
        double scale = Math.pow(10, precision);
        return Math.round((min + (max - min) * RANDOM.nextDouble()) * scale) / scale;
    }
}
