package espresso;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ApiMessageHelper {

    private final MessageSource messageSource;

    /**
     * Constructor for dependency injection.
     * 
     * @param messageSource Spring MessageSource for internationalization and localization
     */
    public ApiMessageHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Gets a localized message for an error code within a specific operation context.
     * Tries module-specific messages first, then falls back to generic error codes.
     * 
     * @param errorCode The error code from the exception
     * @param operationContext The operation context (e.g., "create achievement")
     * @param locale The locale for localization
     * @return Localized error message
     */
    public String getLocalizedMessage(String errorCode, String operationContext, Locale locale) {
        try {
            // Try specific error code first: "error.achievement.title_required"
            String messageKey = String.format("error.%s.%s", 
                getModuleFromContext(operationContext), 
                errorCode.toLowerCase());
            return messageSource.getMessage(messageKey, null, locale);
        } catch (NoSuchMessageException e) {
            try {
                // Fallback to generic error code: "error.title_required"
                String fallbackKey = String.format("error.%s", errorCode.toLowerCase());
                return messageSource.getMessage(fallbackKey, null, locale);
            } catch (NoSuchMessageException e2) {
                // Final fallback
                return messageSource.getMessage("error.unknown", null, 
                    "An unexpected error occurred", locale);
            }
        }
    }

    /**
     * Gets a localized message using the default locale.
     * 
     * @param errorCode The error code from the exception
     * @param operationContext The operation context
     * @return Localized error message
     */
    public String getLocalizedMessage(String errorCode, String operationContext) {
        return getLocalizedMessage(errorCode, operationContext, Locale.getDefault());
    }

    /**
     * Legacy method for backward compatibility.
     * 
     * @param key The message key
     * @param args Arguments for the message
     * @return The localized message
     */
    public String getMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, Locale.getDefault());
    }

    /**
     * Determines the module name from the operation context.
     * 
     * @param operationContext The operation context string
     * @return The module name (achievement, user, security, or general)
     */
    private String getModuleFromContext(String operationContext) {
        if (operationContext.contains("achievement")) return "achievement";
        if (operationContext.contains("user")) return "user";
        if (operationContext.contains("security")) return "security";
        return "general";
    }
}