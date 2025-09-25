package espresso.security.domain.operational.exceptionPolicy;

import espresso.common.domain.operational.exceptionPolicy.DomainException;

/**
 * Domain exception specific to the Security module.
 * Handles all business logic exceptions related to security operations
 * including authentication, authorization, JWT token management, and user credentials.
 * 
 * Provides factory methods for common security-related error scenarios
 * with standardized error codes and localization keys.
 */
public class SecurityException extends DomainException {
    
    /**
     * Creates a security exception with full error context.
     *
     * @param messageKey The localization key for the error message
     * @param messageArgs Arguments for parameterized messages
     * @param scenario The specific business scenario that caused the exception
     * @param errorCode A standardized error code for programmatic handling
     */
    protected SecurityException(String messageKey, Object[] messageArgs, String scenario, String errorCode) {
        super(messageKey, messageArgs, scenario, errorCode);
    }
    
    /**
     * Creates an authentication failed exception.
     *
     * @param username The username that failed authentication
     * @return SecurityException for authentication failure scenario
     */
    public static SecurityException authenticationFailed(String username) {
        return new SecurityException(
            "security.authentication.failed",
            new Object[]{username},
            "AUTHENTICATION_FAILED",
            "SEC_001"
        );
    }
    
    /**
     * Creates an invalid credentials exception.
     *
     * @return SecurityException for invalid credentials scenario
     */
    public static SecurityException invalidCredentials() {
        return new SecurityException(
            "security.invalid.credentials",
            null,
            "INVALID_CREDENTIALS",
            "SEC_002"
        );
    }
    
    /**
     * Creates a JWT token invalid exception.
     *
     * @param reason The specific reason why the token is invalid
     * @return SecurityException for invalid JWT token scenario
     */
    public static SecurityException invalidToken(String reason) {
        return new SecurityException(
            "security.token.invalid",
            new Object[]{reason},
            "INVALID_TOKEN",
            "SEC_003"
        );
    }
    
    /**
     * Creates a JWT token expired exception.
     *
     * @return SecurityException for expired JWT token scenario
     */
    public static SecurityException tokenExpired() {
        return new SecurityException(
            "security.token.expired",
            null,
            "TOKEN_EXPIRED",
            "SEC_004"
        );
    }
    
    /**
     * Creates an access denied exception.
     *
     * @param resource The resource that access was denied to
     * @param userKey The key of the user who was denied access
     * @return SecurityException for access denied scenario
     */
    public static SecurityException accessDenied(String resource, String userKey) {
        return new SecurityException(
            "security.access.denied",
            new Object[]{resource, userKey},
            "ACCESS_DENIED",
            "SEC_005"
        );
    }
    
    /**
     * Creates an insufficient privileges exception.
     *
     * @param requiredRole The role required for the operation
     * @param userRole The actual role of the user
     * @return SecurityException for insufficient privileges scenario
     */
    public static SecurityException insufficientPrivileges(String requiredRole, String userRole) {
        return new SecurityException(
            "security.insufficient.privileges",
            new Object[]{requiredRole, userRole},
            "INSUFFICIENT_PRIVILEGES",
            "SEC_006"
        );
    }
    
    /**
     * Creates a user registration failure exception.
     *
     * @param reason The specific reason for registration failure
     * @return SecurityException for registration failure scenario
     */
    public static SecurityException registrationFailed(String reason) {
        return new SecurityException(
            "security.registration.failed",
            new Object[]{reason},
            "REGISTRATION_FAILED",
            "SEC_007"
        );
    }
    
    /**
     * Creates a password validation failure exception.
     *
     * @param requirements The password requirements that were not met
     * @return SecurityException for password validation failure scenario
     */
    public static SecurityException passwordValidationFailed(String requirements) {
        return new SecurityException(
            "security.password.validation.failed",
            new Object[]{requirements},
            "PASSWORD_VALIDATION_FAILED",
            "SEC_008"
        );
    }
    
    /**
     * Creates a validation failure exception.
     *
     * @param message The validation error message
     * @return SecurityException for validation failure scenario
     */
    public static SecurityException validationFailed(String message) {
        return new SecurityException(
            "security.validation.failed",
            new Object[]{message},
            "VALIDATION_FAILED",
            "SEC_009"
        );
    }
    
    /**
     * Creates an integration failure exception.
     *
     * @param message The integration error message
     * @return SecurityException for integration failure scenario
     */
    public static SecurityException integrationFailed(String message) {
        return new SecurityException(
            "security.integration.failed",
            new Object[]{message},
            "INTEGRATION_FAILED",
            "SEC_010"
        );
    }
}