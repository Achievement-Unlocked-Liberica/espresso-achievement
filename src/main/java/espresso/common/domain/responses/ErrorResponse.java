package espresso.common.domain.responses;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a structured error response with detailed information about the error.
 * Used by the HandlerExceptionPolicy to provide consistent error handling across handlers.
 */
@Data
@Builder
public class ErrorResponse {
    
    /**
     * User-friendly error message that describes what went wrong.
     */
    private String message;
    
    /**
     * Technical error code for system identification and logging.
     */
    private String errorCode;
    
    /**
     * The HTTP response type to be returned (BAD_REQUEST, INTERNAL_ERROR, etc.).
     */
    private ResponseType responseType;
    
    /**
     * Context of the operation that was being performed when the error occurred.
     */
    private String operationContext;
    
    /**
     * Timestamp when the error occurred.
     */
    private OffsetDateTime timestamp;
}