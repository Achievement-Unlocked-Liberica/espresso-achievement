package espresso.security.application.handlers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.security.domain.commands.AuthCredentialsCommand;
import espresso.security.domain.contracts.ISecurityCommandHandler;
import espresso.security.domain.entities.JWTAuthToken;
import espresso.security.domain.entities.JWTUserToken;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

@Service
public class CredentialsCommandHandler implements ISecurityCommandHandler {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private JWTAuthToken jwtAuthToken;

    @Override
    public HandlerResponse<Object> handle(AuthCredentialsCommand command) {
        // Validate the command
        Set<String> validationErrors = command.validate();
        if (!validationErrors.isEmpty()) {
            return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
        }

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
}
