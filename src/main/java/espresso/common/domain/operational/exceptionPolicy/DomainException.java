package espresso.common.domain.operational.exceptionPolicy;

import lombok.Getter;

/**
 * Base domain exception class for all business logic exceptions in the application.
 * Provides a consistent structure for domain-specific errors with support for
 * localization, error codes, and scenario identification.
 * 
 * All domain exceptions should extend this class to ensure uniform error handling
 * across different modules and layers of the application.
 */
@Getter
public abstract class DomainException extends RuntimeException {
    
    /**
     * The localization key for retrieving user-friendly error messages.
     * Used by the global exception handler to look up appropriate messages
     * in the message source based on the current locale.
     */
    private final String messageKey;
    
    /**
     * Arguments to be used with the message key for parameterized messages.
     * These values will be substituted into the localized message template.
     */
    private final Object[] messageArgs;
    
    /**
     * Identifies the specific scenario or business rule that was violated.
     * Provides additional context about the nature of the exception for
     * logging, monitoring, and debugging purposes.
     */
    private final String scenario;
    
    /**
     * A standardized error code that can be used by clients for programmatic
     * error handling. Should follow a consistent format across the application.
     * Example formats: "ACH_001", "USER_002", "SEC_003"
     */
    private final String errorCode;
    
    /**
     * Constructor for creating domain exceptions with full error context.
     *
     * @param messageKey The localization key for the error message
     * @param messageArgs Arguments for parameterized messages
     * @param scenario The specific business scenario that caused the exception
     * @param errorCode A standardized error code for programmatic handling
     */
    protected DomainException(String messageKey, Object[] messageArgs, String scenario, String errorCode) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs != null ? messageArgs : new Object[0];
        this.scenario = scenario;
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor for creating domain exceptions with message and scenario.
     *
     * @param messageKey The localization key for the error message
     * @param scenario The specific business scenario that caused the exception
     * @param errorCode A standardized error code for programmatic handling
     */
    protected DomainException(String messageKey, String scenario, String errorCode) {
        this(messageKey, null, scenario, errorCode);
    }
}