package espresso.common.service;

import java.util.function.Function;

import org.springframework.http.ResponseEntity;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ServiceResponse;

public class CommonCmdApi extends CommonApi {

    /**
     * Generic helper method to handle command processing with consistent error handling
     * This method can be used by all command API classes to eliminate code duplication
     * 
     * @param <T> The type of the command object
     * @param command The command object to be processed
     * @param handler A function that takes the command and returns a HandlerResponse
     * @return ResponseEntity with ServiceResponse containing the result or error
     */
    protected <T> ResponseEntity<ServiceResponse<Object>> executeCommand(
            T command, 
            Function<T, HandlerResponse<Object>> handler) {
        
        ServiceResponse<Object> apiResponse = null;
        try {
            HandlerResponse<Object> handlerResponse = handler.apply(command);
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
