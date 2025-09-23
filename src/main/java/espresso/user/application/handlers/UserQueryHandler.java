package espresso.user.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class UserQueryHandler extends CommonQueryHandler implements IUserQueryHandler {

    @Autowired
    private IUserRepository userRepository;

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
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
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
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
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
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
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
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

    @Override
    public Class<?> getDtoSize(QuerySizeType querySizeType) {
        switch (querySizeType) {
            case xl:
            case lg:
                return UserDtoLg.class;
            case md:
                return UserDtoMd.class;
            case sm:
            case xs:
            default:
                return UserDtoSm.class;
        }
    }

}
