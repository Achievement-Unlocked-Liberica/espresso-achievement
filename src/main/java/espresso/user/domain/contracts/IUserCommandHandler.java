package espresso.user.domain.contracts;

import java.util.logging.Handler;

import espresso.common.domain.responses.HandlerResponse;
import espresso.user.domain.commands.AddUserCommand;
import espresso.user.domain.commands.UpdateProfilePictureCommand;

public interface IUserCommandHandler {
    HandlerResponse<Object> handle(AddUserCommand command);

    HandlerResponse<Object> handle(UpdateProfilePictureCommand command);
}
