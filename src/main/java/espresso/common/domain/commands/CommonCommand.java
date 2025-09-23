package espresso.common.domain.commands;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.Setter;

/**
 * Base class for all command objects in the CQRS architecture.
 * Commands represent write operations and contain all necessary data to perform an action.
 * Provides common validation infrastructure using JSR-303 Bean Validation and custom validation hooks.
 * Commands are simple DTOs - validation logic is executed by command handlers using Spring's Validator.
 */
@Getter
@Setter
public abstract class CommonCommand {
    // NOTE: Validator lifecycle is now managed by Spring and should be injected into command handlers.
    // Commands remain as simple DTOs. Handlers will run bean validation using the shared Validator
    // and then call the command's custom validation hook if any.

    /**
     * Converts a set of constraint violations into a set of human-readable messages.
     * Handlers should use this to map the Validator results into error strings.
     */
    public static Set<String> toMessages(Set<? extends ConstraintViolation<?>> violations) {
        if (violations == null || violations.isEmpty()) {
            return Collections.emptySet();
        }

        return violations.stream()
                .map(error -> "%s:%s".formatted(error.getPropertyPath(), error.getMessage()))
                .collect(Collectors.toSet());
    }

    /**
     * Hook for command-specific validation logic that cannot be expressed with annotations.
     * Default implementation returns an empty set. Command implementations can override this
     * and provide additional domain-specific checks.
     */
    public Set<String> validateCustom() {
        return Collections.emptySet();
    }
}
