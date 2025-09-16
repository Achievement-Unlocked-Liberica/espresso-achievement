package espresso.achievement.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IAddAchievementCommentCommandHandler;
import espresso.achievement.domain.contracts.IAchievementCommentRepository;
import espresso.achievement.domain.commands.AddAchievementCommentCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.entities.AchievementComment;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;

/**
 * Handles the command to add a new comment to an achievement.
 */
@Service
public class AddAchievementCommentCommandHandler implements IAddAchievementCommentCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAchievementCommentRepository achievementCommentRepository;

    /**
     * Handles the command to add a new comment to an achievement.
     * Delegates to the dedicated comment command handler for processing.
     * 
     * @param cmd The command containing comment data
     * @return HandlerResponse with the created comment or error details
     */
    public HandlerResponse<Object> handle(AddAchievementCommentCommand cmd) {
        try {
            // Validate the command
            var validationErrors = cmd.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Verify the achievement exists
            Achievement achievement = achievementRepository.getAchievementByKey(
                    Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Verify the user exists
            User user = userRepository.findByKey(cmd.getUserKey(), User.class);

            if (user == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Create a new achievement comment using the domain model's create operation
            AchievementComment comment = AchievementComment.create(
                    cmd.getCommentText(),
                    achievement,
                    user);

            // Save the comment through the repository
            AchievementComment savedComment = achievementCommentRepository.save(comment);

            // Return success response with the created comment
            return HandlerResponse.created(savedComment);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
