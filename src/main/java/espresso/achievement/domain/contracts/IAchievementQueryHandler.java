package espresso.achievement.domain.contracts;

import java.util.List;

import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.queries.GetAchievementDetailByKeyQuery;
import espresso.achievement.domain.queries.GetAchievementSummariesByUserQuery;
import espresso.achievement.domain.queries.GetAchievementSummaryByKeyQuery;
import espresso.achievement.domain.queries.GetLatestAchievementsQuery;
import espresso.achievement.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.domain.readModels.AchievementSummaryReadModel;

public interface IAchievementQueryHandler {

    public HandlerResult<AchievementDetailReadModel> handle(GetAchievementDetailByKeyQuery query);

    public HandlerResult<AchievementSummaryReadModel> handle(GetAchievementSummaryByKeyQuery query);

    public HandlerResult<List<AchievementSummaryReadModel>> handle(GetAchievementSummariesByUserQuery query);

    public HandlerResult<Object> handle(GetLatestAchievementsQuery query);

}
