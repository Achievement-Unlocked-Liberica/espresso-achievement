package espresso.challenge.domain.contracts;

import espresso.challenge.domain.commands.AddChallengeEncouragementCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface IAddChallengeEncouragementCommandHandler {
    HandlerResponse<Object> handle(AddChallengeEncouragementCommand command);
}
