package espresso.achievement.domain.events;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class NewAchievementCreated {
    private final String key;
    private final String userKey;
    private final Date completedDate;
    private final String[] skillKeys;
    private final String[] mediaKeys;
}
