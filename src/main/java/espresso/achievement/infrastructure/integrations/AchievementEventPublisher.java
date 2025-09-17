package espresso.achievement.infrastructure.integrations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.entities.AchievementCelebration;
import espresso.achievement.domain.events.AchievementCelebrationEvent;
import espresso.achievement.domain.events.AchievementEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.infrastructure.integrations.CommonQueueIntegration;

/**
 * Service for publishing achievement events to message queues.
 * This service demonstrates how to use the Strategy Registry Pattern
 * implementation of CommonQueueIntegration.
 */
@Component
public class AchievementEventPublisher {

    @Autowired
    private CommonQueueIntegration queueIntegration;

    /**
     * Publishes an achievement celebration event to the appropriate queue.
     * The queue is determined automatically by the registered resolver.
     * 
     * @param celebration The achievement celebration event to publish
     */
    public void publishCelebrationCreatedEvent(AchievementCelebrationEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered AchievementQueueNameResolver
        queueIntegration.emitEvent(event);
    }

    public void publishAchievementEvent(AchievementEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered AchievementQueueNameResolver
        queueIntegration.emitEvent(event);
    }
}
