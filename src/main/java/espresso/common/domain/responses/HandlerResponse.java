package espresso.common.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Standard response wrapper for command and query handlers.
 * Provides a consistent response format across all handler operations,
 * including success/failure status, data payload, response type, and optional count information.
 * Used throughout the CQRS architecture to maintain uniform response handling.
 */
@Data
@Builder
@AllArgsConstructor
public class HandlerResponse<T> {

    /**
     * Indicates whether the operation completed successfully.
     */
    private boolean success;
    
    /**
     * Optional count of items in the response data (for collections).
     * Automatically populated for List responses.
     */
    @Builder.Default
    private Integer count = null;
    
    /**
     * The response data payload of type T.
     * Can be null for operations that don't return data.
     */
    private T data;
    
    /**
     * The type of response indicating the operation outcome.
     */
    private ResponseType responseType;

    /**
     * Creates an empty successful response with no data.
     * 
     * @param <T> The type of response data
     * @return HandlerResponse with no data and NONE response type
     */
    public static <T> HandlerResponse<T> empty() {
        return HandlerResponse.<T>builder()
                .data(null)
                .responseType(ResponseType.NONE)
                .success(true)
                .build();
    }

    /**
     * Creates a successful response indicating no content.
     * 
     * @param <T> The type of response data
     * @return HandlerResponse with no data and NO_CONTENT response type
     */
    public static <T> HandlerResponse<T> noContent() {
        return HandlerResponse.<T>builder()
                .data(null)
                .responseType(ResponseType.NO_CONTENT)
                .success(true)
                .build();
    }

    /**
     * Creates a successful response for resource creation operations.
     * Automatically populates count for List data.
     * 
     * @param <T> The type of response data
     * @param data The created resource data
     * @return HandlerResponse with CREATED response type
     */
    public static <T> HandlerResponse<T> created(T data) {

        Integer count = null;
        if (data instanceof java.util.List<?>) {
            count = ((java.util.List<?>) data).size();
        }

        return HandlerResponse.<T>builder()
                .data(data)
                .count(count)
                .responseType(ResponseType.CREATED)
                .success(true)
                .build();
    }

    /**
     * Creates a successful response for general operations.
     * Automatically populates count for List data.
     * 
     * @param <T> The type of response data
     * @param data The response data
     * @return HandlerResponse with SUCCESS response type
     */
    public static <T> HandlerResponse<T> success(T data) {

        Integer count = null;

        if (data instanceof java.util.List<?>) {
            count = ((java.util.List<?>) data).size();
        }

        return HandlerResponse.<T>builder()
                .data(data)
                .count(count)
                .responseType(ResponseType.SUCCESS)
                .success(true)
                .build();

    }

    /**
     * Creates an error response with the specified data and response type.
     * 
     * @param <T> The type of response data
     * @param data The error data (usually error messages)
     * @param responseType The type of error that occurred
     * @return HandlerResponse with success=false and specified error type
     */
    public static <T> HandlerResponse<T> error(T data, ResponseType responseType) {
        return HandlerResponse.<T>builder()
                .data(data)
                .responseType(responseType)
                .success(false)
                .build();
    }

    /**
     * Checks if the response contains data.
     * 
     * @return true if data is not null, false otherwise
     */
    public boolean HasData() {
        return data != null;
    }
}
