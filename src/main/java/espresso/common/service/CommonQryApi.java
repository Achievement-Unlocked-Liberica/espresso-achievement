package espresso.common.service;

import java.util.function.Function;

import org.springframework.http.ResponseEntity;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ServiceResponse;

public class CommonQryApi extends CommonApi {

    /**
     * Generic helper method to handle query processing with consistent error handling
     * This method can be used by all query API classes to eliminate code duplication
     * 
     * @param <T> The type of the query object
     * @param query The query object to be processed
     * @param handler A function that takes the query and returns a HandlerResponse
     * @return ResponseEntity with ServiceResponse containing the result or error
     */
    protected <T> ResponseEntity<ServiceResponse<Object>> executeQuery(
            T query, 
            Function<T, HandlerResponse<Object>> handler) {
        
        ServiceResponse<Object> apiResponse = null;
        try {
            HandlerResponse<Object> handlerResponse = handler.apply(query);
            apiResponse = processHandlerResult(handlerResponse);
            
            return ResponseEntity
                    .status(apiResponse.getHttpStatus())
                    .body(apiResponse);
        } catch (Exception ex) {
            apiResponse = processHandlerError(ex);
        }
        return ResponseEntity
                .status(apiResponse.getHttpStatus())
                .body(apiResponse);
    }
}
