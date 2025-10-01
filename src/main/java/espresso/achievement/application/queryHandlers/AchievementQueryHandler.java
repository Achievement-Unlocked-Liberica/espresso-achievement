package espresso.achievement.application.queryHandlers;

import java.util.List;

import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.contracts.IAchievementQueryHandler;
import espresso.achievement.domain.entities.AchievementDtoLg;
import espresso.achievement.domain.entities.AchievementDtoMd;
import espresso.achievement.domain.entities.AchievementDtoSm;
import espresso.achievement.domain.queries.GetAchievementDetailQuery;
import espresso.achievement.domain.queries.GetLatestAchievementsQuery;
import espresso.common.domain.queries.QuerySizeType;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.application.handlers.CommonQueryHandler;

@Service
public class AchievementQueryHandler extends CommonQueryHandler implements IAchievementQueryHandler {

    private final IAchievementRepository achievementRepository;

    /**
     * Constructor for dependency injection.
     * 
     * @param achievementRepository Repository for achievement entity queries and operations
     */
    public AchievementQueryHandler(IAchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Override
    public HandlerResponse<Object> handle(GetLatestAchievementsQuery qry) {

        HandlerResponse<Object> response;

        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            // Get the achievements from repository
            List<?> achievementDtos = achievementRepository.getLatestAchievements(getDtoSize(qry.getSize()),
                    qry.getLimit(), qry.getFromDate());

            response = achievementDtos != null
                    ? HandlerResponse.success(achievementDtos)
                    : HandlerResponse.error(null, ResponseType.NOT_FOUND);

            return response;
        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

    @Override
    public HandlerResponse<Object> handle(GetAchievementDetailQuery qry) {

        HandlerResponse<Object> response;

        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            Object achievementDto = achievementRepository.getAchievementByKey(getDtoSize(qry.getSize()), qry.getEntityKey());

            response = achievementDto != null
                    ? HandlerResponse.success(achievementDto)
                    : HandlerResponse.error(null, ResponseType.NOT_FOUND);

            return response;

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }

    }

    /**
     * Maps QuerySizeType to the appropriate Achievement DTO class
     * 
     * @param querySizeType the size type enum
     * @return the corresponding DTO class
     */
    public Class<?> getDtoSize(QuerySizeType querySizeType) {
        switch (querySizeType) {
            case xl, lg:
                return AchievementDtoLg.class;
            case md:
                return AchievementDtoMd.class;
            case sm,xs:
                return AchievementDtoSm.class;
            default:
                return AchievementDtoSm.class;
        }
    }
}
