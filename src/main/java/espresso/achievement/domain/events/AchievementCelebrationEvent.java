package espresso.achievement.domain.events;

import java.time.OffsetDateTime;

import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.support.KeyGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Event raised when a celebration is added to an achievement.
 * This event is used to notify other systems about achievement celebrations.
 */
@Getter
@SuperBuilder
public class AchievementCelebrationEvent extends CommonEvent {

    private String achievementKey;
    private String userKey;
    private int count;

    /**
     * Creates a new achievement celebration event.
     * 
     * @param eventType      The type of the event (for example: created, updated,
     *                       sent, deleted...)
     * @param achievementKey The key of the achievement being celebrated
     * @param userKey        The key of the user who added the celebration
     * @param count          The celebration count that was added
     * @return A new AchievementCelebrationEvent instance
     */
    public static AchievementCelebrationEvent create(EventActionTypes eventType, String achievementKey, String userKey, int count) {
        return AchievementCelebrationEvent.builder()
                .eventId(KeyGenerator.generateKey(7))
                .timestamp(OffsetDateTime.now())
                .eventType("Achievement.Celebration." + eventType.name())
                .source("achievement-module")
                .achievementKey(achievementKey)
                .userKey(userKey)
                .count(count)
                .build();
    }
}
