package espresso.achievement.cmd.domain.contracts;

import espresso.achievement.cmd.domain.commands.CreateAchivementCommand;
import espresso.achievement.cmd.domain.entities.Achievement;
import espresso.achievement.common.response.HandlerResult;

public interface IAchievementCommandHandler {
    
    public HandlerResult<Achievement> handle(CreateAchivementCommand command) ;
}
