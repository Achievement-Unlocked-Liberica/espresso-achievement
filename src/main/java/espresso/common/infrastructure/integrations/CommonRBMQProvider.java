package espresso.common.infrastructure.integrations;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import espresso.common.domain.events.CommonEvent;

@Component
public class CommonRBMQProvider {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Emit a JSON representation of the event to the specified RabbitMQ queue.
     *
     * @param event    The event to emit.
     * @param queueName The name of the RabbitMQ queue to send the event to.
     */
    public void emitJson(CommonEvent event, String queueName) {
        try {
            // Convert event to JSON string to avoid SimpleMessageConverterlimitations
            // Send the JSON string to the queue
            rabbitTemplate.convertAndSend(
                    queueName,
                    objectMapper.writeValueAsString(event),
                    message -> {
                        message.getMessageProperties().setContentType("text/json");
                        return message;
                    }
            );

        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Failed to serialize the event", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to emit the event to RabbitMQ", ex);
        }
    }
}
