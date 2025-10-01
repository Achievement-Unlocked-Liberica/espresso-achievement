package espresso.user.domain.operational.exceptionPolicy;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import espresso.achievement.domain.operational.exceptionPolicy.AchievementException;
import espresso.common.domain.responses.ErrorResponse;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.infrastructure.correlation.CorrelationContext;
import espresso.security.domain.operational.exceptionPolicy.SecurityException;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralized exception policy for the User module that handles mapping of different exception types
 * to appropriate HandlerResponse objects with user-friendly messages.
 * 
 * This policy eliminates code duplication across user handlers and provides consistent
 * error handling patterns throughout the user module.
 */
@Component
@Slf4j
public class UserHandlerExceptionPolicy {
    
    /**
     * Maps exceptions to appropriate HandlerResponse with user-friendly messages.
     * 
     * @param ex The exception to handle
     * @param operationContext Description of the operation being performed (e.g., "register user")
     * @return HandlerResponse with appropriate error message and status
     */
    public HandlerResponse<Object> handleException(Exception ex, String operationContext) {
        ErrorResponse errorResponse = mapToErrorResponse(ex, operationContext);
        return HandlerResponse.error(errorResponse.getMessage(), errorResponse.getResponseType());
    }
    
    /**
     * Maps an exception to a structured ErrorResponse object.
     * 
     * @param ex The exception to map
     * @param operationContext The operation context
     * @return ErrorResponse with structured error information
     */
    private ErrorResponse mapToErrorResponse(Exception ex, String operationContext) {
        return switch (ex) {
          case UserException userException -> 
            mapUserException(userException, operationContext);
          case AchievementException achievementException -> 
            mapAchievementException(achievementException, operationContext);
          case SecurityException securityException -> 
            mapSecurityException(securityException, operationContext);
          default -> 
            mapSystemException(ex, operationContext);
        };
    }
    
    /**
     * Maps UserException to ErrorResponse.
     */
    private ErrorResponse mapUserException(UserException ex, String operationContext) {
        log.warn("{} failed - Scenario: {}", operationContext, ex.getScenario());
        if (log.isDebugEnabled()) {
            log.debug("Full error details for {}", operationContext, ex);
        }
        
        return ErrorResponse.builder()
            .message(ex.getMessage()) // Preserve original technical message
            .errorCode(ex.getErrorCode())
            .responseType(ResponseType.BAD_REQUEST)
            .operationContext(operationContext)
            .correlationId(ex.getCorrelationId())
            .timestamp(OffsetDateTime.now())
            .build();
    }
    
    /**
     * Maps AchievementException to ErrorResponse.
     */
    private ErrorResponse mapAchievementException(AchievementException ex, String operationContext) {
        log.warn("Achievement-related error during {} - Scenario: {}", operationContext, ex.getScenario());
        if (log.isDebugEnabled()) {
            log.debug("Full error details for {}", operationContext, ex);
        }
        
        return ErrorResponse.builder()
            .message(ex.getMessage()) // Preserve original technical message
            .errorCode(ex.getErrorCode())
            .responseType(ResponseType.BAD_REQUEST)
            .operationContext(operationContext)
            .correlationId(ex.getCorrelationId())
            .timestamp(OffsetDateTime.now())
            .build();
    }
    
    /**
     * Maps SecurityException to ErrorResponse.
     */
    private ErrorResponse mapSecurityException(SecurityException ex, String operationContext) {
        log.warn("Security error during {} - Scenario: {}", operationContext, ex.getScenario());
        if (log.isDebugEnabled()) {
            log.debug("Full error details for {}", operationContext, ex);
        }
        
        return ErrorResponse.builder()
            .message(ex.getMessage()) // Preserve original technical message
            .errorCode(ex.getErrorCode())
            .responseType(ResponseType.FORBIDDEN)
            .operationContext(operationContext)
            .correlationId(ex.getCorrelationId())
            .timestamp(OffsetDateTime.now())
            .build();
    }
    
    /**
     * Maps system exceptions to ErrorResponse.
     */
    private ErrorResponse mapSystemException(Exception ex, String operationContext) {
        log.error("Unexpected error during {}: {}", operationContext, ex.getClass().getSimpleName());
        if (log.isDebugEnabled()) {
            log.debug("Full error details for {}", operationContext, ex);
        }
        
        return ErrorResponse.builder()
            .message(ex.getMessage()) // Preserve original technical message
            .errorCode("SYSTEM_ERROR")
            .responseType(ResponseType.INTERNAL_ERROR)
            .operationContext(operationContext)
            .correlationId(CorrelationContext.getCorrelationId()) // Get from context for system exceptions
            .timestamp(OffsetDateTime.now())
            .build();
    }
}