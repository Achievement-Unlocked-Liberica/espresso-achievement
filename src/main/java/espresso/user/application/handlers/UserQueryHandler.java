package espresso.user.application.handlers;

import org.springframework.stereotype.Service;

import espresso.common.domain.queries.QuerySizeType;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.application.handlers.CommonQueryHandler;
import espresso.user.domain.queries.GetUserByKeyQuery;
import espresso.user.domain.queries.GetUserNameExistsQuery;
import espresso.user.domain.queries.GetEmailExistsQuery;
import espresso.user.domain.queries.GetMyUserQuery;
import espresso.user.domain.contracts.IUserQueryHandler;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.UserDtoLg;
import espresso.user.domain.entities.UserDtoMd;
import espresso.user.domain.entities.UserDtoSm;
import espresso.user.domain.operational.exceptionPolicy.UserHandlerExceptionPolicy;

@Service
public class UserQueryHandler extends CommonQueryHandler implements IUserQueryHandler {

    private final IUserRepository userRepository;
    private final UserHandlerExceptionPolicy exceptionPolicy;

    /**
     * Constructor for dependency injection.
     * 
     * @param userRepository Repository for user entity queries and operations
     * @param exceptionPolicy Centralized exception handling policy
     */
    public UserQueryHandler(
            IUserRepository userRepository,
            UserHandlerExceptionPolicy exceptionPolicy) {
        this.userRepository = userRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    @Override
    public HandlerResponse<Object> handle(GetUserByKeyQuery qry) {

        HandlerResponse<Object> response;

        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            // Get the registered user by user key and size, then set the response
            Object userDto = this.userRepository.findByKey(qry.getEntityKey(), getDtoSize(qry.getSize()));

            response = userDto != null
                    ? HandlerResponse.success(userDto)
                    : HandlerResponse.error(null, ResponseType.NOT_FOUND);

            return response;

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "get user by key");
        }
    }

    @Override
    public HandlerResponse<Object> handle(GetUserNameExistsQuery qry) {
        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            // Check if username exists in the database
            boolean usernameExists = this.userRepository.checkUsernameExists(qry.getUsername());

            return HandlerResponse.success(usernameExists);

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "check username exists");
        }
    }

    @Override
    public HandlerResponse<Object> handle(GetEmailExistsQuery qry) {
        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            // Check if email exists in the database
            boolean emailExists = this.userRepository.checkEmailExists(qry.getEmail());

            return HandlerResponse.success(emailExists);

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "check email exists");
        }
    }

    @Override
    public HandlerResponse<Object> handle(GetMyUserQuery qry) {
        try {
            // Validate the query
            var validationResult = validateQuery(qry);
            if (validationResult != null)
                return validationResult;

            // Get the user by entity key using UserDtoLg (same as GetUserByKeyQuery)
            Object userDto = this.userRepository.findByKey(qry.getEntityKey(), UserDtoLg.class);

            return userDto != null
                    ? HandlerResponse.success(userDto)
                    : HandlerResponse.error("User not found", ResponseType.NOT_FOUND);

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "get my user");
        }
    }

    @Override
    public Class<?> getDtoSize(QuerySizeType querySizeType) {
        switch (querySizeType) {
            case xl,lg:
                return UserDtoLg.class;
            case md:
                return UserDtoMd.class;
            case sm,xs:
            default:
                return UserDtoSm.class;
        }
    }

}
