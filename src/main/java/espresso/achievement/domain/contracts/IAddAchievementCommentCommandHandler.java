package espresso.achievement.domain.contracts;

import espresso.achievement.domain.commands.AddAchievementCommentCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface IAddAchievementCommentCommandHandler {
    HandlerResponse<Object> handle(AddAchievementCommentCommand command);
}
