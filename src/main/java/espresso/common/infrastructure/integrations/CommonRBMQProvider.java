package espresso.common.infrastructure.integrations;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
 
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import espresso.common.domain.events.CommonEvent;
import espresso.security.domain.operational.exceptionPolicy.SecurityException;
import espresso.security.domain.operational.validationPolicy.SecurityValidator;

@Component
public class CommonRBMQProvider {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for dependency injection.
     * 
     * @param rabbitTemplate Spring AMQP RabbitMQ template for message operations
     * @param objectMapper Jackson object mapper for JSON serialization
     */
    public CommonRBMQProvider(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Emit a JSON representation of the event to the specified RabbitMQ queue.
     *
     * @param event    The event to emit.
     * @param queueName The name of the RabbitMQ queue to send the event to.
     */
    public void emitJson(CommonEvent event, String queueName) {
        try {
            SecurityValidator.validateEventForSerialization(event);
            SecurityValidator.validateRabbitMQQueueName(queueName);
            
            // Convert event to JSON string to avoid SimpleMessageConverter limitations
            String eventJson = objectMapper.writeValueAsString(event);
            
            // Send the JSON string to the queue
            rabbitTemplate.convertAndSend(
                    queueName,
                    eventJson,
                    message -> {
                        message.getMessageProperties().setContentType("text/json");
                        return message;
                    }
            );

        } catch (SecurityException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (JsonProcessingException e) {
            throw SecurityException.integrationFailed("Failed to serialize the event: " + e.getMessage());
        } catch (AmqpException e) {
            throw SecurityException.integrationFailed("Failed to emit the event to RabbitMQ: " + e.getMessage());
        } catch (Exception e) {
            throw SecurityException.integrationFailed("Unexpected error occurred while emitting event to RabbitMQ: " + e.getMessage());
        }
    }
}
