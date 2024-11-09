package espresso.achievement.qry.service.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.fasterxml.jackson.core.JsonProcessingException;

import espresso.achievement.common.helpers.ApiMessageHelper;
import espresso.achievement.common.response.ApiResult;

@ControllerAdvice
public class QryApiExceptionHandlers {

    @Autowired
    private ApiMessageHelper apiMsgHelper;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> handleMissingPathVariableExceptions(
        MissingPathVariableException ex)
            throws JsonProcessingException {

        String message = apiMsgHelper.getMessage("queryValidationFailure", null);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.error(message, null));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> handleHandlerMethodValidationExceptions(
        HandlerMethodValidationException ex)
            throws JsonProcessingException {

                String message = apiMsgHelper.getMessage("queryValidationFailure", null);
        
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResult.error(message, null));
    }
}
