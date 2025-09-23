package espresso.common.domain.contracts;

/**
 * Functional interface for resolving queue names based on event type and source.
 * This interface allows feature modules to define their own queue routing logic
 * while keeping the common infrastructure agnostic to specific queue naming strategies.
 */
@FunctionalInterface
public interface IQueueNameResolver {
    
    /**
     * Resolves the queue name for an event based on its type and source.
     * 
     * @param eventType The type of the event (typically the class name)
     * @param source The source module that raised the event
     * @return The queue name where the event should be sent
     */
    String resolveQueueName(String eventType, String source);
}
