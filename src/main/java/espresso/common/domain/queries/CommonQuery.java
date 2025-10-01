package espresso.common.domain.queries;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;

/**
 * Base class for all query objects in the CQRS architecture.
 * Queries represent read operations and contain parameters needed to retrieve data.
 * Provides common validation infrastructure using JSR-303 Bean Validation and custom validation hooks.
 * Queries are simple DTOs - validation logic is executed by query handlers using Spring's Validator.
 */
public abstract class CommonQuery {
    // NOTE: Validator lifecycle is now managed by Spring and should be injected into query handlers.
    // Queries remain as simple DTOs. Handlers will run bean validation using the shared Validator
    // and then call the query's custom validation hook if any.

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
     * Hook for query-specific validation logic that cannot be expressed with annotations.
     * Default implementation returns an empty set. Query implementations can override this
     * and provide additional domain-specific checks.
     */
    public Set<String> validateCustom() {
        return Collections.emptySet();
    }
}
