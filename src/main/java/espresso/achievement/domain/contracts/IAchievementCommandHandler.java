package espresso.achievement.domain.contracts;

import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.domain.responses.HandlerResponse;

public interface IAchievementCommandHandler {
    
    HandlerResponse<Object> handle(CreateAchivementCommand command) ;
}
