package espresso.user.application.commandHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.user.domain.commands.AddUserCommand;
import espresso.user.domain.contracts.IUserCommandHandler;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

@Service
public class AddUserCommandHandler implements IUserCommandHandler {

    @Autowired
    private IUserRepository userRepository;

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
}
