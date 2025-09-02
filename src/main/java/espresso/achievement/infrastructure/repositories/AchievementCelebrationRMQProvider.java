package espresso.achievement.infrastructure.repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.entities.AchievementCelebration;

/**
 * RabbitMQ data provider for achievement celebration messaging.
 * Publishes celebration events to a RabbitMQ queue for downstream processing.
 */
@Service
public class AchievementCelebrationRMQProvider {

    /**
     * The RabbitMQ queue name for achievement celebrations
     */
    @Value("${messaging.achievement.celebration.queue}")
    private String celebrationQueueName;

    /**
     * Emits an achievement celebration to the RabbitMQ queue.
     * This method publishes the celebration event for downstream processing.
     * 
     * @param celebration The celebration to emit to the queue
     */
    public void emit(AchievementCelebration celebration) {
        // TODO: Implement RabbitMQ publishing once RabbitMQ dependencies are added
        // For now, this is a placeholder implementation
        
        System.out.println("Publishing achievement celebration to queue: " + celebrationQueueName);
        System.out.println("Celebration details: " + 
            "achievementKey=" + celebration.getAchievementKey() + 
            ", userKey=" + celebration.getUserKey() + 
            ", count=" + celebration.getCount());
        
        // Future implementation would include:
        // 1. Convert celebration to message format (JSON)
        // 2. Use RabbitTemplate to send to the configured queue
        // 3. Handle publishing errors appropriately
    }
}
