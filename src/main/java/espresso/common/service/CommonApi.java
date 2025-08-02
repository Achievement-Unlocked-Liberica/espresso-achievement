package espresso.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ServiceResponse;
import espresso.common.service.operational.ApiLogger;

public class CommonApi {

    @Operation(summary = "Health Check", description = "Checks the health of the API.")
    @GetMapping(value = "/health", headers = "X-API-Version=1")
    @ApiResponse(responseCode = "200:OK", description = "API is healthy.")
    @ApiLogger("Common API health check")
    public ResponseEntity<String> healthCheckV1() {
        return ResponseEntity.ok("OK V1.0");
    }

    /*
     * This method is used to process the result of the handler
     */
    public ServiceResponse<Object> processHandlerResult(HandlerResponse<Object> result) {
        if (result.isSuccess()) {

            switch (result.getResponseType()) {
                case CREATED:
                    return ServiceResponse.success(HttpStatus.CREATED, result.getData(), result.getCount());
                case SUCCESS:
                    return ServiceResponse.success(HttpStatus.OK, result.getData(), result.getCount());
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

    /*
     * This method is used to process any error that occurs during the handler
     */
    public ServiceResponse<Object> processHandlerError(Exception e) {
        return ServiceResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
}
