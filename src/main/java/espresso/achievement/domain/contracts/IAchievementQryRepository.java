package espresso.achievement.domain.contracts;

import java.util.List;

import espresso.achievement.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.domain.readModels.AchievementSummaryReadModel;

public interface IAchievementQryRepository {
    
    AchievementDetailReadModel getAchievementDetailByKey(String key);

    AchievementSummaryReadModel getAchievementSummaryByKey(String key);

    List<AchievementSummaryReadModel> getAchievementSummariesByUserKey(String userKey);
}
