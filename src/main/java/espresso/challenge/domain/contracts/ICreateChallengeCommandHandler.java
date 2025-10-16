package espresso.challenge.domain.contracts;

import espresso.challenge.domain.commands.CreateChallengeCommand;
import espresso.common.domain.responses.HandlerResponse;

/**
 * Handler interface for processing challenge creation commands.
 * Responsible for validating command data and orchestrating challenge creation.
 */
public interface ICreateChallengeCommandHandler {
    
    /**
     * Processes a command to create a new challenge.
     * Validates the command, creates the challenge entity, and persists it to the repository.
     * 
     * @param cmd The command containing challenge creation data
     * @return HandlerResponse indicating success or failure with appropriate messages
     */
    HandlerResponse<Object> handle(CreateChallengeCommand cmd);
}
