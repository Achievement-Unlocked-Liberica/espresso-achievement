package espresso.common.domain.commands;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public abstract class CommonCommand {

    //TODO: figure out a way to creeate the validator at runtime and then inject it into the command handler
    protected final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    protected final Validator validator = factory.getValidator();

    /*
     * Validates the given object and returns a set of error messages if any.
     */
    public Set<String> validate() {

        // Validate the object
        Set<ConstraintViolation<CommonCommand>> violations = validator.validate(this);

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
