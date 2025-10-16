package espresso.challenge.application.queryHandlers;

import java.util.List;

import org.springframework.stereotype.Service;

import espresso.challenge.domain.contracts.IChallengeRepository;
import espresso.challenge.domain.contracts.IChallengeQueryHandler;
import espresso.challenge.domain.entities.ChallengeDtoLg;
import espresso.challenge.domain.entities.ChallengeDtoMd;
import espresso.challenge.domain.entities.ChallengeDtoSm;
import espresso.challenge.domain.queries.GetChallengeDetailQuery;
import espresso.challenge.domain.queries.GetLatestChallengesQuery;
import espresso.challenge.domain.queries.GetMyChallengesQuery;
import espresso.challenge.domain.queries.GetUserChallengesQuery;
import espresso.common.domain.queries.QuerySizeType;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.application.handlers.CommonQueryHandler;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.UserKto;

@Service
public class ChallengeQueryHandler extends CommonQueryHandler implements IChallengeQueryHandler {

    private final IChallengeRepository challengeRepository;
    private final IUserRepository userRepository;

    /**
     * Constructor for dependency injection.
     * 
     * @param challengeRepository Repository for challenge entity queries and operations
     * @param userRepository Repository for user entity queries
     */
    public ChallengeQueryHandler(IChallengeRepository challengeRepository, IUserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public HandlerResponse<Object> handle(GetLatestChallengesQuery qry) {

        HandlerResponse<Object> response;

        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            // Get the challenges from repository
            List<?> challengeDtos = challengeRepository.getLatestChallenges(getDtoSize(qry.getSize()),
                    qry.getLimit(), qry.getFromDate());

            response = challengeDtos != null
                    ? HandlerResponse.success(challengeDtos)
                    : HandlerResponse.error(null, ResponseType.NOT_FOUND);

            return response;
        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

    @Override
    public HandlerResponse<Object> handle(GetChallengeDetailQuery qry) {

        HandlerResponse<Object> response;

        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            Object challengeDto = challengeRepository.getChallengeByKey(getDtoSize(qry.getSize()), qry.getEntityKey());

            response = challengeDto != null
                    ? HandlerResponse.success(challengeDto)
                    : HandlerResponse.error(null, ResponseType.NOT_FOUND);

            return response;

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }

    }

    @Override
    public HandlerResponse<Object> handle(GetMyChallengesQuery qry) {

        HandlerResponse<Object> response;

        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            // Get the profile of the user that is requesting their challenges
            UserKto userKto = userRepository.findByKey(qry.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Get the challenges from repository for the authenticated user
            List<?> challengeDtos = challengeRepository.getChallengesByUserKey(getDtoSize(qry.getSize()),
                    qry.getUserKey(), qry.getLimit(), qry.getFromDate());

            response = challengeDtos != null
                    ? HandlerResponse.success(challengeDtos)
                    : HandlerResponse.error(null, ResponseType.NOT_FOUND);

            return response;
        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

    @Override
    public HandlerResponse<Object> handle(GetUserChallengesQuery qry) {

        HandlerResponse<Object> response;

        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            // Get the profile of the user whose challenges are being requested
            UserKto userKto = userRepository.findByKey(qry.getRequestedUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Get the challenges from repository for the specified user
            List<?> challengeDtos = challengeRepository.getChallengesByUserKey(getDtoSize(qry.getSize()),
                    qry.getRequestedUserKey(), qry.getLimit(), qry.getFromDate());

            response = challengeDtos != null
                    ? HandlerResponse.success(challengeDtos)
                    : HandlerResponse.error(null, ResponseType.NOT_FOUND);

            return response;
        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

    /**
     * Maps QuerySizeType to the appropriate Challenge DTO class
     * 
     * @param querySizeType the size type enum
     * @return the corresponding DTO class
     */
    public Class<?> getDtoSize(QuerySizeType querySizeType) {
        switch (querySizeType) {
            case xl, lg:
                return ChallengeDtoLg.class;
            case md:
                return ChallengeDtoMd.class;
            case sm, xs:
                return ChallengeDtoSm.class;
            default:
                return ChallengeDtoSm.class;
        }
    }
}
