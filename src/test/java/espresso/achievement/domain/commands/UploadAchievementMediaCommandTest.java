package espresso.achievement.domain.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class UploadAchievementMediaCommandTest {

    private UploadAchievementMediaCommand command;

    @BeforeEach
    void setUp() {
        command = new UploadAchievementMediaCommand();
    }

    @Test
    void testValidCommand() throws IOException {
        // Create a valid mock image file
        byte[] imageData = createValidImageData();
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", 
            "test-image.jpg", 
            "image/jpeg", 
            imageData
        );

        command = new UploadAchievementMediaCommand("ACHI001", "USER123", mockFile); // 7 characters

        Set<String> errors = command.validate();
        
        // Debug: Print validation errors
        System.out.println("Validation errors: " + errors);
        
        assertTrue(errors.isEmpty(), "Valid command should not have validation errors. Errors: " + errors);
    }

    @Test
    void testInvalidAchievementKey() {
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", 
            "test-image.jpg", 
            "image/jpeg", 
            createValidImageData()
        );

        command = new UploadAchievementMediaCommand("", "USER123", mockFile);

        Set<String> errors = command.validate();
        assertFalse(errors.isEmpty(), "Empty achievement key should cause validation error");
    }

    @Test
    void testMissingImage() {
        command = new UploadAchievementMediaCommand("ACHI001", "USER123", null); // 7 characters

        Set<String> errors = command.validate();
        assertTrue(errors.stream().anyMatch(error -> error.contains("ACHIEVEMENT MEDIA CANNOT BE EMPTY")), 
                   "Missing image should cause validation error");
    }

    @Test
    void testInvalidImageType() {
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", 
            "test-doc.txt", 
            "text/plain", 
            "Not an image".getBytes()
        );

        command = new UploadAchievementMediaCommand("ACHI001", "USER123", mockFile); // 7 characters

        Set<String> errors = command.validate();
        assertTrue(errors.stream().anyMatch(error -> error.contains("INVALID FILE TYPE")), 
                   "Invalid file type should cause validation error");
    }

    @Test
    void testOversizedImage() {
        // Create a file that exceeds 10MB
        byte[] largeData = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", 
            "large-image.jpg", 
            "image/jpeg", 
            largeData
        );

        command = new UploadAchievementMediaCommand("ACHI001", "USER123", mockFile); // 7 characters

        Set<String> errors = command.validate();
        assertTrue(errors.stream().anyMatch(error -> error.contains("EXCEEDS MAXIMUM SIZE")), 
                   "Oversized file should cause validation error");
    }

    private byte[] createValidImageData() {
        try {
            // Create a valid 300x300 pixel BufferedImage (within acceptable dimensions)
            BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            
            // Fill with a simple pattern
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    image.setRGB(x, y, 0xFF0000); // Red color
                }
            }
            
            // Convert to JPEG byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test image", e);
        }
    }
}
