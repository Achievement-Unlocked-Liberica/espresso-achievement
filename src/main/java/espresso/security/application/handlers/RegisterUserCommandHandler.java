package espresso.security.application.handlers;

import org.springframework.stereotype.Service;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.security.domain.commands.RegisterUserCommand;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.common.application.handlers.CommonCommandHandler;
import espresso.security.domain.operational.exceptionPolicy.SecurityHandlerExceptionPolicy;
// Validation centralized in CommonCommandHandler

/**
 * Command handler for user registration operations in the security domain.
 * Processes user registration commands to create new user accounts with validation
 * for unique usernames and email addresses. Extends CommonCommandHandler to inherit
 * standard validation and error handling capabilities.
 */
@Service
public class RegisterUserCommandHandler extends CommonCommandHandler {

    private final IUserRepository userRepository;
    private final SecurityHandlerExceptionPolicy exceptionPolicy;

    /**
     * Constructor for dependency injection.
     * 
     * @param userRepository Repository for user data access and persistence operations
     * @param exceptionPolicy Exception policy for centralized security exception handling
     */
    public RegisterUserCommandHandler(
            IUserRepository userRepository,
            SecurityHandlerExceptionPolicy exceptionPolicy) {
        this.userRepository = userRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    // validator provided by base class

    /**
     * Handles user registration commands to create new user accounts.
     * Validates command data, checks for username and email uniqueness,
     * creates a new user entity, and persists it to the repository.
     *
     * @param command The registration command containing new user details
     * @return HandlerResponse containing the created user on success or error details on failure
     */
    public HandlerResponse<Object> handle(RegisterUserCommand command) {
        try {
            // Validate the command using shared Validator and any custom checks on the command
            var invalid = validateCommand(command);
            if (invalid != null) return invalid;

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
            return exceptionPolicy.handleException(ex, "register user");
        }
    }
}
