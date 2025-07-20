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
import org.springframework.web.multipart.MultipartFile;

public class UploadAchievementMediaCommandTest {

    private UploadAchievementMediaCommand command;

    @BeforeEach
    void setUp() {
        command = new UploadAchievementMediaCommand();
    }

    @Test
    void testValidCommand() throws IOException {
        // Create valid mock image files
        byte[] imageData = createValidImageData();
        MockMultipartFile mockFile1 = new MockMultipartFile(
            "images", 
            "test-image1.jpg", 
            "image/jpeg", 
            imageData
        );
        MockMultipartFile mockFile2 = new MockMultipartFile(
            "images", 
            "test-image2.jpg", 
            "image/jpeg", 
            imageData
        );
        MultipartFile[] mockFiles = {mockFile1, mockFile2};

        command = new UploadAchievementMediaCommand("ACHI001", "USER123", mockFiles);

        Set<String> errors = command.validate();
        
        // Debug: Print validation errors
        System.out.println("Validation errors: " + errors);
        
        assertTrue(errors.isEmpty(), "Valid command should not have validation errors. Errors: " + errors);
    }

    @Test
    void testInvalidAchievementKey() {
        MockMultipartFile mockFile = new MockMultipartFile(
            "images", 
            "test-image.jpg", 
            "image/jpeg", 
            createValidImageData()
        );
        MultipartFile[] mockFiles = {mockFile};

        command = new UploadAchievementMediaCommand("", "USER123", mockFiles);

        Set<String> errors = command.validate();
        assertFalse(errors.isEmpty(), "Empty achievement key should cause validation error");
    }

    @Test
    void testMissingImages() {
        command = new UploadAchievementMediaCommand("ACHI001", "USER123", null);

        Set<String> errors = command.validate();
        assertTrue(errors.stream().anyMatch(error -> error.contains("ACHIEVEMENT MEDIA CANNOT BE EMPTY")), 
                   "Missing images should cause validation error");
    }

    @Test
    void testEmptyImageArray() {
        MultipartFile[] emptyFiles = {};
        command = new UploadAchievementMediaCommand("ACHI001", "USER123", emptyFiles);

        Set<String> errors = command.validate();
        assertTrue(errors.stream().anyMatch(error -> error.contains("ACHIEVEMENT MEDIA CANNOT BE EMPTY")), 
                   "Empty image array should cause validation error");
    }

    @Test
    void testInvalidImageType() {
        MockMultipartFile invalidFile = new MockMultipartFile(
            "images", 
            "test-doc.txt", 
            "text/plain", 
            "Not an image".getBytes()
        );
        MockMultipartFile validFile = new MockMultipartFile(
            "images", 
            "test-image.jpg", 
            "image/jpeg", 
            createValidImageData()
        );
        MultipartFile[] mockFiles = {validFile, invalidFile};

        command = new UploadAchievementMediaCommand("ACHI001", "USER123", mockFiles);

        Set<String> errors = command.validate();
        assertTrue(errors.stream().anyMatch(error -> error.contains("INVALID FILE TYPE") && error.contains("Image 2")), 
                   "Invalid file type should cause validation error with proper indexing");
    }

    @Test
    void testOversizedImage() {
        // Create a file that exceeds 10MB
        byte[] largeData = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile oversizedFile = new MockMultipartFile(
            "images", 
            "large-image.jpg", 
            "image/jpeg", 
            largeData
        );
        MockMultipartFile validFile = new MockMultipartFile(
            "images", 
            "test-image.jpg", 
            "image/jpeg", 
            createValidImageData()
        );
        MultipartFile[] mockFiles = {validFile, oversizedFile};

        command = new UploadAchievementMediaCommand("ACHI001", "USER123", mockFiles);

        Set<String> errors = command.validate();
        assertTrue(errors.stream().anyMatch(error -> error.contains("EXCEEDS MAXIMUM SIZE") && error.contains("Image 2")), 
                   "Oversized file should cause validation error with proper indexing");
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
