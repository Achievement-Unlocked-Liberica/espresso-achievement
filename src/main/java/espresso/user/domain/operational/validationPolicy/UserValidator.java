package espresso.user.domain.operational.validationPolicy;

import espresso.user.domain.entities.User;
import espresso.user.domain.entities.UserProfileImage;
import espresso.user.domain.operational.exceptionPolicy.UserException;

/**
 * Validation utility class for User module entities and operations.
 * Provides static validation methods to encapsulate validation logic and keep infrastructure classes lean.
 * 
 * This validator handles validation for:
 * - User entities
 * - UserProfileImage entities
 * - Common parameters like usernames, emails, entity keys, and paths
 */
public class UserValidator {

    /**
     * Validates a User entity for persistence operations.
     * Checks that the user is not null.
     *
     * @param user The user to validate
     * @throws UserException if validation fails
     */
    public static void validateUser(User user) {
        if (user == null) {
            throw UserException.validationFailed("User cannot be null");
        }
    }

    /**
     * Validates a User entity for update operations.
     * Checks that the user is not null and has valid required fields.
     *
     * @param user The user to validate
     * @throws UserException if validation fails
     */
    public static void validateUserForUpdate(User user) {
        if (user == null) {
            throw UserException.validationFailed("User model cannot be null");
        }
        
        if (user.getEntityKey() == null || user.getEntityKey().trim().isEmpty()) {
            throw UserException.validationFailed("User entity key cannot be null or empty");
        }
    }

    /**
     * Validates a username parameter.
     * Checks that the username is not null or empty.
     *
     * @param username The username to validate
     * @throws UserException if validation fails
     */
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw UserException.validationFailed("Username cannot be null or empty");
        }
    }

    /**
     * Validates an email parameter.
     * Checks that the email is not null or empty.
     *
     * @param email The email to validate
     * @throws UserException if validation fails
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw UserException.validationFailed("Email cannot be null or empty");
        }
    }

    /**
     * Validates an entity key parameter.
     * Checks that the entity key is not null or empty.
     *
     * @param entityKey The entity key to validate
     * @throws UserException if validation fails
     */
    public static void validateEntityKey(String entityKey) {
        if (entityKey == null || entityKey.trim().isEmpty()) {
            throw UserException.validationFailed("Entity key cannot be null or empty");
        }
    }

    /**
     * Validates that a query result is not null.
     * Throws not found exception if the result is null.
     *
     * @param result The query result to validate
     * @param searchKey The key that was searched for (for error context)
     * @throws UserException if result is null
     */
    public static void validateQueryResult(Object result, String searchKey) {
        if (result == null) {
            throw UserException.notFound(searchKey);
        }
    }

    /**
     * Validates a UserProfileImage entity for persistence operations.
     * Checks that the profile image is not null.
     *
     * @param userProfileImage The user profile image to validate
     * @throws UserException if validation fails
     */
    public static void validateUserProfileImage(UserProfileImage userProfileImage) {
        if (userProfileImage == null) {
            throw UserException.validationFailed("User profile image cannot be null");
        }
    }

    /**
     * Validates UserProfileImage for S3 upload operations.
     * Checks profile image entity, user entity key, image data, and base path.
     *
     * @param userProfileImage The user profile image to validate
     * @throws UserException if validation fails
     */
    public static void validateUserProfileImageForUpload(UserProfileImage userProfileImage) {
        if (userProfileImage == null) {
            throw UserException.validationFailed("User profile image cannot be null");
        }
        
        if (userProfileImage.getUser() == null || userProfileImage.getUser().getEntityKey() == null) {
            throw UserException.validationFailed("User entity key cannot be null");
        }
        
        if (userProfileImage.getImageData() == null || userProfileImage.getImageData().length == 0) {
            throw UserException.validationFailed("Image data cannot be null or empty");
        }
    }

    /**
     * Validates a base path parameter for S3 operations.
     * Checks that the path is not null or empty.
     *
     * @param basePath The base path to validate
     * @throws UserException if validation fails
     */
    public static void validateBasePath(String basePath) {
        if (basePath == null || basePath.trim().isEmpty()) {
            throw UserException.validationFailed("Base path cannot be null or empty");
        }
    }

    /**
     * Validates a configuration directory parameter.
     * Checks that the directory is not null or empty.
     *
     * @param directory The directory path to validate
     * @param configName The name of the configuration (for error messages)
     * @throws UserException if validation fails
     */
    public static void validateConfigurationDirectory(String directory, String configName) {
        if (directory == null || directory.trim().isEmpty()) {
            throw UserException.profileUpdateFailed("unknown", configName + " configuration is missing");
        }
    }
}