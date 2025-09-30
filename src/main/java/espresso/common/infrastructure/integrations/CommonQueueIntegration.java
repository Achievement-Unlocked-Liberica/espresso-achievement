package espresso.common.infrastructure.integrations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

 
import org.springframework.stereotype.Component;

import espresso.common.domain.contracts.IQueueNameResolver;
import espresso.common.domain.events.CommonEvent;
 
import espresso.security.domain.operational.exceptionPolicy.SecurityException;
import espresso.security.domain.operational.validationPolicy.SecurityValidator;

@Component
public class CommonQueueIntegration {

    private final Map<String, IQueueNameResolver> resolvers = new ConcurrentHashMap<>();
    private final CommonRBMQProvider rbmqProvider;

    /**
     * Constructor for dependency injection.
     * 
     * @param rbmqProvider RabbitMQ provider for message operations
     */
    public CommonQueueIntegration(CommonRBMQProvider rbmqProvider) {
        this.rbmqProvider = rbmqProvider;
    }

    /**
     * Registers a queue name resolver for a specific source module.
     * This allows each module to define its own queue routing logic.
     * 
     * @param source The source module identifier
     * @param resolver The resolver function for that module
     */
    public void registerResolver(String source, IQueueNameResolver resolver) {
        resolvers.put(source, resolver);
    }

    /**
     * Emits an event to the appropriate queue using the registered resolver
     * for the event's source module.
     * 
     * @param event The event to emit
     * @throws IllegalStateException if no resolver is registered for the event's source
     */
    public void emitEvent(CommonEvent event) {
        try {
            SecurityValidator.validateEvent(event);
            
            IQueueNameResolver resolver = resolvers.get(event.getSource());
            
            SecurityValidator.validateQueueNameResolver(resolver, event.getSource());
            
            String queueName = resolver.resolveQueueName(event.getEventType(), event.getSource());
            
            SecurityValidator.validateQueueName(queueName, event.getSource());

            rbmqProvider.emitJson(event, queueName);
            
        } catch (SecurityException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (Exception e) {
            throw SecurityException.integrationFailed("Unexpected error occurred while emitting event: " + e.getMessage());
        }
    }

    /**
     * Gets the number of registered resolvers.
     * Useful for monitoring and debugging purposes.
     * 
     * @return The number of registered resolvers
     */
    public int getRegisteredResolverCount() {
        return resolvers.size();
    }

    /**
     * Checks if a resolver is registered for the given source.
     * 
     * @param source The source module identifier
     * @return true if a resolver is registered, false otherwise
     */
    public boolean hasResolverForSource(String source) {
        return resolvers.containsKey(source);
    }
}
