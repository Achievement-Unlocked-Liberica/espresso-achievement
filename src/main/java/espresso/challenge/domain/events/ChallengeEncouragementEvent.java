package espresso.challenge.domain.events;

import java.time.OffsetDateTime;

import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.support.KeyGenerator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Event raised when an encouragement is added to a challenge.
 * This event is used to notify other systems about challenge encouragements.
 */
@Getter
@SuperBuilder
public class ChallengeEncouragementEvent extends CommonEvent {

    /**
     * The 7-character alphanumeric key of the challenge being encouraged.
     */
    private String challengeKey;
    
    /**
     * The 7-character alphanumeric key of the user giving the encouragement.
     */
    private String userKey;
    
    /**
     * The number of encouragements given (between 1 and 9).
     */
    private int count;

    /**
     * Creates a new challenge encouragement event.
     * 
     * @param eventType      The type of the event (for example: created, updated,
     *                       sent, deleted...)
     * @param challengeKey   The key of the challenge being encouraged
     * @param userKey        The key of the user who added the encouragement
     * @param count          The encouragement count that was added
     * @return A new ChallengeEncouragementEvent instance
     */
    public static ChallengeEncouragementEvent create(EventActionTypes eventType, String challengeKey, String userKey, int count) {
        return ChallengeEncouragementEvent.builder()
                .eventId(KeyGenerator.generateKey(7))
                .timestamp(OffsetDateTime.now())
                .eventType("Challenge.Encouragement." + eventType.name())
                .source("challenge-module")
                .challengeKey(challengeKey)
                .userKey(userKey)
                .count(count)
                .build();
    }
}
