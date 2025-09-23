package espresso.achievement.domain.contracts;

import espresso.achievement.domain.commands.DisableAchievementCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface IDisableAchievementCommandHandler {
    HandlerResponse<Object> handle(DisableAchievementCommand command);
}
