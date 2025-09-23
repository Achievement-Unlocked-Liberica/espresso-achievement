package espresso.achievement.domain.constants;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Constants related to achievements and media handling.
 * This class centralizes all achievement-related constants for use across all layers of the achievement module.
 */
public final class AchievementConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AchievementConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Set of allowed skill abbreviations.
     * These represent the valid skill types that can be associated with achievements.
     */
    public static final Set<String> ALLOWED_SKILLS = Set.of("str", "dex", "con", "wis", "int", "cha", "luc");

    /**
     * Error message template for invalid skill validation.
     * Use with String.format to include the invalid skill value.
     */
    public static final String ERROR_INVALID_SKILL = "LOCALIZE: INVALID SKILL '%s'. ALLOWED SKILLS ARE: str, dex, con, wis, int, cha, luc";

    /**
     * List of allowed MIME content types for achievement media uploads.
     * Supports common image formats used for achievement media.
     */
    public static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp");

    /**
     * Error message for when no images are provided in an upload request.
     */
    public static final String ERROR_EMPTY_IMAGE = "LOCALIZE: ACHIEVEMENT MEDIA CANNOT BE EMPTY";
    
    /**
     * Error message for when uploaded files exceed the maximum size limit.
     */
    public static final String ERROR_FILE_SIZE = "LOCALIZE: ACHIEVEMENT MEDIA EXCEEDS MAXIMUM SIZE OF 10MB";
    
    /**
     * Error message for when uploaded files have invalid content types.
     */
    public static final String ERROR_FILE_TYPE = "LOCALIZE: INVALID FILE TYPE. ONLY JPEG, PNG, GIF, AND WEBP FORMATS ARE ALLOWED";
    
    /**
     * Error message for when uploaded files cannot be processed as valid images.
     */
    public static final String ERROR_INVALID_IMAGE = "LOCALIZE: INVALID IMAGE FILE";
    
    /**
     * Error message for when image dimensions are below the minimum required size.
     */
    public static final String ERROR_IMAGE_TOO_SMALL = "LOCALIZE: IMAGE DIMENSIONS TOO SMALL. MINIMUM SIZE IS 200X200 PIXELS";
    
    /**
     * Error message for when image dimensions exceed the maximum allowed size.
     */
    public static final String ERROR_IMAGE_TOO_LARGE = "LOCALIZE: IMAGE DIMENSIONS TOO LARGE. MAXIMUM SIZE IS 3000X3000 PIXELS";
    
    /**
     * Error message for when filenames contain invalid characters.
     */
    public static final String ERROR_INVALID_FILENAME = "LOCALIZE: INVALID FILENAME. ONLY ALPHANUMERIC CHARACTERS, DOTS, HYPHENS, AND UNDERSCORES ARE ALLOWED";
    
    /**
     * Error message template for image processing failures.
     */
    public static final String ERROR_PROCESSING_IMAGE = "LOCALIZE: FAILED TO PROCESS IMAGE: %s";
    
    /**
     * Regular expression pattern for valid filenames.
     * Allows alphanumeric characters, dots, hyphens, and underscores.
     */
    public static final String FILENAME_REGEX_PATTERN = "^[a-zA-Z0-9._-]+$";

    /**
     * Maximum allowed file size for achievement media uploads (10MB in bytes).
     */
    public static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB for achievement media

    /**
     * Minimum required image dimension in pixels for achievement media.
     * Both width and height must be at least this size.
     */
    public static final int MIN_IMAGE_DIMENSION = 200; // pixels - larger minimum for achievement media
    
    /**
     * Maximum allowed image dimension in pixels for achievement media.
     * Both width and height must not exceed this size.
     */
    public static final int MAX_IMAGE_DIMENSION = 3000; // pixels - larger maximum for achievement media
}
