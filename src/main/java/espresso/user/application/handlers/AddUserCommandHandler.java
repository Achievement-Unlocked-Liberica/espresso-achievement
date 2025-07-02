package espresso.user.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.user.domain.commands.AddUserCommand;
import espresso.user.domain.commands.UpdateProfilePictureCommand;
import espresso.user.domain.contracts.IUserCommandHandler;
import espresso.user.domain.contracts.IUserProfileImageRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.user.domain.entities.UserProfileImage;

@Service
public class AddUserCommandHandler implements IUserCommandHandler {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IUserProfileImageRepository userProfileImageRepository;


    public HandlerResponse<Object> handle(AddUserCommand command) {
        try {
            // Validate the command
            var validationErrors = command.validate();
            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Check if username already exists
            User existingUserByUsername = userRepository.findByUsername(command.getUsername());
            if (existingUserByUsername != null) {
                return HandlerResponse.error("Username already exists", ResponseType.VALIDATION_ERROR);
            }

            // Check if email already exists
            User existingUserByEmail = userRepository.findByEmail(command.getEmail());
            if (existingUserByEmail != null) {
                return HandlerResponse.error("Email already exists", ResponseType.VALIDATION_ERROR);
            }

            // Create the user entity
            User entity = User.create(
                    command.getUsername(),
                    command.getEmail(),
                    command.getFirstName(),
                    command.getLastName(),
                    command.getBirthDate());

            User savedEntity = userRepository.save(entity);

            return HandlerResponse.created(savedEntity);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

    @Override
    public HandlerResponse<Object> handle(UpdateProfilePictureCommand cmd) {
        try {
            // Validate the command
            var validationErrors = cmd.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Retrieve the RegisteredUser by key
            User user = userRepository.findByKey(cmd.getRegisteredUserKey(), User.class);

            if (user == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            UserProfileImage entity = UserProfileImage.create(
                    user,
                    cmd.getImage().getOriginalFilename(),
                    cmd.getImage().getContentType(),
                    cmd.getImage().getBytes());

            UserProfileImage savedEntity = userProfileImageRepository.save(entity);

            user.setProfileImage(savedEntity);

            userRepository.save(user);

            return HandlerResponse.success(savedEntity);
        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

}
