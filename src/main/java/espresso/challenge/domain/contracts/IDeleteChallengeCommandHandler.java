package espresso.challenge.domain.contracts;

import espresso.challenge.domain.commands.DeleteChallengeCommand;
import espresso.common.domain.responses.HandlerResponse;

/**
 * Handler interface for processing challenge delete commands.
 * Responsible for validating delete requests and permanently removing challenges
 * and all their associated data.
 */
public interface IDeleteChallengeCommandHandler {
    
    /**
     * Processes a command to delete an existing challenge.
     * Validates the command, retrieves the challenge, and permanently removes it
     * along with all dependencies (comments, media, etc.) in proper order.
     * 
     * @param command The command containing challenge delete data
     * @return HandlerResponse indicating success or failure with appropriate messages
     */
    HandlerResponse<Object> handle(DeleteChallengeCommand command);
}
