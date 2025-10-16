package espresso.challenge.domain.events;

import java.time.OffsetDateTime;
import java.util.Date;

import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.support.KeyGenerator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Domain event representing challenge-related actions.
 * Fired when challenges are created, updated, or deleted.
 */
@Getter
@SuperBuilder
public class ChallengeEvent extends CommonEvent {
    /**
     * The 7-character alphanumeric key of the challenge.
     */
    private final String key;

    /**
     * The 7-character alphanumeric key of the user who owns the challenge.
     */
    private final String userKey;

    /**
     * The title or name of the challenge.
     */
    private final String title;

    /**
     * A detailed description of the challenge.
     */
    private final String description;

    /**
     * The date when the challenge will be fulfilled.
     */
    private final Date fulfillmentDate;

    /**
     * Array of skill abbreviations associated with the challenge.
     */
    private final String[] skillKeys;

    /**
     * Factory method to create a challenge event.
     * 
     * @param eventType      The type of action being performed (CREATE, UPDATE, DELETE)
     * @param key            The 7-character alphanumeric key of the challenge
     * @param userKey        The 7-character alphanumeric key of the user
     * @param title          The title of the challenge
     * @param description    The description of the challenge
     * @param fulfillmentDate The date when the challenge will be fulfilled
     * @param skillKeys      Array of skill abbreviations
     * @return A new ChallengeEvent instance
     */
    public static ChallengeEvent create(
            EventActionTypes eventType,
            String key,
            String userKey,
            String title,
            String description,
            Date fulfillmentDate,
            String[] skillKeys) {

        return ChallengeEvent.builder()
                .eventId(KeyGenerator.generateKey(7))
                .timestamp(OffsetDateTime.now())
                .eventType("Challenge." + eventType.name())
                .source("challenge-module")
                .key(key)
                .userKey(userKey)
                .title(title)
                .description(description)
                .fulfillmentDate(fulfillmentDate)
                .skillKeys(skillKeys)
                .build();
    }
}
