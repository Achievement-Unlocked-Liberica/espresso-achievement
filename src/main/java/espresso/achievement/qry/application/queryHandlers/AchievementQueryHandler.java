package espresso.achievement.qry.application.queryHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.common.response.HandlerResult;
import espresso.achievement.qry.domain.contracts.IAchievementQryRepository;
import espresso.achievement.qry.domain.contracts.IAchievementQueryHandler;
import espresso.achievement.qry.domain.queries.GetAchievementDetailByKeyQuery;
import espresso.achievement.qry.domain.queries.GetAchievementSummaryByKeyQuery;
import espresso.achievement.qry.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.qry.domain.readModels.AchievementSummaryReadModel;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class AchievementQueryHandler implements IAchievementQueryHandler {

    @Autowired
    IAchievementQryRepository achievementRepository;

    @Override
    public HandlerResult<AchievementDetailReadModel> handle(GetAchievementDetailByKeyQuery query) {
        // ? Validate the inputs and throw an exception if the input is invalid
        if (query == null) {
            throw new IllegalArgumentException("The query is null");
        }

        AchievementDetailReadModel entity = achievementRepository.getAchievementDetailByKey(query.getKey());

        return entity != null
                ? HandlerResult.success(null, entity)
                : HandlerResult.empty();
    }

    @Override
    public HandlerResult<AchievementSummaryReadModel> handle(GetAchievementSummaryByKeyQuery query) {
        // ? Validate the inputs and throw an exception if the input is invalid
        if (query == null) {
            throw new IllegalArgumentException("The query is null");
        }

        AchievementSummaryReadModel entity = achievementRepository.getAchievementSummaryByKey(query.getKey());

        return entity != null
                ? HandlerResult.success(null, entity)
                : HandlerResult.empty();
    }
}
