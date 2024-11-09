package espresso.achievement.qry.domain.contracts;

import espresso.achievement.qry.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.qry.domain.readModels.AchievementSummaryReadModel;

public interface IAchievementQryRepository {
    
    AchievementDetailReadModel getAchievementDetailByKey(String key);

    AchievementSummaryReadModel getAchievementSummaryByKey(String key);
}
