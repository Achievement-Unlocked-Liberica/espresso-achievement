package espresso.common.domain.support;

import java.util.concurrent.ThreadLocalRandom;

public class KeyGenerator {
    
    private static final String baseCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    public static String generateKey(int length) {
        char[] key = new char[length];

        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (ThreadLocalRandom.current().nextDouble() * baseCharacters.length());
            key[i] = baseCharacters.charAt(randomIndex);
        }
        return new String(key);
    }
}
