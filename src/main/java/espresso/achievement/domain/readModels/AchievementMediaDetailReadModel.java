package espresso.achievement.domain.readModels;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AchievementMediaDetailReadModel {

    private final String key;
    private final boolean isUploaded;
}
