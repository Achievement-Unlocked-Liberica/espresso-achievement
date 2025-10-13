package espresso.challenge.domain.commandhandlers;

import espresso.challenge.domain.commands.AddChallengeCommentCommand;
import espresso.challenge.domain.entities.ChallengeComment;

/**
 * Interface for handling AddChallengeCommentCommand.
 * Processes the command to add a comment to a challenge.
 */
public interface IAddChallengeCommentCommandHandler {
    
    /**
     * Executes the command to add a comment to a challenge.
     * Validates the command, looks up the user and challenge,
     * creates the comment, adds it to the challenge, saves it,
     * and publishes relevant domain events.
     * 
     * @param command The command containing comment details
     * @return The created ChallengeComment entity
     * @throws espresso.common.infrastructure.exceptions.ValidationException if command validation fails
     * @throws espresso.common.infrastructure.exceptions.ResourceNotFoundException if user or challenge not found
     * @throws espresso.common.infrastructure.exceptions.BusinessRuleException if business rules are violated
     */
    ChallengeComment execute(AddChallengeCommentCommand command);
}
