package espresso.achievement.infrastructure.integrations;

import org.springframework.stereotype.Component;
import espresso.achievement.domain.events.AchievementCelebrationEvent;
import espresso.achievement.domain.events.AchievementCommentEvent;
import espresso.achievement.domain.events.AchievementEvent;
import espresso.achievement.domain.events.AchievementMediaEvent;
import espresso.common.infrastructure.integrations.CommonQueueIntegration;

/**
 * Service for publishing achievement events to message queues.
 * This service demonstrates how to use the Strategy Registry Pattern
 * implementation of CommonQueueIntegration.
 */
@Component
public class AchievementEventPublisher {

    private final CommonQueueIntegration queueIntegration;

    /**
     * Constructor for dependency injection.
     * 
     * @param queueIntegration Common queue integration service for message publishing
     */
    public AchievementEventPublisher(CommonQueueIntegration queueIntegration) {
        this.queueIntegration = queueIntegration;
    }

    public void publishEvent(AchievementEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered AchievementQueueNameResolver
        queueIntegration.emitEvent(event);
    }

    /**
     * Publishes an achievement celebration event to the appropriate queue.
     * The queue is determined automatically by the registered resolver.
     * 
     * @param celebration The achievement celebration event to publish
     */
    public void publishEvent(AchievementCelebrationEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered AchievementQueueNameResolver
        queueIntegration.emitEvent(event);
    }

    public void publishEvent(AchievementCommentEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered AchievementQueueNameResolver
        queueIntegration.emitEvent(event);
    }

    /**
     * Publishes an achievement media event to the appropriate queue.
     * The queue is determined automatically by the registered resolver.
     * 
     * @param event The achievement media event to publish
     */
    public void publishEvent(AchievementMediaEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered AchievementQueueNameResolver
        queueIntegration.emitEvent(event);
    }

}
