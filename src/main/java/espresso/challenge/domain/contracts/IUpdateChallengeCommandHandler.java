package espresso.challenge.domain.contracts;

import espresso.challenge.domain.commands.UpdateChallengeCommand;
import espresso.common.domain.responses.HandlerResponse;

/**
 * Handler interface for processing challenge update commands.
 * Responsible for validating update data and modifying existing challenges.
 */
public interface IUpdateChallengeCommandHandler {
    
    /**
     * Processes a command to update an existing challenge.
     * Validates the command, retrieves the challenge, updates its properties, and persists changes.
     * 
     * @param command The command containing challenge update data
     * @return HandlerResponse indicating success or failure with appropriate messages
     */
    HandlerResponse<Object> handle(UpdateChallengeCommand command);
}
