package espresso.challenge.domain.contracts;

import espresso.challenge.domain.commands.DisableChallengeCommand;
import espresso.common.domain.responses.HandlerResponse;

/**
 * Handler interface for processing challenge disable commands.
 * Responsible for validating disable requests and disabling existing challenges.
 */
public interface IDisableChallengeCommandHandler {
    
    /**
     * Processes a command to disable an existing challenge.
     * Validates the command, retrieves the challenge, and sets its enabled property to false.
     * 
     * @param command The command containing challenge disable data
     * @return HandlerResponse indicating success or failure with appropriate messages
     */
    HandlerResponse<Object> handle(DisableChallengeCommand command);
}
