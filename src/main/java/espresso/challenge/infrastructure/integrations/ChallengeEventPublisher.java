package espresso.challenge.infrastructure.integrations;

import org.springframework.stereotype.Component;
import espresso.challenge.domain.events.ChallengeEncouragementEvent;
import espresso.challenge.domain.events.ChallengeCommentEvent;
import espresso.challenge.domain.events.ChallengeEvent;
import espresso.challenge.domain.events.ChallengeMediaEvent;
import espresso.common.infrastructure.integrations.CommonQueueIntegration;

/**
 * Service for publishing challenge events to message queues.
 * This service demonstrates how to use the Strategy Registry Pattern
 * implementation of CommonQueueIntegration.
 */
@Component
public class ChallengeEventPublisher {

    private final CommonQueueIntegration queueIntegration;

    /**
     * Constructor for dependency injection.
     * 
     * @param queueIntegration Common queue integration service for message publishing
     */
    public ChallengeEventPublisher(CommonQueueIntegration queueIntegration) {
        this.queueIntegration = queueIntegration;
    }

    public void publishEvent(ChallengeEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered ChallengeQueueNameResolver
        queueIntegration.emitEvent(event);
    }

    /**
     * Publishes a challenge encouragement event to the appropriate queue.
     * The queue is determined automatically by the registered resolver.
     * 
     * @param event The challenge encouragement event to publish
     */
    public void publishEvent(ChallengeEncouragementEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered ChallengeQueueNameResolver
        queueIntegration.emitEvent(event);
    }

    public void publishEvent(ChallengeCommentEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered ChallengeQueueNameResolver
        queueIntegration.emitEvent(event);
    }

    /**
     * Publishes a challenge media event to the appropriate queue.
     * The queue is determined automatically by the registered resolver.
     * 
     * @param event The challenge media event to publish
     */
    public void publishEvent(ChallengeMediaEvent event) {

        // Emit the event - the queue name will be resolved automatically
        // by the registered ChallengeQueueNameResolver
        queueIntegration.emitEvent(event);
    }

}
