package espresso.achievement.domain.events;

import java.time.OffsetDateTime;
import java.util.Date;

import espresso.common.domain.events.CommonEvent;
import espresso.common.domain.events.EventActionTypes;
import espresso.common.domain.support.KeyGenerator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AchievementEvent extends CommonEvent {
    private final String key;
    private final String userKey;
    private final String title;
    private final String description;
    private final Date completedDate;

    private final String[] skillKeys;

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