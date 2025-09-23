package espresso.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.operational.ApiLogger;

/**
 * Base class for all API controllers in the system.
 * Provides common functionality including health checks and standardized response processing.
 * Handles the translation between internal HandlerResponse objects and HTTP ServiceResponse objects.
 */
public class CommonApi {

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
            switch (result.getResponseType()) {
                case VALIDATION_ERROR:
                    return ServiceResponse.error(HttpStatus.BAD_REQUEST, result.getData());
                case NOT_FOUND:
                    return ServiceResponse.error(HttpStatus.NOT_FOUND, result.getData());
                case UNAUTHORIZED:
                    return ServiceResponse.error(HttpStatus.UNAUTHORIZED, result.getData());
                case FORBIDDEN:
                    return ServiceResponse.error(HttpStatus.FORBIDDEN, result.getData());
                default:
                    return ServiceResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, result.getData());
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
}
