package espresso.security.domain.operational.validationPolicy;

import espresso.common.domain.contracts.IQueueNameResolver;
import espresso.common.domain.events.CommonEvent;
import espresso.security.domain.operational.exceptionPolicy.SecurityException;

/**
 * Validation utility class for Security/Common module entities and operations.
 * Provides static validation methods to encapsulate validation logic and keep infrastructure classes lean.
 * 
 * This validator handles validation for:
 * - CommonEvent entities (for queue operations)
 * - Queue-related parameters
 * - Integration and messaging operations
 */
public class SecurityValidator {

    /**
     * Validates a CommonEvent entity for queue operations.
     * Checks that the event is not null and has a valid source.
     *
     * @param event The common event to validate
     * @throws SecurityException if validation fails
     */
    public static void validateEvent(CommonEvent event) {
        if (event == null) {
            throw SecurityException.validationFailed("Event cannot be null");
        }
        
        if (event.getSource() == null || event.getSource().trim().isEmpty()) {
            throw SecurityException.validationFailed("Event source cannot be null or empty");
        }
    }

    /**
     * Validates a queue name resolver.
     * Checks that the resolver is not null.
     *
     * @param resolver The queue name resolver to validate
     * @param source The event source (for error context)
     * @throws SecurityException if validation fails
     */
    public static void validateQueueNameResolver(IQueueNameResolver resolver, String source) {
        if (resolver == null) {
            throw SecurityException.integrationFailed("No queue name resolver registered for source: " + source);
        }
    }

    /**
     * Validates a resolved queue name.
     * Checks that the queue name is not null or empty.
     *
     * @param queueName The resolved queue name to validate
     * @param source The event source (for error context)
     * @throws SecurityException if validation fails
     */
    public static void validateQueueName(String queueName, String source) {
        if (queueName == null || queueName.trim().isEmpty()) {
            throw SecurityException.integrationFailed("Resolved queue name cannot be null or empty for source: " + source);
        }
    }

    /**
     * Validates a queue name parameter for RabbitMQ operations.
     * Checks that the queue name is not null or empty.
     *
     * @param queueName The queue name to validate
     * @throws SecurityException if validation fails
     */
    public static void validateRabbitMQQueueName(String queueName) {
        if (queueName == null || queueName.trim().isEmpty()) {
            throw SecurityException.validationFailed("Queue name cannot be null or empty");
        }
    }

    /**
     * Validates an event for RabbitMQ JSON serialization.
     * Checks that the event is not null.
     *
     * @param event The event to validate
     * @throws SecurityException if validation fails
     */
    public static void validateEventForSerialization(CommonEvent event) {
        if (event == null) {
            throw SecurityException.validationFailed("Event cannot be null");
        }
    }
}