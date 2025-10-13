package espresso.challenge.application.commandHandlers;

import org.springframework.stereotype.Service;

import espresso.challenge.domain.contracts.IDisableChallengeCommandHandler;
import espresso.challenge.domain.commands.DisableChallengeCommand;
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
 * Handles the command to disable an existing challenge.
 */
@Slf4j
@Service
public class DisableChallengeCommandHandler extends CommonCommandHandler
        implements IDisableChallengeCommandHandler {

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
    public DisableChallengeCommandHandler(
            IChallengeRepository challengeRepository,
            IUserRepository userRepository,
            ChallengeHandlerExceptionPolicy exceptionPolicy) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    /**
     * Handles the command to disable an existing challenge.
     * Validates the command, verifies user and challenge exist, disables the
     * challenge,
     * and persists the changes to the repository.
     * 
     * @param cmd The command containing the challenge key and user key
     * @return HandlerResponse with the disabled challenge or error details
     */
    public HandlerResponse<Object> handle(DisableChallengeCommand cmd) {
        try {
            // Validate the command using shared Validator and command-specific checks
            var validationResult = validateCommand(cmd);
            if (validationResult != null)
                return validationResult;

            // Get the profile of the user that is disabling the challenge
            // We use a Kto instance since we just need to know if it exists and its keys
            UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Retrieve challenge by challengeKey - throw not found error if missing
            Challenge challenge = challengeRepository.getChallengeByKey(
                    Challenge.class,
                    cmd.getChallengeKey());

            if (challenge == null) {
                return HandlerResponse.error("LOCALIZE: CHALLENGE NOT FOUND", ResponseType.NOT_FOUND);
            }

            // Verify that the user is authorized to disable this challenge (user must own
            // the challenge)
            if (!challenge.isCreator(User.fromKto(userKto))) {
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DISABLE THIS CHALLENGE",
                        ResponseType.UNAUTHORIZED);
            }

            // Check if challenge is already disabled
            if (!challenge.isEnabled()) {
                return HandlerResponse.noContent();
            }

            // Call disable method on challenge to update the entity
            challenge.disable();

            // Save updated challenge via challengeRepository.update (since we
            // modified the entity)
            challengeRepository.update(challenge);

            return HandlerResponse.success(challenge.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "disable challenge");
        }
    }
}
