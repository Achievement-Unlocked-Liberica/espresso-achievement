package espresso.achievement.domain.contracts;

import java.util.List;

import espresso.achievement.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.domain.readModels.AchievementSummaryReadModel;

public interface IAchievementQryRepository {
    
    AchievementDetailReadModel getAchievementDetailByKey(String key);

    AchievementSummaryReadModel getAchievementSummaryByKey(String key);

    List<AchievementSummaryReadModel> getAchievementSummariesByUserKey(String userKey);
    
    /**
     * Gets the latest achievements ordered by completion date (newest first)
     * @param dtoType The DTO class to project to (e.g., AchievementDtoSm.class)
     * @param limit Maximum number of results to return
     * @return List of achievements projected to the specified DTO type
     */
    <T> List<T> getLatestAchievements(Class<T> dtoType, Integer limit);
}
