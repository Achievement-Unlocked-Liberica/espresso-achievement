package espresso.achievement.domain.contracts;

import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface ICreateAchivementCommandHandler {
    HandlerResponse<Object> handle(CreateAchivementCommand command);
}
