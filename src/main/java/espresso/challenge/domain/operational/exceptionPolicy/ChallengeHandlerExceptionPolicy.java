package espresso.challenge.domain.operational.exceptionPolicy;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import espresso.common.domain.responses.ErrorResponse;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.infrastructure.correlation.CorrelationContext;
import espresso.security.domain.operational.exceptionPolicy.SecurityException;
import espresso.user.domain.operational.exceptionPolicy.UserException;
import lombok.extern.slf4j.Slf4j;

/**
 * Centralized exception policy for the Challenge module that handles mapping
 * of different exception types to appropriate HandlerResponse objects with user-friendly messages.
 * 
 * This policy eliminates code duplication across challenge handlers and provides consistent
 * error handling patterns throughout the challenge module.
 */
@Component
@Slf4j
public class ChallengeHandlerExceptionPolicy {

    /**
     * Maps exceptions to appropriate HandlerResponse with user-friendly messages.
     * 
     * @param ex               The exception to handle
     * @param operationContext Description of the operation being performed (e.g., "create challenge")
     * @return HandlerResponse with appropriate error message and status
     */
    public HandlerResponse<Object> handleException(Exception ex, String operationContext) {
        ErrorResponse errorResponse = mapToErrorResponse(ex, operationContext);
        return HandlerResponse.error(errorResponse.getMessage(), errorResponse.getResponseType());
    }

    /**
     * Maps an exception to a structured ErrorResponse object.
     * 
     * @param ex               The exception to map
     * @param operationContext The operation context
     * @return ErrorResponse with structured error information
     */
    private ErrorResponse mapToErrorResponse(Exception ex, String operationContext) {
        return switch (ex) {
            case ChallengeException challengeException ->
                mapChallengeException(challengeException, operationContext);
            case UserException userException -> 
                mapUserException(userException, operationContext);
            case SecurityException securityException -> 
                mapSecurityException(securityException, operationContext);
            default -> 
                mapSystemException(ex, operationContext);
        };
    }

    /**
     * Maps ChallengeException to ErrorResponse.
     */
    private ErrorResponse mapChallengeException(ChallengeException ex, String operationContext) {
        log.warn("{} failed - Scenario: {}", operationContext, ex.getScenario());
        if (log.isDebugEnabled()) {
            log.debug("Full error details for {}", operationContext, ex);
        }

        return ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .responseType(ResponseType.BAD_REQUEST)
                .operationContext(operationContext)
                .correlationId(ex.getCorrelationId())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    /**
     * Maps UserException to ErrorResponse.
     */
    private ErrorResponse mapUserException(UserException ex, String operationContext) {
        log.warn("User-related error during {} - Scenario: {}", operationContext, ex.getScenario());
        if (log.isDebugEnabled()) {
            log.debug("Full error details for {}", operationContext, ex);
        }

        return ErrorResponse.builder()
                .message(ex.getMessage())
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
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .responseType(ResponseType.FORBIDDEN)
                .operationContext(operationContext)
                .correlationId(ex.getCorrelationId())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    /**
     * Maps system-level exceptions to ErrorResponse.
     */
    private ErrorResponse mapSystemException(Exception ex, String operationContext) {
        log.error("System error during {}", operationContext, ex);

        return ErrorResponse.builder()
                .message("An unexpected error occurred. Please try again later.")
                .errorCode("SYSTEM_ERROR")
                .responseType(ResponseType.INTERNAL_ERROR)
                .operationContext(operationContext)
                .correlationId(CorrelationContext.getCorrelationId())
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
