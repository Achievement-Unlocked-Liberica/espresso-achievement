package espresso.common.domain.events;

import java.time.Instant;
import java.time.OffsetDateTime;

import espresso.common.domain.support.KeyGenerator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all domain events in the system.
 * Domain events represent important business occurrences that other parts of the system
 * may need to react to. Events are published after successful aggregate persistence
 * and can trigger side effects, notifications, or integration with external systems.
 */
@Getter
@Setter
@SuperBuilder
public class CommonEvent {
    
    /**
     * Identifier used to correlate related events across operations.
     * Helps trace event chains and business process flows.
     */
    private String correlationId;

    /**
     * Unique identifier for this specific event instance.
     * Generated automatically for each event occurrence.
     */
    private String eventId;

    /**
     * Type or category of the event (e.g., "Achievement.Created", "User.Registered").
     * Used for event routing and handler selection.
     */
    private String eventType;

    /**
     * Source module or component that generated the event.
     * Identifies the origin of the event for debugging and routing.
     */
    private String source;

    /**
     * Timestamp when the event occurred (UTC).
     * Represents the business time of the event occurrence.
     */
    private OffsetDateTime timestamp;
}
