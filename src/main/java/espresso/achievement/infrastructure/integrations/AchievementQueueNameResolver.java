package espresso.achievement.infrastructure.integrations;

import org.springframework.stereotype.Component;

import espresso.common.domain.contracts.IQueueNameResolver;

/**
 * Queue name resolver for the achievement module.
 * This class implements the module-specific logic for determining which queue
 * should receive different types of achievement events.
 */
@Component
public class AchievementQueueNameResolver implements IQueueNameResolver {

    /**
     * Resolves the queue name for achievement events based on the event type.
     * 
     * @param eventType The type of event (class name)
     * @param source    The source module (should be "achievement-module")
     * @return The queue name where the event should be sent
     */
    @Override
    public String resolveQueueName(String eventType, String source) {
        // Validate that this resolver is being used for the correct source
        if (!"achievement-module".equals(source)) {
            throw new IllegalArgumentException(
                    "AchievementQueueNameResolver can only handle 'achievement-module' source, got: " + source);
        }

        // Simple mapping logic based on event type

        // The queue name is the event type in lowercase with ".queue" suffix
        // e.g., for eventType "Achievement.Media", the queue name would be
        // "achievement.media.queue"
        String queueName = eventType.toLowerCase() + ".queue";

        return queueName;
    }
}
