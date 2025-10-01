package espresso.achievement.domain.contracts;

import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.common.domain.responses.HandlerResponse;

/**
 * Handler interface for processing achievement creation commands.
 * Responsible for validating command data and orchestrating achievement creation.
 */
public interface ICreateAchivementCommandHandler {
    
    /**
     * Processes a command to create a new achievement.
     * Validates the command, creates the achievement entity, and persists it to the repository.
     * 
     * @param cmd The command containing achievement creation data
     * @return HandlerResponse indicating success or failure with appropriate messages
     */
    HandlerResponse<Object> handle(CreateAchivementCommand cmd);
}
