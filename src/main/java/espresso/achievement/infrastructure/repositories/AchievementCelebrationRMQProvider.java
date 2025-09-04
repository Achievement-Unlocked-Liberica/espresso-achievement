package espresso.achievement.infrastructure.repositories;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import espresso.achievement.domain.entities.AchievementCelebration;

/**
 * RabbitMQ data provider for achievement celebration messaging.
 * Publishes celebration events to a RabbitMQ queue for downstream processing.
 */
@Service
public class AchievementCelebrationRMQProvider {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
        try {
            // Convert celebration to JSON string manually to avoid SimpleMessageConverter limitations
            String celebrationJson = objectMapper.writeValueAsString(celebration);
            
            // Send the JSON string to the queue
            rabbitTemplate.convertAndSend(celebrationQueueName, celebrationJson);
            
            // System.out.println("Successfully published achievement celebration to queue: " + celebrationQueueName);
            // System.out.println("Celebration details: " + 
            //     "achievementKey=" + celebration.getAchievementKey() + 
            //     ", userKey=" + celebration.getUserKey() + 
            //     ", count=" + celebration.getCount());
                
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to serialize celebration", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to emit celebration to RabbitMQ", ex);
        }
    }
}
