package espresso.challenge.domain.events;

import java.time.OffsetDateTime;

import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.support.KeyGenerator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Domain event indicating that a comment was added to a challenge.
 * Published when a user successfully adds a comment to a challenge.
 */
@Getter
@SuperBuilder
public class ChallengeCommentEvent extends CommonEvent {

    /**
     * The unique key identifying the challenge
     */
    private String challengeKey;

    /**
     * The unique key identifying the user who added the comment
     */
    private String userKey;

    /**
     * The text content of the comment
     */
    private String commentText;

    /**
     * Factory method to create a ChallengeCommentEvent with all required fields.
     * 
     * @param eventType The type of event action (e.g., CREATED)
     * @param challengeKey The unique key of the challenge
     * @param userKey The unique key of the user who commented
     * @param commentText The text content of the comment
     * @return A new ChallengeCommentEvent instance
     */
    public static ChallengeCommentEvent create(
            EventActionTypes eventType,
            String challengeKey,
            String userKey,
            String commentText) {
        
        return ChallengeCommentEvent.builder()
                .eventId(KeyGenerator.generateKey(7))
                .timestamp(OffsetDateTime.now())
                .eventType("Challenge.Comment." + eventType.name())
                .source("challenge-module")
                .challengeKey(challengeKey)
                .userKey(userKey)
                .commentText(commentText)
                .build();
    }
}
