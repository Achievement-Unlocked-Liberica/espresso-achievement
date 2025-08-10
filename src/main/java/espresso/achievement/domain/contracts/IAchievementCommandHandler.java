package espresso.achievement.domain.contracts;

import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.commands.AddAchievementCommentCommand;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.common.domain.responses.HandlerResponse;

public interface IAchievementCommandHandler {
    
    HandlerResponse<Object> handle(CreateAchivementCommand command);
    HandlerResponse<Object> handleUploadMedia(UploadAchievementMediaCommand command);
    HandlerResponse<Object> handleAddComment(AddAchievementCommentCommand command);
}
