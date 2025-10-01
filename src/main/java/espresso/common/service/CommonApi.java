package espresso.common.service;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import espresso.ApiMessageHelper;
import espresso.common.domain.responses.ErrorResponse;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.operational.ApiLogger;

import java.util.Locale;

/**
 * Base class for all API controllers in the system.
 * Provides common functionality including health checks and standardized response processing.
 * Handles the translation between internal HandlerResponse objects and HTTP ServiceResponse objects.
 */
public class CommonApi {

    private static final String GENERAL_CONTEXT = "general";
    
    private final ApiMessageHelper messageHelper;

    public CommonApi(ApiMessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    /**
     * Health check endpoint to verify API availability.
     * 
     * @return ResponseEntity with OK status and version information
     */
    @Operation(summary = "Health Check", description = "Checks the health of the API.")
    @GetMapping(value = "/health", headers = "X-API-Version=1")
    @ApiResponse(responseCode = "200:OK", description = "API is healthy.")
    @ApiLogger("Common API health check")
    public ResponseEntity<String> healthCheckV1() {
        return ResponseEntity.ok("OK V1.0");
    }

    /**
     * Processes handler results and converts them to appropriate HTTP responses.
     * Maps internal response types to HTTP status codes and formats responses consistently.
     * 
     * @param result The handler response to process
     * @return ServiceResponse with appropriate HTTP status and data
     */
    public ServiceResponse<Object> processHandlerResult(HandlerResponse<Object> result) {
        if (result.isSuccess()) {

            switch (result.getResponseType()) {
                case CREATED:
                    return ServiceResponse.success(HttpStatus.CREATED, result.getData(), result.getCount());
                case SUCCESS:
                    return ServiceResponse.success(HttpStatus.OK, result.getData(), result.getCount());
                case NO_CONTENT:
                    return ServiceResponse.success(HttpStatus.NO_CONTENT, result.getData(), result.getCount());
                default:
                    return ServiceResponse.success(HttpStatus.OK, result.getData(), result.getCount());
            }

        } else {
            // Extract user-friendly message from error response
            String friendlyMessage = extractLocalizedMessage(result.getData(), getCurrentLocale());
            
            switch (result.getResponseType()) {
                case VALIDATION_ERROR:
                    return ServiceResponse.error(HttpStatus.BAD_REQUEST, friendlyMessage);
                case NOT_FOUND:
                    return ServiceResponse.error(HttpStatus.NOT_FOUND, friendlyMessage);
                case UNAUTHORIZED:
                    return ServiceResponse.error(HttpStatus.UNAUTHORIZED, friendlyMessage);
                case FORBIDDEN:
                    return ServiceResponse.error(HttpStatus.FORBIDDEN, friendlyMessage);
                case BAD_REQUEST:
                    return ServiceResponse.error(HttpStatus.BAD_REQUEST, friendlyMessage);
                default:
                    return ServiceResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                        messageHelper.getLocalizedMessage("SYSTEM_ERROR", GENERAL_CONTEXT, getCurrentLocale()));
            }
        }
    }

    /**
     * Processes unexpected exceptions that occur during handler execution.
     * Provides a consistent error response format for unhandled exceptions.
     * 
     * @param e The exception that occurred
     * @return ServiceResponse with internal server error status
     */
    public ServiceResponse<Object> processHandlerError(Exception e) {
        return ServiceResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    /**
     * Get the current locale from Spring's LocaleContextHolder
     */
    private Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * Extract user-friendly localized message from error response
     */
    private String extractLocalizedMessage(Object errorData, Locale locale) {
        if (errorData instanceof ErrorResponse errorResponse) {
            String errorCode = errorResponse.getErrorCode();
            
            // Determine operation context from error code or use general
            String operationContext = determineOperationContext(errorCode);
            
            // Get localized message using the error code
            return messageHelper.getLocalizedMessage(errorCode, operationContext, locale);
        }
        
        // Fallback for non-ErrorResponse data
        return errorData != null ? errorData.toString() : 
            messageHelper.getLocalizedMessage("SYSTEM_ERROR", GENERAL_CONTEXT, locale);
    }

    /**
     * Determine operation context from error code
     */
    private String determineOperationContext(String errorCode) {
        if (errorCode == null) return GENERAL_CONTEXT;
        
        String lowerCode = errorCode.toLowerCase();
        if (lowerCode.contains("achievement")) return "achievement";
        if (lowerCode.contains("user") || lowerCode.contains("register") || lowerCode.contains("login")) return "user";
        if (lowerCode.contains("auth") || lowerCode.contains("token") || lowerCode.contains("credential")) return "security";
        
        return GENERAL_CONTEXT;
    }
}
