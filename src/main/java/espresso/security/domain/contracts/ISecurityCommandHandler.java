package espresso.security.domain.contracts;

import espresso.common.domain.responses.HandlerResponse;
import espresso.security.domain.commands.AuthCredentialsCommand;

public interface ISecurityCommandHandler {
    HandlerResponse<Object> handle(AuthCredentialsCommand command);
}
