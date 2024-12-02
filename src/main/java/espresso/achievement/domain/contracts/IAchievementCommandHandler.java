package espresso.achievement.domain.contracts;

import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.entities.Achievement;

public interface IAchievementCommandHandler {
    
    public HandlerResult<Object> handle(CreateAchivementCommand command) ;
}
