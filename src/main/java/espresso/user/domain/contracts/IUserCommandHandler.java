package espresso.user.domain.contracts;

import espresso.common.domain.responses.HandlerResponse;
import espresso.user.domain.commands.AddUserCommand;

public interface IUserCommandHandler {
    HandlerResponse<Object> handle(AddUserCommand command);
}
