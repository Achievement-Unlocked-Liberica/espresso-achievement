package espresso.achievement.domain.events;

import java.time.OffsetDateTime;

import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.support.KeyGenerator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Event raised when a comment is added to an achievement.
 * This event is used to notify other systems about achievement comments.
 */
@Getter
@SuperBuilder
public class AchievementCommentEvent extends CommonEvent {

    /**
     * The 7-character alphanumeric key of the achievement being commented on.
     */
    private String achievementKey;
    
    /**
     * The 7-character alphanumeric key of the user posting the comment.
     */
    private String userKey;
    
    /**
     * The text content of the comment.
     */
    private String commentText;

    /**
     * Creates a new achievement comment event.
     * 
     * @param eventType      The type of the event (for example: created, updated,
     *                       deleted...)
     * @param achievementKey The key of the achievement being commented on
     * @param userKey        The key of the user who added the comment
     * @param commentText    The text of the comment
     * @return A new AchievementCommentEvent instance
     */
    public static AchievementCommentEvent create(EventActionTypes eventType, String achievementKey, String userKey, String commentText) {
        return AchievementCommentEvent.builder()
                .eventId(KeyGenerator.generateKey(7))
                .timestamp(OffsetDateTime.now())
                .eventType("Achievement.Comment." + eventType.name())
                .source("achievement-module")
                .achievementKey(achievementKey)
                .userKey(userKey)
                .commentText(commentText)
                .build();
    }
}
