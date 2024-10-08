package espresso.achievement.cmd.domain.contracts;

import espresso.achievement.cmd.domain.entities.Achievement;

public interface IAchievementRepository {

    void save(Achievement achievement);
}
