package espresso.achievement.domain.contracts;

import espresso.achievement.domain.commands.DeleteAchievementCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface IDeleteAchievementCommandHandler {
    HandlerResponse<Object> handle(DeleteAchievementCommand command);
}
