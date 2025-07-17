package espresso.achievement.domain.contracts;

import espresso.achievement.domain.entities.AchievementMedia;

public interface IAchievementMediaRepository {
    AchievementMedia save(AchievementMedia achievementMedia);
}
