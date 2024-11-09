package espresso.achievement.qry.domain.contracts;

import espresso.achievement.common.response.HandlerResult;
import espresso.achievement.qry.domain.queries.GetAchievementDetailByKeyQuery;
import espresso.achievement.qry.domain.queries.GetAchievementSummaryByKeyQuery;
import espresso.achievement.qry.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.qry.domain.readModels.AchievementSummaryReadModel;

public interface IAchievementQueryHandler {

    public HandlerResult<AchievementDetailReadModel> handle(GetAchievementDetailByKeyQuery query) ;

    public HandlerResult<AchievementSummaryReadModel> handle(GetAchievementSummaryByKeyQuery query) ;
}
