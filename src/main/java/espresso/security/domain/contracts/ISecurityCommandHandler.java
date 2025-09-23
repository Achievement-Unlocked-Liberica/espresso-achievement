package espresso.security.domain.contracts;

import espresso.common.domain.responses.HandlerResponse;
import espresso.security.domain.commands.AuthCredentialsCommand;
import espresso.security.domain.commands.RegisterUserCommand;

/**
 * Contract interface for security command handling operations.
 * Defines the standard operations for processing authentication and user registration
 * commands within the security domain. Implementations should provide secure
 * credential validation and user account creation functionality.
 */
public interface ISecurityCommandHandler {
    
    /**
     * Handles authentication credential commands for user login operations.
     * Validates user credentials and generates authentication tokens for successful logins.
     *
     * @param command The authentication credentials command containing username and password
     * @return HandlerResponse containing authentication token on success or error details on failure
     */
    HandlerResponse<Object> handle(AuthCredentialsCommand command);
    
    /**
     * Handles user registration commands for creating new user accounts.
     * Validates registration data and creates new user entities with secure password handling.
     *
     * @param command The user registration command containing new user details
     * @return HandlerResponse containing the created user on success or error details on failure
     */
    HandlerResponse<Object> handle(RegisterUserCommand command);
}
