package espresso.challenge.infrastructure.commandhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import espresso.challenge.domain.commandhandlers.IAddChallengeCommentCommandHandler;
import espresso.challenge.domain.commands.AddChallengeCommentCommand;
import espresso.challenge.domain.contracts.IChallengeCommentRepository;
import espresso.challenge.domain.contracts.IChallengeRepository;
import espresso.challenge.domain.entities.Challenge;
import espresso.challenge.domain.entities.ChallengeComment;
import espresso.challenge.domain.operational.exceptionPolicy.ChallengeHandlerExceptionPolicy;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.user.domain.entities.UserKto;
import lombok.RequiredArgsConstructor;

/**
 * Handler implementation for processing AddChallengeCommentCommand.
 * Coordinates the workflow of adding a comment to a challenge including validation,
 * entity retrieval, comment creation, persistence, and event publishing.
 */
@Service
@RequiredArgsConstructor
public class AddChallengeCommentCommandHandler implements IAddChallengeCommentCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(AddChallengeCommentCommandHandler.class);

    private final IChallengeRepository challengeRepository;
    private final IUserRepository userRepository;
    private final IChallengeCommentRepository challengeCommentRepository;
    private final ChallengeHandlerExceptionPolicy exceptionPolicy;

    /**
     * Executes the command to add a comment to a challenge.
     * 
     * @param command The command containing comment details
     * @return The created ChallengeComment entity
     */
    @Override
    @Transactional
    public ChallengeComment execute(AddChallengeCommentCommand command) {
        logger.info("Executing AddChallengeCommentCommand for challengeKey={}, userKey={}", 
                    command.getChallengeKey(), command.getUserKey());

        // Step 1: Look up the user using KTO for lightweight reference
        UserKto userKto = userRepository.findByKey(command.getUserKey(), UserKto.class);
        if (userKto == null) {
            logger.error("User not found with userKey={}", command.getUserKey());
            throw new IllegalArgumentException("User not found");
        }

        // Step 2: Look up the challenge
        Challenge challenge = challengeRepository.getChallengeByKey(
                Challenge.class,
                command.getChallengeKey());
        if (challenge == null) {
            logger.error("Challenge not found with challengeKey={}", command.getChallengeKey());
            throw new IllegalArgumentException("Challenge not found");
        }

        // Step 3: Create the comment entity using fromKto pattern
        ChallengeComment comment = ChallengeComment.create(
            command.getCommentText(),
            challenge,
            User.fromKto(userKto)
        );

        // Step 4: Add the comment to the challenge (raises domain events)
        challenge.addComment(comment);

        // Step 5: Save the comment
        ChallengeComment savedComment = challengeCommentRepository.save(comment);

        // Step 6: Save the challenge (to persist the updated comments collection)
        challengeRepository.save(challenge);

        logger.info("Successfully added comment id={} to challengeKey={} by userKey={}", 
                    savedComment.getId(), command.getChallengeKey(), command.getUserKey());

        return savedComment;
    }
}
