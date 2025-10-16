package espresso.challenge.application.commandHandlers;

import org.springframework.stereotype.Service;

import espresso.challenge.domain.contracts.IDeleteChallengeCommandHandler;
import espresso.challenge.domain.commands.DeleteChallengeCommand;
import espresso.challenge.domain.contracts.IChallengeRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.user.domain.entities.UserKto;
import espresso.challenge.domain.entities.Challenge;
import espresso.common.application.handlers.CommonCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.challenge.domain.operational.exceptionPolicy.ChallengeHandlerExceptionPolicy;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles the command to delete an existing challenge.
 */
@Slf4j
@Service
public class DeleteChallengeCommandHandler extends CommonCommandHandler implements IDeleteChallengeCommandHandler {

    private final IChallengeRepository challengeRepository;
    private final IUserRepository userRepository;
    private final ChallengeHandlerExceptionPolicy exceptionPolicy;

    /**
     * Constructor for dependency injection.
     * 
     * @param challengeRepository Repository for challenge entity persistence operations
     * @param userRepository Repository for user entity queries and operations
     * @param exceptionPolicy Centralized exception handling policy
     */
    public DeleteChallengeCommandHandler(
            IChallengeRepository challengeRepository,
            IUserRepository userRepository,
            ChallengeHandlerExceptionPolicy exceptionPolicy) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    /**
     * Handles the command to delete an existing challenge.
     * Validates the command, verifies user and challenge exist, checks
     * authorization,
     * and permanently deletes the challenge and all its dependencies.
     * 
     * @param cmd The command containing the challenge key and user key
     * @return HandlerResponse with success confirmation or error details
     */
    public HandlerResponse<Object> handle(DeleteChallengeCommand cmd) {
        try {
            // Validate the command using shared Validator and command-specific checks
            var validationResult = validateCommand(cmd);
            if (validationResult != null) 
                return validationResult;

            // Get the profile of the user that is deleting the challenge
            // We use a Kto instance since we just need to know if it exists and its keys
            UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Retrieve challenge by challengeKey - return No Content if missing (per
            // achievement pattern)
            Challenge challenge = challengeRepository.getChallengeByKey(
                    Challenge.class,
                    cmd.getChallengeKey());

            if (challenge == null) {
                return HandlerResponse.noContent();
            }

            // Verify that the user is authorized to delete this challenge (user must own the challenge)
            if (!challenge.isCreator(User.fromKto(userKto))) {
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS CHALLENGE",
                        ResponseType.UNAUTHORIZED);
            }

            // Call delete method on challenge to raise domain events before deletion
            challenge.delete();

            // Delete the challenge and all its dependencies in proper order
            challengeRepository.deleteWithDependencies(challenge);

            // Return success confirmation (HTTP 200 OK with entity key data)
            return HandlerResponse.success(challenge.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "delete challenge");
        }
    }
}
