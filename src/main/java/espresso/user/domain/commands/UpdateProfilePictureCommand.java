package espresso.user.domain.commands;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import espresso.common.domain.commands.CommonCommand;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class UpdateProfilePictureCommand extends CommonCommand {

    // Constants for file size validation
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

    // Constants for image dimensions validation
    private static final int MIN_IMAGE_DIMENSION = 100; // pixels
    private static final int MAX_IMAGE_DIMENSION = 2000; // pixels

    // Constants for file type validation
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp");

    // Constants for error messages
    private static final String ERROR_EMPTY_IMAGE = "LOCALIZE: PROFILE PICTURE CANNOT BE EMPTY";
    private static final String ERROR_FILE_SIZE = "LOCALIZE: PROFILE PICTURE EXCEEDS MAXIMUM SIZE OF 5MB";
    private static final String ERROR_FILE_TYPE = "LOCALIZE: INVALID FILE TYPE. ONLY JPEG, PNG, GIF, AND WEBP FORMATS ARE ALLOWED";
    private static final String ERROR_INVALID_IMAGE = "LOCALIZE: INVALID IMAGE FILE";
    private static final String ERROR_IMAGE_TOO_SMALL = "LOCALIZE: IMAGE DIMENSIONS TOO SMALL. MINIMUM SIZE IS 100X100 PIXELS";
    private static final String ERROR_IMAGE_TOO_LARGE = "LOCALIZE: IMAGE DIMENSIONS TOO LARGE. MAXIMUM SIZE IS 2000X2000 PIXELS";
    private static final String ERROR_INVALID_FILENAME = "LOCALIZE: INVALID FILENAME. ONLY ALPHANUMERIC CHARACTERS, DOTS, HYPHENS, AND UNDERSCORES ARE ALLOWED";
    private static final String ERROR_PROCESSING_IMAGE = "LOCALIZE: FAILED TO PROCESS IMAGE: %s";
    private static final String FILENAME_REGEX_PATTERN = "^[a-zA-Z0-9._-]+$";

    @NotBlank(message = "LOCALIZE: REGISTERED USER KEY IS REQUIRED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    private String registeredUserKey;

    private MultipartFile image;

    @Override
    public Set<String> validate() {
        Set<String> errors = super.validate();
        if (errors == null || errors.isEmpty()) {
            errors = new HashSet<>();
        } // Check if image exists
        if (image == null || image.isEmpty()) {
            errors.add("image:" + ERROR_EMPTY_IMAGE);
            return errors; // Return early as we can't validate further without an image
        }

        // Validate file size
        if (image.getSize() > MAX_FILE_SIZE_BYTES) {
            errors.add("image:" + ERROR_FILE_SIZE);
        }

        // Validate file content type (MIME type)
        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            errors.add("image:" + ERROR_FILE_TYPE);
        }

        // Validate image dimensions
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream()); // Check if it's actually a valid image
            if (bufferedImage == null) {
                errors.add("image:" + ERROR_INVALID_IMAGE);
                return errors; // Return early as we can't check dimensions without a valid image
            }

            // Check dimensions
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            if (width < MIN_IMAGE_DIMENSION || height < MIN_IMAGE_DIMENSION) {
                errors.add("image:" + ERROR_IMAGE_TOO_SMALL);
            }

            if (width > MAX_IMAGE_DIMENSION || height > MAX_IMAGE_DIMENSION) {
                errors.add("image:" + ERROR_IMAGE_TOO_LARGE);
            }
        } catch (IOException e) {
            errors.add("image:" + String.format(ERROR_PROCESSING_IMAGE, e.getMessage()));
        } // Validate filename
        String originalFilename = image.getOriginalFilename();
        if (originalFilename != null && !originalFilename.matches(FILENAME_REGEX_PATTERN)) {
            errors.add("filename:" + ERROR_INVALID_FILENAME);
        }

        return errors;
    }

}
