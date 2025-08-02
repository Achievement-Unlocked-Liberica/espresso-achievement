package espresso.common.domain.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NameGenerator {

    private static final String NAMES_FILE_PATH = "/random-race-names.txt";
    private static final Random random = new Random();

    private static List<String> namesCache = null;

    static {
        try {
            namesCache = loadNamesFromFile();
        } catch (IOException e) {
            System.err.println("Failed to initialize name cache: " + e.getMessage());
            namesCache = new ArrayList<>();
        }
    }

    /**
     * Generates a random profile name by reading from the random-race-names.txt
     * file
     * and returning a random line in the format: {Race Name} - {First Name} {Last
     * Name}
     * 
     * @return A random profile name string, or null if unable to read the file
     */
    public static String generateProfileName() {
        try {
            int randomIndex = random.nextInt(namesCache.size());
            return namesCache.get(randomIndex);

        } catch (Exception e) {
            // Log error and return null if file cannot be read
            System.err.println("Error generating profile name: " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads all names from the random-race-names.txt file
     * 
     * @return List of name strings from the file
     * @throws IOException if the file cannot be read
     */
    private static List<String> loadNamesFromFile() throws IOException {
        List<String> names = new ArrayList<>();

        try (InputStream inputStream = NameGenerator.class.getResourceAsStream(NAMES_FILE_PATH);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new IOException("Names file not found: " + NAMES_FILE_PATH);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    names.add(line);
                }
            }
        }

        return names;
    }
}
