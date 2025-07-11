package espresso.security.application.handlers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.domain.support.NameGenerator;
import espresso.security.domain.commands.RegisterUserCommand;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

@Service
public class RegisterUserCommandHandler {

    @Autowired
    private IUserRepository userRepository;

    public HandlerResponse<Object> handle(RegisterUserCommand command) {
        try {
            // Validate the command
            Set<String> validationErrors = command.validate();
            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Check if username already exists
            User existingUserByUsername = userRepository.findByUsername(command.getUsername());
            if (existingUserByUsername != null) {
                return HandlerResponse.error("LOCALIZE: USERNAME ALREADY EXISTS", ResponseType.VALIDATION_ERROR);
            }

            // Check if email already exists
            User existingUserByEmail = userRepository.findByEmail(command.getEmail());
            if (existingUserByEmail != null) {
                return HandlerResponse.error("LOCALIZE: EMAIL ALREADY EXISTS", ResponseType.VALIDATION_ERROR);
            }

            // Create new user for registration
            User newUser = User.createForRegistration(
                command.getUsername(),
                command.getEmail(),
                command.getPassword()
            );

            // Save the user
            User savedUser = userRepository.save(newUser);            

            return HandlerResponse.success(savedUser);

        } catch (Exception ex) {
            return HandlerResponse.error("LOCALIZE: USER REGISTRATION FAILED - " + ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
