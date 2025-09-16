package espresso.common.domain.events;

import java.time.Instant;
import java.time.OffsetDateTime;

import espresso.common.domain.support.KeyGenerator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CommonEvent {
    // Identifier used to correlate related events.
    private String correlationId;

    // Unique identifier for the event.
    private String eventId;

    // Type or category of the event.
    private String eventType;

    // Source that generated the event.
    private String source;

    // Timestamp when the event occurred (in milliseconds since epoch).
    private OffsetDateTime timestamp;

    public CommonEvent() {
        this.initializeEvent();
    }

    private void initializeEvent() {
        this.eventId = KeyGenerator.generateKey(7);
        this.timestamp = OffsetDateTime.now();
    }
}
