package espresso.achievement.domain.contracts;

import espresso.achievement.domain.commands.AddAchievementCelebrationCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface IAddAchievementCelebrationCommandHandler {
    HandlerResponse<Object> handle(AddAchievementCelebrationCommand command);
}
