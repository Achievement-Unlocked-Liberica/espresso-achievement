package espresso.achievement.domain.contracts;

import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.queries.GetAchievementMediaStorageQuery;
import espresso.achievement.domain.readModels.MediaStorageDetailReadModel;

public interface IAchievementMediaQueryHandler {
    
    public HandlerResult<MediaStorageDetailReadModel> handle(GetAchievementMediaStorageQuery query) ;
}
