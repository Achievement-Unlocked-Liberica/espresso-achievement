package espresso.achievement.cmd.domain.contracts;

import espresso.achievement.cmd.application.commandHandlers.CreateAchivementCommand;

public interface IAchievementCommandHandler {
    
    public void handle(CreateAchivementCommand command) ;
}
