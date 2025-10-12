package espresso.challenge.application.commandHandlers;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

import espresso.challenge.domain.contracts.IUpdateChallengeCommandHandler;
import espresso.challenge.domain.commands.UpdateChallengeCommand;
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
 * Handles the command to update an existing challenge.
 */
@Slf4j
@Service
public class UpdateChallengeCommandHandler extends CommonCommandHandler implements IUpdateChallengeCommandHandler {

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
    public UpdateChallengeCommandHandler(
            IChallengeRepository challengeRepository,
            IUserRepository userRepository,
            ChallengeHandlerExceptionPolicy exceptionPolicy) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    /**
     * Handles the command to update an existing challenge.
     * Validates the command, verifies user and challenge exist, updates the
     * challenge,
     * and persists the changes to the repository.
     * 
     * @param cmd The command containing updated challenge data
     * @return HandlerResponse with the updated challenge or error details
     */
    public HandlerResponse<Object> handle(UpdateChallengeCommand cmd) {
        try {
            // Validate the command using shared Validator and command-specific checks
            var validationResult = validateCommand(cmd);
            if (validationResult != null)
                return validationResult;

            // Get the profile of the user that is updating the challenge
            // We use a Kto instance since we just need to know if it exists and its keys
            UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Retrieve challenge by challengeKey - throw not found error if missing
            // We can't use a Kto instance here, since we need the domain aggregate for its behavior
            Challenge challenge = challengeRepository.getChallengeByKey(
                    Challenge.class,
                    cmd.getChallengeKey());

            if (challenge == null) {
                return HandlerResponse.error("LOCALIZE: CHALLENGE NOT FOUND", ResponseType.NOT_FOUND);
            }

            // Verify that the user is authorized to update this challenge (user must own the challenge)
            if (!challenge.isCreator(User.fromKto(userKto))) {
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO UPDATE THIS CHALLENGE",
                        ResponseType.UNAUTHORIZED);
            }

            // Convert skills array to list for the update method
            List<String> skillsList = Arrays.asList(cmd.getSkills());

            // Call update method on challenge with command properties
            challenge.update(
                    cmd.getTitle(),
                    cmd.getDescription(),
                    skillsList,
                    cmd.getIsPublic());

            // Save updated challenge via challengeRepository.update
            challengeRepository.update(challenge);

            return HandlerResponse.success(challenge.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "update challenge");
        }
    }
}
