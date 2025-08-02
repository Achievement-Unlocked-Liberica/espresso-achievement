package espresso.common.service.operational;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Component
public class RequestValidator {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    /*
     * Validates the given object and returns a set of error messages if any.
    */
    public <T> Set<String> validate(T objectToValidate) {

        // Validate the object
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);

        // If there are violations, return a set of error messages
        if (!violations.isEmpty()) {            
            return violations
                    .stream()
                    .map(error -> "%s:%s".formatted(error.getPropertyPath(), error.getMessage()))
                    .collect(Collectors.toSet());
        }
        
        return Collections.emptySet();
    }
}
