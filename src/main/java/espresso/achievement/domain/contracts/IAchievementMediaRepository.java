package espresso.achievement.domain.contracts;

import java.io.IOException;

import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;

public interface IAchievementMediaRepository {
    AchievementMedia save(Achievement achievement, AchievementMedia achievementMedia) throws IOException;
}
