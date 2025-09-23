package espresso.achievement.domain.events;

import java.time.OffsetDateTime;
import java.util.Date;

import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.support.KeyGenerator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Domain event representing achievement-related actions.
 * Fired when achievements are created, updated, or deleted.
 */
@Getter
@SuperBuilder
public class AchievementEvent extends CommonEvent {
    /**
     * The 7-character alphanumeric key of the achievement.
     */
    private final String key;

    /**
     * The 7-character alphanumeric key of the user who owns the achievement.
     */
    private final String userKey;

    /**
     * The title or name of the achievement.
     */
    private final String title;

    /**
     * A detailed description of the achievement.
     */
    private final String description;

    /**
     * The date when the achievement was completed.
     */
    private final Date completedDate;

    /**
     * Array of skill abbreviations associated with the achievement.
     */
    private final String[] skillKeys;

    /**
     * Factory method to create an achievement event.
     * 
     * @param eventType     The type of action being performed (CREATE, UPDATE,
     *                      DELETE)
     * @param key           The 7-character alphanumeric key of the achievement
     * @param userKey       The 7-character alphanumeric key of the user
     * @param title         The title of the achievement
     * @param description   The description of the achievement
     * @param completedDate The completion date of the achievement
     * @param skillKeys     Array of skill abbreviations
     * @return A new AchievementEvent instance
     */
    public static AchievementEvent create(EventActionTypes eventType, String key, String userKey, String title,
            String description, Date completedDate, String[] skillKeys) {
        return AchievementEvent.builder()
                .eventId(KeyGenerator.generateKey(7))
                .timestamp(OffsetDateTime.now())
                .eventType("Achievement." + eventType.name())
                .source("achievement-module")
                .key(key)
                .userKey(userKey)
                .title(title)
                .description(description)
                .completedDate(completedDate)
                .skillKeys(skillKeys)
                .build();
    }
}