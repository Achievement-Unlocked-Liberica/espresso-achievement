package espresso.achievement.application.queryHandlers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.HandlerResponseList;
import espresso.common.domain.responses.ResponseType;
import lombok.NoArgsConstructor;

/**
 * Service class responsible for handling achievement-related queries.
 * Implements the {@link IAchievementQueryHandler} interface to process various
 * achievement queries
 * and return the corresponding read models or handler responses.
 * <p>
 * This handler interacts with the {@link IAchievementQryRepository} to fetch
 * achievement details,
 * summaries, and the latest achievements based on the provided queries.
 * </p>
 * 
 * <p>
 * Supported queries:
 * <ul>
 * <li>{@link GetAchievementDetailByKeyQuery} - Retrieves detailed information
 * about a specific achievement.</li>
 * <li>{@link GetAchievementSummaryByKeyQuery} - Retrieves a summary of a
 * specific achievement.</li>
 * <li>{@link GetAchievementSummariesByUserQuery} - Retrieves summaries of
 * achievements for a specific user.</li>
 * <li>{@link GetLatestAchievementsQuery} - Retrieves the latest achievements
 * with configurable DTO size and limit.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Input validation is performed for each query, and appropriate exceptions are
 * thrown for invalid inputs.
 * The handler returns either a successful result or an empty/error response
 * depending on the repository outcome.
 * </p>
 * 
 * <p>
 * The {@code getDtoSize} method maps {@link QuerySizeType} to the corresponding
 * DTO class for flexible response sizing.
 * </p>
 * 
 * <p>
 * This class is annotated with {@link Service} for Spring component scanning
 * and uses constructor injection.
 * </p>
 */
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

        List<AchievementSummaryReadModel> entity = achievementRepository
                .getAchievementSummariesByUserKey(query.getUserKey());

        return entity != null
                ? HandlerResult.success(null, entity)
                : HandlerResult.empty();
    }

    @Override
    public HandlerResponse<Object> handle(GetLatestAchievementsQuery qry) {

        HandlerResponse<Object> response;

        try {
            // Validate the query
            var validationErrors = qry.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Verify that fromDate is not null and is not after now (UTC)
            if (qry.getFromDate() != null && qry.getFromDate().isAfter(OffsetDateTime.now(ZoneOffset.UTC))) {
                return HandlerResponse.error(
                    new String[] { "fromDate must not be in the future" }, ResponseType.VALIDATION_ERROR);
            }

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

    /**
     * Maps QuerySizeType to the appropriate Achievement DTO class
     * 
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
