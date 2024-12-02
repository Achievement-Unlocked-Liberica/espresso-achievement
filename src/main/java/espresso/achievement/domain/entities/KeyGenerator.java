package espresso.achievement.domain.entities;

import java.security.SecureRandom;
import java.util.Date;

public class KeyGenerator {

    public static String generateShortString() {
        SecureRandom random = new SecureRandom();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortString = new StringBuilder();
        long oldNumber = 0;
        long seedNumber = 0;

        do {
            seedNumber = Math.abs(random.nextLong() + System.currentTimeMillis());
        } while (oldNumber == seedNumber);

        System.out.println("seedNumber: " + seedNumber);

        while (seedNumber > 0) {
            int index = (int) (seedNumber % characters.length());
            shortString.append(characters.charAt(index));
            seedNumber /= characters.length();
        }

        System.out.println("shortString: " + shortString.toString());

        return shortString.toString();
    }
    
    public static String generateKeyString(int length) {
        SecureRandom random = new SecureRandom();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_=+";
        StringBuilder keyString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            keyString.append(characters.charAt(index));
        }

        return keyString.toString();
    }
}