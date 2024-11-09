package espresso.achievement.cmd.service.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

import espresso.achievement.common.helpers.ApiMessageHelper;
import espresso.achievement.common.response.ApiResult;

@ControllerAdvice
public class CmdApiExceptionHandlers {

    @Autowired
    private ApiMessageHelper apiMsgHelper;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> handleCommandValidationExceptions(
            MethodArgumentNotValidException ex)
            throws JsonProcessingException {

        String message = apiMsgHelper.getMessage("commandValidationFailure", null);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach((error) -> {
                    errors.put(
                            ((FieldError) error).getField(),
                            error.getDefaultMessage());
                });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResult.error(message, errors));
    }
}
