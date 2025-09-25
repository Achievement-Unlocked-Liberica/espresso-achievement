package espresso.user.domain.operational.exceptionPolicy;

import espresso.common.domain.operational.exceptionPolicy.DomainException;

/**
 * Domain exception specific to the User module.
 * Handles all business logic exceptions related to user operations
 * including registration, profile management, authentication, and access control.
 * 
 * Provides factory methods for common user-related error scenarios
 * with standardized error codes and localization keys.
 */
public class UserException extends DomainException {
    
    /**
     * Creates a user exception with full error context.
     *
     * @param messageKey The localization key for the error message
     * @param messageArgs Arguments for parameterized messages
     * @param scenario The specific business scenario that caused the exception
     * @param errorCode A standardized error code for programmatic handling
     */
    protected UserException(String messageKey, Object[] messageArgs, String scenario, String errorCode) {
        super(messageKey, messageArgs, scenario, errorCode);
    }
    
    /**
     * Creates a user not found exception.
     *
     * @param identifier The user identifier (key, username, email) that was not found
     * @return UserException for not found scenario
     */
    public static UserException notFound(String identifier) {
        return new UserException(
            "user.not.found",
            new Object[]{identifier},
            "USER_NOT_FOUND",
            "USER_001"
        );
    }
    
    /**
     * Creates a user already exists exception.
     *
     * @param field The field that already exists (username, email)
     * @param value The value that already exists
     * @return UserException for already exists scenario
     */
    public static UserException alreadyExists(String field, String value) {
        return new UserException(
            "user.already.exists",
            new Object[]{field, value},
            "USER_ALREADY_EXISTS",
            "USER_002"
        );
    }
    
    /**
     * Creates a user account inactive exception.
     *
     * @param userKey The key of the inactive user account
     * @return UserException for inactive account scenario
     */
    public static UserException accountInactive(String userKey) {
        return new UserException(
            "user.account.inactive",
            new Object[]{userKey},
            "USER_ACCOUNT_INACTIVE",
            "USER_003"
        );
    }
    
    /**
     * Creates a user validation failure exception.
     *
     * @param validationErrors The validation errors that occurred
     * @return UserException for validation failure scenario
     */
    public static UserException validationFailed(Object validationErrors) {
        return new UserException(
            "user.validation.failed",
            new Object[]{validationErrors},
            "USER_VALIDATION_FAILED",
            "USER_004"
        );
    }
    
    /**
     * Creates a user profile update failure exception.
     *
     * @param userKey The key of the user whose profile failed to update
     * @param reason The specific reason for update failure
     * @return UserException for profile update failure scenario
     */
    public static UserException profileUpdateFailed(String userKey, String reason) {
        return new UserException(
            "user.profile.update.failed",
            new Object[]{userKey, reason},
            "USER_PROFILE_UPDATE_FAILED",
            "USER_005"
        );
    }
    
    /**
     * Creates a user profile picture processing exception.
     *
     * @param userKey The key of the user whose profile picture failed processing
     * @param reason The specific reason for processing failure
     * @return UserException for profile picture processing scenario
     */
    public static UserException profilePictureProcessingFailed(String userKey, String reason) {
        return new UserException(
            "user.profile.picture.processing.failed",
            new Object[]{userKey, reason},
            "USER_PROFILE_PICTURE_PROCESSING_FAILED",
            "USER_006"
        );
    }
    
    /**
     * Creates a user password validation exception.
     *
     * @param requirements The password requirements that were not met
     * @return UserException for password validation scenario
     */
    public static UserException passwordValidationFailed(String requirements) {
        return new UserException(
            "user.password.validation.failed",
            new Object[]{requirements},
            "USER_PASSWORD_VALIDATION_FAILED",
            "USER_007"
        );
    }
}