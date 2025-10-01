package espresso.common.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;

import espresso.common.domain.queries.CommonQuery;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for query handlers that provides validation functionality.
 * This class centralizes validation logic similar to CommonCommandHandler
 * but for query objects instead of commands.
 */
public abstract class CommonQueryHandler {
    
    @Autowired
    private Validator validator; // Shared Spring-managed validator

    /**
     * Collects bean validation and custom validation errors for a query.
     * @param query The query instance to validate
     * @return set of error messages (empty if none)
     */
    protected <T extends CommonQuery> Set<String> collectValidationErrors(T query) {
        Set<ConstraintViolation<T>> violations = validator.validate(query);
        Set<String> errors = new HashSet<>(CommonQuery.toMessages(violations));
        errors.addAll(query.validateCustom());
        return errors;
    }

    /**
     * Validates the query and returns a HandlerResponse containing validation errors
     * if any. Returns null when validation passes so callers can proceed.
     * 
     * @param query The query to validate
     * @return HandlerResponse with validation errors, or null if validation passes
     */
    protected <T extends CommonQuery> HandlerResponse<Object> validateQuery(T query) {
        Set<String> errors = collectValidationErrors(query);
        if (!errors.isEmpty()) {
            return HandlerResponse.error(errors, ResponseType.VALIDATION_ERROR);
        }
        return null;
    }
}