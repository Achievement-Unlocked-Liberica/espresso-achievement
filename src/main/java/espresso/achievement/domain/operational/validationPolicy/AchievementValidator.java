package espresso.achievement.domain.operational.validationPolicy;

import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;
import espresso.achievement.domain.operational.exceptionPolicy.AchievementException;
 

/**
 * Validation utility class for Achievement module entities and operations.
 * Provides static validation methods to encapsulate validation logic and keep infrastructure classes lean.
 * 
 * This validator handles validation for:
 * - Achievement entities
 * - AchievementMedia entities
 * - User entities (in context of achievements)
 * - Common parameters like keys, DTOs, and configuration values
 */
public class AchievementValidator {

    /**
     * Validates an Achievement entity for persistence operations (save/create).
     * Checks that the achievement is not null.
     *
     * @param achievement The achievement to validate
     * @throws AchievementException if validation fails
     */
    public static void validateForPersistence(Achievement achievement) {
        if (achievement == null) {
            throw AchievementException.validationFailed("Achievement cannot be null");
        }
    }

    /**
     * Validates an Achievement entity for update operations.
     * Checks that the achievement is not null and has a valid entity key.
     *
     * @param achievement The achievement to validate
     * @throws AchievementException if validation fails
     */
    public static void validateForUpdate(Achievement achievement) {
        if (achievement == null) {
            throw AchievementException.validationFailed("Achievement cannot be null");
        }
        
        if (achievement.getEntityKey() == null || achievement.getEntityKey().trim().isEmpty()) {
            throw AchievementException.validationFailed("Achievement entity key cannot be null or empty");
        }
    }

    /**
     * Validates an Achievement entity for deletion operations.
     * Checks that the achievement is not null and has a valid entity key.
     *
     * @param achievement The achievement to validate
     * @throws AchievementException if validation fails
     */
    public static void validateForDeletion(Achievement achievement) {
        if (achievement == null) {
            throw AchievementException.validationFailed("Achievement cannot be null");
        }
        
        if (achievement.getEntityKey() == null || achievement.getEntityKey().trim().isEmpty()) {
            throw AchievementException.validationFailed("Achievement entity key cannot be null or empty");
        }
    }

    /**
     * Validates an AchievementMedia entity for persistence operations.
     * Checks that the media and its associated user are not null.
     *
     * @param achievementMedia The achievement media to validate
     * @throws AchievementException if validation fails
     */
    public static void validateAchievementMedia(AchievementMedia achievementMedia) {
        if (achievementMedia == null) {
            throw AchievementException.validationFailed("Achievement media cannot be null");
        }
        
        Achievement achievement = achievementMedia.getAchievement();
        if (achievement == null) {
            throw AchievementException.validationFailed("Achievement in achievement media cannot be null");
        }
    }

    /**
     * Validates AchievementMedia for S3 upload operations.
     * Checks media entity, user entity key, and image data.
     *
     * @param achievementMedia The achievement media to validate
     * @throws AchievementException if validation fails
     */
    public static void validateAchievementMediaForUpload(AchievementMedia achievementMedia) {
        if (achievementMedia == null) {
            throw AchievementException.validationFailed("Achievement media cannot be null");
        }
        
        if (achievementMedia.getImageData() == null || achievementMedia.getImageData().length == 0) {
            throw AchievementException.validationFailed("Achievement media image data cannot be null or empty");
        }
    }

    /**
     * Validates an entity key parameter.
     * Checks that the key is not null or empty.
     *
     * @param entityKey The entity key to validate
     * @throws AchievementException if validation fails
     */
    public static void validateEntityKey(String entityKey) {
        if (entityKey == null || entityKey.trim().isEmpty()) {
            throw AchievementException.validationFailed("Achievement key cannot be null or empty");
        }
    }

    /**
     * Validates a DTO type parameter for query operations.
     * Checks that the type is not null.
     *
     * @param dtoType The DTO type to validate
     * @throws AchievementException if validation fails
     */
    public static void validateDtoType(Class<?> dtoType) {
        if (dtoType == null) {
            throw AchievementException.validationFailed("DTO type cannot be null");
        }
    }

    /**
     * Validates and normalizes a limit parameter for query operations.
     * Returns a default limit if the provided limit is null or invalid.
     *
     * @param limit The limit to validate
     * @return Normalized limit value (default: 10)
     */
    public static Integer validateAndNormalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 10; // Default limit
        }
        return limit;
    }

    /**
     * Validates configuration values (like endpoints, API keys, directories).
     * Checks that the value is not null or empty.
     *
     * @param configValue The configuration value to validate
     * @param configName The name of the configuration (for error messages)
     * @throws AchievementException if validation fails
     */
    public static void validateConfigurationValue(String configValue, String configName) {
        if (configValue == null || configValue.trim().isEmpty()) {
            throw AchievementException.validationFailed(configName + " cannot be null or empty");
        }
    }

    /**
     * Validates text content for safety verification.
     * Checks that the text is not null or empty.
     *
     * @param text The text content to validate
     * @throws AchievementException if validation fails
     */
    public static void validateTextContent(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw AchievementException.validationFailed("Text content cannot be null or empty for safety verification");
        }
    }

    /**
     * Validates image data for safety verification.
     * Checks that the image data is not null or empty.
     *
     * @param imageData The image data to validate
     * @throws AchievementException if validation fails
     */
    public static void validateImageData(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            throw AchievementException.validationFailed("Image data cannot be null or empty for safety verification");
        }
    }
}