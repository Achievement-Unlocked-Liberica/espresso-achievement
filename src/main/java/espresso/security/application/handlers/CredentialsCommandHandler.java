package espresso.security.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import espresso.common.application.handlers.CommonCommandHandler;
// Validation centralized in CommonCommandHandler

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.security.domain.commands.AuthCredentialsCommand;
import espresso.security.domain.commands.RegisterUserCommand;
import espresso.security.domain.contracts.ISecurityCommandHandler;
import espresso.security.domain.entities.JWTAuthToken;
import espresso.security.domain.entities.JWTUserToken;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

/**
 * Command handler for user authentication and credential validation.
 * Processes authentication commands to verify user credentials and generate JWT tokens.
 * Also delegates user registration commands to the specialized registration handler.
 * Extends CommonCommandHandler to inherit standard validation and error handling.
 */
@Service
public class CredentialsCommandHandler extends CommonCommandHandler implements ISecurityCommandHandler {

    /**
     * Repository for user data access and authentication operations.
     * Used to retrieve user information for credential verification.
     */
    @Autowired
    private IUserRepository userRepository;

    // validator provided by base class

    /**
     * JWT token service for generating and managing authentication tokens.
     * Creates secure tokens for authenticated user sessions.
     */
    @Autowired
    private JWTAuthToken jwtAuthToken;

    /**
     * Specialized handler for user registration operations.
     * Delegates registration commands to maintain separation of concerns.
     */
    @Autowired
    private RegisterUserCommandHandler registerUserCommandHandler;

    /**
     * Handles authentication credential commands to verify user login.
     * Validates user credentials, checks account status, and generates JWT tokens
     * for successful authentication attempts.
     *
     * @param command The authentication credentials command containing username and password
     * @return HandlerResponse containing JWT token on success or error details on failure
     */
    @Override
    public HandlerResponse<Object> handle(AuthCredentialsCommand command) {
        // Validate the command using shared Validator and any custom checks
    var invalid = validateCommand(command);
        if (invalid != null) return invalid;

        try {
            // Find user by username
            User user = userRepository.findByUsername(command.getUsername());
            if (user == null) {
                return HandlerResponse.error("LOCALIZE: INVALID USERNAME OR PASSWORD", ResponseType.UNAUTHORIZED);
            }

            // Check if user is active
            if (!user.isActive()) {
                return HandlerResponse.error("LOCALIZE: USER ACCOUNT IS INACTIVE", ResponseType.UNAUTHORIZED);
            }

            // Verify password
            if (!user.verifyPassword(command.getPassword())) {
                return HandlerResponse.error("LOCALIZE: INVALID USERNAME OR PASSWORD", ResponseType.UNAUTHORIZED);
            }

            // Generate JWT token
            JWTUserToken jwtToken = jwtAuthToken.generateToken(user);

            return HandlerResponse.success(jwtToken);

        } catch (Exception ex) {
            return HandlerResponse.error("LOCALIZE: AUTHENTICATION FAILED - " + ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

    /**
     * Handles user registration commands by delegating to the specialized registration handler.
     * Maintains clear separation of concerns between authentication and registration operations.
     *
     * @param command The user registration command containing new user details
     * @return HandlerResponse from the registration handler
     */
    @Override
    public HandlerResponse<Object> handle(RegisterUserCommand command) {
        return registerUserCommandHandler.handle(command);
    }
}
