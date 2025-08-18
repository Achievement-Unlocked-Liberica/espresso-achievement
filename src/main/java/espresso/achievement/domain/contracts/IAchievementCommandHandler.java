package espresso.achievement.domain.contracts;

import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.commands.AddAchievementCommentCommand;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.commands.UpdateAchievementCommand;
import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface IAchievementCommandHandler {
    
    HandlerResponse<Object> handle(CreateAchivementCommand command);
    HandlerResponse<Object> handle(UploadAchievementMediaCommand command);
    HandlerResponse<Object> handle(AddAchievementCommentCommand command);
    HandlerResponse<Object> handle(UpdateAchievementCommand command);
}
