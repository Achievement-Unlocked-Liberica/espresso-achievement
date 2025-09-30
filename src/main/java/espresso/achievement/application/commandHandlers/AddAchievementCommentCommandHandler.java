package espresso.achievement.application.commandHandlers;

import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IAddAchievementCommentCommandHandler;
import espresso.achievement.domain.contracts.IAchievementCommentRepository;
import espresso.achievement.domain.commands.AddAchievementCommentCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.entities.AchievementComment;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.user.domain.entities.UserKto;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.application.handlers.CommonCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.achievement.domain.operational.exceptionPolicy.AchievementHandlerExceptionPolicy;

import lombok.extern.slf4j.Slf4j;
// Validation centralized in CommonCommandHandler

/**
 * Handles the command to add a new comment to an achievement.
 */
@Slf4j
@Service
public class AddAchievementCommentCommandHandler extends CommonCommandHandler
        implements IAddAchievementCommentCommandHandler {

    private final IAchievementRepository achievementRepository;
    private final IUserRepository userRepository;
    private final IAchievementCommentRepository achievementCommentRepository;
    private final AchievementHandlerExceptionPolicy exceptionPolicy;

    /**
     * Constructor for dependency injection.
     * 
     * @param achievementRepository Repository for achievement entity persistence operations
     * @param userRepository Repository for user entity queries and operations
     * @param achievementCommentRepository Repository for achievement comment operations
     * @param exceptionPolicy Centralized exception handling policy
     */
    public AddAchievementCommentCommandHandler(
            IAchievementRepository achievementRepository,
            IUserRepository userRepository,
            IAchievementCommentRepository achievementCommentRepository,
            AchievementHandlerExceptionPolicy exceptionPolicy) {
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
        this.achievementCommentRepository = achievementCommentRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    // validator provided by base class

    /**
     * Handles the command to add a new comment to an achievement.
     * Delegates to the dedicated comment command handler for processing.
     * 
     * @param cmd The command containing comment data
     * @return HandlerResponse with the created comment or error details
     */
    public HandlerResponse<Object> handle(AddAchievementCommentCommand cmd) {
        try {
            // Validate the command using shared Validator and command-specific checks
            var validationResult = validateCommand(cmd);
            if (validationResult != null)
                return validationResult;

                // Get the profile of the user that is creating the achievemnet
            // We use a Kto instance since we just need to know if it exists and its keys,
            UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Verify the achievement exists
            Achievement achievement = achievementRepository.getAchievementByKey(
                    Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Create a new achievement comment using the domain model's create operation
            AchievementComment comment = AchievementComment.create(
                    cmd.getCommentText(),
                    achievement,
                    User.fromKto(userKto));

            achievement.addComment(comment);

            // Save the comment through the repository
            achievementCommentRepository.save(comment);

            this.publishDomainEvents(achievement);

            // Return success response with the created comment
            return HandlerResponse.success(achievement.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "add comment to achievement");
        }
    }
}
