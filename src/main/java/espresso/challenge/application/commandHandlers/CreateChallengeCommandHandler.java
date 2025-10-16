package espresso.challenge.application.commandHandlers;

import java.util.Arrays;
import org.springframework.stereotype.Service;

import espresso.challenge.domain.contracts.ICreateChallengeCommandHandler;
import espresso.challenge.domain.commands.CreateChallengeCommand;
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
 * Handles the creation of new challenges.
 * 
 * This handler validates the provided {@link CreateChallengeCommand}, retrieves the user
 * associated with the command, creates a new challenge entity, and saves it to the repository.
 */
@Slf4j
@Service
public class CreateChallengeCommandHandler extends CommonCommandHandler implements ICreateChallengeCommandHandler {

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
    public CreateChallengeCommandHandler(
            IChallengeRepository challengeRepository,
            IUserRepository userRepository,
            ChallengeHandlerExceptionPolicy exceptionPolicy) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    /**
     * Processes the challenge creation command.
     * 
     * @param cmd The command containing challenge creation data
     * @return HandlerResponse with the created challenge or error information
     */
    public HandlerResponse<Object> handle(CreateChallengeCommand cmd) {

        try {
            var validationResult = validateCommand(cmd);
            if (validationResult != null)
                return validationResult;

            // Get the profile of the user that is creating the challenge
            UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            Challenge challenge = Challenge.create(
                    cmd.getTitle(),
                    cmd.getDescription(),
                    cmd.getFulfillmentDate(),
                    cmd.getIsPublic(),
                    User.fromKto(userKto),
                    Arrays.asList(cmd.getSkills()));

            Challenge savedChallenge = challengeRepository.save(challenge);

            // We don't need to call 'publish events' explicitly,
            // the JPA call to save the entity will take care of the event publishing

            return HandlerResponse.created(savedChallenge.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "create challenge");
        }
    }
}
