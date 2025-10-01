package espresso.achievement.domain.contracts;

import espresso.achievement.domain.commands.UpdateAchievementCommand;
import espresso.common.domain.responses.HandlerResponse;

/**
 * Handler interface for processing achievement update commands.
 * Responsible for validating update data and modifying existing achievements.
 */
public interface IUpdateAchievementCommandHandler {
    
    /**
     * Processes a command to update an existing achievement.
     * Validates the command, retrieves the achievement, updates its properties, and persists changes.
     * 
     * @param command The command containing achievement update data
     * @return HandlerResponse indicating success or failure with appropriate messages
     */
    HandlerResponse<Object> handle(UpdateAchievementCommand command);
}
