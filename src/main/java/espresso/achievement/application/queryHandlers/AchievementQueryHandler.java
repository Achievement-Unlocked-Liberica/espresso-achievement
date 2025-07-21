package espresso.achievement.application.queryHandlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.contracts.IAchievementQryRepository;
import espresso.achievement.domain.contracts.IAchievementQueryHandler;
import espresso.achievement.domain.entities.AchievementDtoLg;
import espresso.achievement.domain.entities.AchievementDtoMd;
import espresso.achievement.domain.entities.AchievementDtoSm;
import espresso.achievement.domain.queries.GetAchievementDetailByKeyQuery;
import espresso.achievement.domain.queries.GetAchievementSummariesByUserQuery;
import espresso.achievement.domain.queries.GetAchievementSummaryByKeyQuery;
import espresso.achievement.domain.queries.GetLatestAchievementsQuery;
import espresso.achievement.domain.readModels.AchievementDetailReadModel;
import espresso.achievement.domain.readModels.AchievementSummaryReadModel;
import espresso.common.domain.queries.QuerySizeType;
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

    @Override
    public HandlerResult<List<AchievementSummaryReadModel>> handle(GetAchievementSummariesByUserQuery query) {
        // ? Validate the inputs and throw an exception if the input is invalid
        if (query == null) {
            throw new IllegalArgumentException("The query is null");
        }

        List<AchievementSummaryReadModel> entity = achievementRepository.getAchievementSummariesByUserKey(query.getUserKey());

        return entity != null
                ? HandlerResult.success(null, entity)
                : HandlerResult.empty();
    }

    @Override
    public HandlerResult<Object> handle(GetLatestAchievementsQuery query) {
        // Validate the inputs and throw an exception if the input is invalid
        if (query == null) {
            throw new IllegalArgumentException("The query is null");
        }

        if (query.getDtoSize() == null) {
            throw new IllegalArgumentException("DTO size must be specified");
        }

        // Get the achievements from repository
        List<?> achievements = achievementRepository.getLatestAchievements(getDtoSize(query.getDtoSize()), query.getLimit());

        return achievements != null && !achievements.isEmpty()
                ? HandlerResult.success(null, achievements)
                : HandlerResult.empty();
    }

    /**
     * Maps QuerySizeType to the appropriate Achievement DTO class
     * @param querySizeType the size type enum
     * @return the corresponding DTO class
     */
    public Class<?> getDtoSize(QuerySizeType querySizeType) {
        switch (querySizeType) {
            case xl:
            case lg:
                return AchievementDtoLg.class;
            case md:
                return AchievementDtoMd.class;
            case sm:
            case xs:
                return AchievementDtoSm.class;
            default:
                return AchievementDtoSm.class;
        }
    }
}
