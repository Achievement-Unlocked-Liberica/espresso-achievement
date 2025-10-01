package espresso.achievement.domain.operational.exceptionPolicy;

import espresso.common.domain.operational.exceptionPolicy.DomainException;

/**
 * Domain exception specific to the Achievement module.
 * Handles all business logic exceptions related to achievement operations
 * including creation, updates, validation, and access control.
 * 
 * Provides factory methods for common achievement-related error scenarios
 * with standardized error codes and localization keys.
 */
public class AchievementException extends DomainException {
    
    /**
     * Creates an achievement exception with full error context.
     *
     * @param messageKey The localization key for the error message
     * @param messageArgs Arguments for parameterized messages
     * @param scenario The specific business scenario that caused the exception
     * @param errorCode A standardized error code for programmatic handling
     */
    protected AchievementException(String messageKey, Object[] messageArgs, String scenario, String errorCode) {
        super(messageKey, messageArgs, scenario, errorCode);
    }
    
    /**
     * Creates an achievement not found exception.
     *
     * @param achievementKey The key of the achievement that was not found
     * @return AchievementException for not found scenario
     */
    public static AchievementException notFound(String achievementKey) {
        return new AchievementException(
            "achievement.not.found",
            new Object[]{achievementKey},
            "ACHIEVEMENT_NOT_FOUND",
            "ACH_001"
        );
    }
    
    /**
     * Creates an achievement creation failure exception.
     *
     * @param reason The specific reason for creation failure
     * @return AchievementException for creation failure scenario
     */
    public static AchievementException creationFailed(String reason) {
        return new AchievementException(
            "achievement.creation.failed",
            new Object[]{reason},
            "ACHIEVEMENT_CREATION_FAILED",
            "ACH_002"
        );
    }
    
    /**
     * Creates an achievement validation failure exception.
     *
     * @param validationErrors The validation errors that occurred
     * @return AchievementException for validation failure scenario
     */
    public static AchievementException validationFailed(Object validationErrors) {
        return new AchievementException(
            "achievement.validation.failed",
            new Object[]{validationErrors},
            "ACHIEVEMENT_VALIDATION_FAILED",
            "ACH_003"
        );
    }
    
    /**
     * Creates an achievement access denied exception.
     *
     * @param userKey The key of the user attempting access
     * @param achievementKey The key of the achievement being accessed
     * @return AchievementException for access denied scenario
     */
    public static AchievementException accessDenied(String userKey, String achievementKey) {
        return new AchievementException(
            "achievement.access.denied",
            new Object[]{userKey, achievementKey},
            "ACHIEVEMENT_ACCESS_DENIED",
            "ACH_004"
        );
    }
    
    /**
     * Creates an achievement update failure exception.
     *
     * @param achievementKey The key of the achievement that failed to update
     * @param reason The specific reason for update failure
     * @return AchievementException for update failure scenario
     */
    public static AchievementException updateFailed(String achievementKey, String reason) {
        return new AchievementException(
            "achievement.update.failed",
            new Object[]{achievementKey, reason},
            "ACHIEVEMENT_UPDATE_FAILED",
            "ACH_005"
        );
    }
    
    /**
     * Creates an achievement media processing exception.
     *
     * @param mediaType The type of media that failed processing
     * @param reason The specific reason for media processing failure
     * @return AchievementException for media processing scenario
     */
    public static AchievementException mediaProcessingFailed(String mediaType, String reason) {
        return new AchievementException(
            "achievement.media.processing.failed",
            new Object[]{mediaType, reason},
            "ACHIEVEMENT_MEDIA_PROCESSING_FAILED",
            "ACH_006"
        );
    }
    
    /**
     * Creates an achievement deletion failure exception.
     *
     * @param achievementKey The key of the achievement that failed to delete
     * @param reason The specific reason for deletion failure
     * @return AchievementException for deletion failure scenario
     */
    public static AchievementException deletionFailed(String achievementKey, String reason) {
        return new AchievementException(
            "achievement.deletion.failed",
            new Object[]{achievementKey, reason},
            "ACHIEVEMENT_DELETION_FAILED",
            "ACH_007"
        );
    }
}