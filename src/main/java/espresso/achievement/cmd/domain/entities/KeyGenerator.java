package espresso.achievement.cmd.domain.entities;

public class KeyGenerator {
    
    public static String generateShortString() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortString = new StringBuilder();
        long seedNumber = System.currentTimeMillis();

        while (seedNumber > 0) {
            int index = (int) (seedNumber % characters.length());
            shortString.append(characters.charAt(index));
            seedNumber /= characters.length();
        }

        return shortString.toString();
    }
}