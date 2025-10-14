package espresso.challenge.application.commandHandlers;

import org.springframework.stereotype.Service;

import espresso.challenge.domain.contracts.IAddChallengeEncouragementCommandHandler;
import espresso.challenge.domain.commands.AddChallengeEncouragementCommand;
import espresso.challenge.domain.contracts.IChallengeEncouragementRepository;
import espresso.challenge.domain.contracts.IChallengeRepository;
import espresso.challenge.domain.entities.ChallengeEncouragement;
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
 * Handles the command to add an encouragement to an existing challenge.
 */
@Slf4j
@Service
public class AddChallengeEncouragementCommandHandler extends CommonCommandHandler
        implements IAddChallengeEncouragementCommandHandler {

    private final IChallengeRepository challengeRepository;
    private final IUserRepository userRepository;
    private final IChallengeEncouragementRepository encouragementRepository;
    private final ChallengeHandlerExceptionPolicy exceptionPolicy;

    /**
     * Constructor for dependency injection.
     * 
     * @param challengeRepository Repository for challenge entity persistence operations
     * @param userRepository Repository for user entity queries and operations
     * @param encouragementRepository Repository for challenge encouragement operations
     * @param exceptionPolicy Centralized exception handling policy
     */
    public AddChallengeEncouragementCommandHandler(
            IChallengeRepository challengeRepository,
            IUserRepository userRepository,
            IChallengeEncouragementRepository encouragementRepository,
            ChallengeHandlerExceptionPolicy exceptionPolicy) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.encouragementRepository = encouragementRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    /**
     * Handles the command to add an encouragement to an existing challenge.
     * Validates the command, verifies dependent entities exist, creates the encouragement,
     * adds it to the challenge, saves to repository, and emits to message queue.
     * 
     * @param cmd The command containing encouragement data
     * @return HandlerResponse with success status or error details
     */
    public HandlerResponse<Object> handle(AddChallengeEncouragementCommand cmd) {
        try {
            // Validate the command using shared Validator and command-specific checks
            var validationResult = validateCommand(cmd);

            if (validationResult != null)
                return validationResult;

            // Get the profile of the user that is giving the encouragement
            // We use a Kto instance since we just need to know if it exists and its keys
            UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Verify challenge exists
            Challenge challenge = challengeRepository.getChallengeByKey(
                    Challenge.class,
                    cmd.getChallengeKey());

            if (challenge == null) {
                return HandlerResponse.error("Challenge not found", ResponseType.NOT_FOUND);
            }

            // Create the encouragement
            ChallengeEncouragement encouragement = ChallengeEncouragement.create(
                    cmd.getCount(),
                    challenge,
                    User.fromKto(userKto));

            // Add encouragement to challenge (this will raise domain events)
            challenge.addEncouragement(encouragement);

            encouragementRepository.save(encouragement);

            this.publishDomainEvents(challenge);

            // Return success response
            return HandlerResponse.success(challenge.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "add encouragement to challenge");
        }
    }
}
