package espresso.common.domain.events;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CommonEvent {
    // Unique identifier for the event.
    private String eventId;

    // Type or category of the event.
    private String eventType;

    // Timestamp when the event occurred (in milliseconds since epoch).
    private Instant timestamp;

    // Source that generated the event.
    private String source;

    // Identifier used to correlate related events.
    private String correlationId;
}
