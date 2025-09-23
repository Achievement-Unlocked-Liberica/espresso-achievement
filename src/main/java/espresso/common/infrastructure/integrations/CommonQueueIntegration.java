package espresso.common.infrastructure.integrations;

import java.util.concurrent.ConcurrentHashMap;
import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import espresso.common.domain.contracts.IQueueNameResolver;
import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.support.KeyGenerator;

@Component
public class CommonQueueIntegration {

    private final Map<String, IQueueNameResolver> resolvers = new ConcurrentHashMap<>();
    
    @Autowired
    private CommonRBMQProvider rbmqProvider;

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
        IQueueNameResolver resolver = resolvers.get(event.getSource());
        
        if (resolver == null) {
            throw new IllegalStateException("No queue name resolver registered for source: " + event.getSource());
        }
        
        String queueName = resolver.resolveQueueName(event.getEventType(), event.getSource());

        rbmqProvider.emitJson(event, queueName);
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
