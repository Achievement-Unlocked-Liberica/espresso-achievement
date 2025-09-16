package espresso.achievement.domain.contracts;

import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface IUploadAchievementMediaCommandHandler {
    HandlerResponse<Object> handle(UploadAchievementMediaCommand command);
}
