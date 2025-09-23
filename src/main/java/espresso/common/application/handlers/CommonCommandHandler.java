package espresso.common.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import espresso.common.domain.commands.CommonCommand;
import espresso.common.domain.models.DomainAggregate;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.Set;

public abstract class CommonCommandHandler {
    
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private Validator validator; // Shared Spring-managed validator


    /**
     * Publishes domain events from the given aggregate.
     *
     * @param aggregate The aggregate containing domain events
     */
    protected void publishDomainEvents(DomainAggregate aggregate) {
        var domainEvents = aggregate.getDomainEvents();
        domainEvents.forEach(event -> applicationEventPublisher.publishEvent(event));
        aggregate.clearDomainEvents();
    }

    /**
     * Collects bean validation and custom validation errors for a command.
     * @param command The command instance to validate
     * @return set of error messages (empty if none)
     */
    protected <T extends CommonCommand> Set<String> collectValidationErrors(T command) {
        Set<ConstraintViolation<T>> violations = validator.validate(command);
        Set<String> errors = new HashSet<>(CommonCommand.toMessages(violations));
        errors.addAll(command.validateCustom());
        return errors;
    }

    /**
     * Validates the command and returns a HandlerResponse containing validation errors
     * if any. Returns null when validation passes so callers can proceed.
     */
    protected <T extends CommonCommand> HandlerResponse<Object> validateCommand(T command) {
        Set<String> errors = collectValidationErrors(command);
        if (!errors.isEmpty()) {
            return HandlerResponse.error(errors, ResponseType.VALIDATION_ERROR);
        }
        return null;
    }
}
