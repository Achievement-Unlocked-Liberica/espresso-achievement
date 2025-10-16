package espresso.challenge.domain.commands;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import espresso.common.domain.commands.CommonCommand;
import io.swagger.v3.oas.annotations.media.Schema;

import espresso.challenge.domain.constants.ChallengeConstants;

/**
 * Command for uploading media files to an existing challenge.
 * Contains validation for image file types, sizes, dimensions, and filenames.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class UploadChallengeMediaCommand extends CommonCommand {

    /**
     * The 7-character alphanumeric key of the challenge to upload media to.
     * This value is obtained from the URL path parameter.
     */
    @JsonIgnore
    @Schema(hidden = true)
    @NotBlank(message = "LOCALIZE: CHALLENGE KEY IS REQUIRED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    private String challengeKey;

    /**
     * The 7-character alphanumeric key of the user uploading the media.
     * This value is obtained from the JWT token and not from the request body.
     */
    @JsonIgnore
    @Schema(hidden = true)
    @NotBlank(message = "LOCALIZE: USER KEY IS REQUIRED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    private String userKey;

    /**
     * Array of image files to upload. Supports JPEG, PNG, GIF, and WebP formats.
     * Each file must be under 10MB and have dimensions between 200x200 and
     * 3000x3000 pixels.
     */
    private MultipartFile[] images;

    /**
     * Performs comprehensive validation of uploaded image files.
     * Validates file presence, size, content type, dimensions, and filename format.
     * 
     * @return Set of validation error messages, empty if valid
     */
    @Override
    public Set<String> validateCustom() {
        Set<String> errors = new HashSet<>();

        // Check if images array exists and is not empty
        if (images == null || images.length == 0) {
            errors.add("images:" + ChallengeConstants.ERROR_EMPTY_IMAGE);
            return errors; // Return early as we can't validate further without images
        }

        // Validate each image in the array
        for (int i = 0; i < images.length; i++) {
            MultipartFile image = images[i];
            String fieldPrefix = "Image " + (i + 1) + ": "; // 1-based indexing for user-friendly messages

            // Check if individual image exists
            if (image == null || image.isEmpty()) {
                errors.add(fieldPrefix + ChallengeConstants.ERROR_EMPTY_IMAGE);
                continue;
            }

            // Validate file size
            if (image.getSize() > ChallengeConstants.MAX_FILE_SIZE_BYTES) {
                errors.add(fieldPrefix + ChallengeConstants.ERROR_FILE_SIZE);
            }

            // Validate file content type (MIME type)
            String contentType = image.getContentType();
            if (contentType == null || !ChallengeConstants.ALLOWED_CONTENT_TYPES.contains(contentType)) {
                errors.add(fieldPrefix + ChallengeConstants.ERROR_FILE_TYPE);
            }

            // Validate image dimensions
            try {
                BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
                // Check if it's actually a valid image
                if (bufferedImage == null) {
                    errors.add(fieldPrefix + ChallengeConstants.ERROR_INVALID_IMAGE);
                    continue; // Continue to next image
                }

                // Check dimensions
                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();

                if (width < ChallengeConstants.MIN_IMAGE_DIMENSION || height < ChallengeConstants.MIN_IMAGE_DIMENSION) {
                    errors.add(fieldPrefix + ChallengeConstants.ERROR_IMAGE_TOO_SMALL);
                }

                if (width > ChallengeConstants.MAX_IMAGE_DIMENSION || height > ChallengeConstants.MAX_IMAGE_DIMENSION) {
                    errors.add(fieldPrefix + ChallengeConstants.ERROR_IMAGE_TOO_LARGE);
                }
            } catch (IOException e) {
                errors.add(fieldPrefix + String.format(ChallengeConstants.ERROR_PROCESSING_IMAGE, e.getMessage()));
            }

            // Validate filename
            String originalFilename = image.getOriginalFilename();
            if (originalFilename != null && !originalFilename.matches(ChallengeConstants.FILENAME_REGEX_PATTERN)) {
                errors.add(fieldPrefix + ChallengeConstants.ERROR_INVALID_FILENAME);
            }
        }

        return errors;
    }
}
