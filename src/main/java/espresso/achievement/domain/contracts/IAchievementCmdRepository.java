package espresso.achievement.domain.contracts;

import espresso.achievement.domain.entities.Achievement;

public interface IAchievementCmdRepository {

    Achievement save(Achievement achievement);
    <T> T findByKey(String entityKey, Class<T> type);
}
