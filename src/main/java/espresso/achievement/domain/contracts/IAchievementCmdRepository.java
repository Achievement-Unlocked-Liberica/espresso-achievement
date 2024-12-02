package espresso.achievement.domain.contracts;

import espresso.achievement.domain.entities.Achievement;

public interface IAchievementCmdRepository {

    Achievement save(Achievement achievement);
}
