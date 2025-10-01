package espresso.common.domain.events;

import espresso.common.infrastructure.correlation.CorrelationContext;

/**
 * Helper utility for creating domain events with proper correlation ID population.
 * This ensures all events have correlation IDs for proper distributed tracing.
 */
public class EventBuilder {
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private EventBuilder() {
        // Utility class
    }
    
    /**
     * Create a domain event builder with correlation ID auto-populated from current context.
     * Use this method when creating events in handlers, services, or domain entities.
     * 
     * Example usage:
     * <pre>
     * AchievementCreatedEvent event = EventBuilder.createEvent(AchievementCreatedEvent.builder())
     *     .achievementKey(achievement.getKey())
     *     .eventType("Achievement.Created")
     *     .source("espresso-achievement")
     *     .timestamp(OffsetDateTime.now())
     *     .build();
     * </pre>
     * 
     * @param <T> The type of event being built
     * @param <B> The type of the event builder
     * @param builder The event builder to configure
     * @return The configured builder with correlation ID set
     */
    public static <T extends CommonEvent, B extends CommonEvent.CommonEventBuilder<T, B>> 
           B createEvent(B builder) {
        
        // Auto-populate correlation ID from current request context
        String correlationId = CorrelationContext.getCorrelationId();
        if (correlationId != null) {
            builder.correlationId(correlationId);
        }
        
        return builder;
    }
    
    /**
     * Create a domain event builder with explicit correlation ID.
     * Use this method when you need to set a specific correlation ID 
     * (e.g., when processing events from external systems).
     * 
     * @param <T> The type of event being built
     * @param <B> The type of the event builder
     * @param builder The event builder to configure
     * @param correlationId The specific correlation ID to use
     * @return The configured builder with correlation ID set
     */
    public static <T extends CommonEvent, B extends CommonEvent.CommonEventBuilder<T, B>> 
           B createEvent(B builder, String correlationId) {
        
        if (correlationId != null && !correlationId.trim().isEmpty()) {
            builder.correlationId(correlationId);
        }
        
        return builder;
    }
}