package espresso.achievement.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.commands.AddAchievementCommentCommand;
import espresso.achievement.domain.contracts.IAchievementCommentRepository;
import espresso.achievement.domain.contracts.IAchievementQryRepository;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementComment;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

/**
 * Command handler for processing AddAchievementCommentCommand requests.
 * Handles the business logic for creating new achievement comments.
 */
@Service
public class AchievementCommentCommandHandler {

    @Autowired
    private IAchievementCommentRepository achievementCommentRepository;

    @Autowired
    private IAchievementQryRepository achievementQryRepository;

    @Autowired
    private IUserRepository userRepository;

    /**
     * Handles the command to add a new comment to an achievement.
     * Validates the command, verifies dependent entities exist, creates the comment,
     * and persists it to the repository.
     * 
     * @param command The command containing comment data
     * @return HandlerResponse with the created comment or error details
     */
    public HandlerResponse<Object> handle(AddAchievementCommentCommand command) {
        try {
            // Validate the command
            var validationErrors = command.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Verify the achievement exists
            Achievement achievement = achievementQryRepository.getAchievementByKey(
                Achievement.class, 
                command.getAchievementKey()
            );

            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Verify the user exists
            User user = userRepository.findByKey(command.getUserKey(), User.class);

            if (user == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Create a new achievement comment using the domain model's create operation
            AchievementComment comment = AchievementComment.create(
                command.getCommentText(),
                achievement,
                user
            );

            // Save the comment through the repository
            AchievementComment savedComment = achievementCommentRepository.save(comment);

            // Return success response with the created comment
            return HandlerResponse.created(savedComment);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
