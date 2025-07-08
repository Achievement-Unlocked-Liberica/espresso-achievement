package espresso.security.domain.contracts;

import espresso.common.domain.responses.HandlerResponse;
import espresso.security.domain.commands.AuthCredentialsCommand;
import espresso.security.domain.commands.RegisterUserCommand;

public interface ISecurityCommandHandler {
    HandlerResponse<Object> handle(AuthCredentialsCommand command);
    HandlerResponse<Object> handle(RegisterUserCommand command);
}
