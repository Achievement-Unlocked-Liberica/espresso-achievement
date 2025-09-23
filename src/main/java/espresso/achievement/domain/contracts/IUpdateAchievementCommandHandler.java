package espresso.achievement.domain.contracts;

import espresso.achievement.domain.commands.UpdateAchievementCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface IUpdateAchievementCommandHandler {
    HandlerResponse<Object> handle(UpdateAchievementCommand command);
}
