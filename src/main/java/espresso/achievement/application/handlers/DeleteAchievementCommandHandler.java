package espresso.achievement.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IDeleteAchievementCommandHandler;
import espresso.achievement.domain.commands.DeleteAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;

/**
 * Handles the command to delete an existing achievement.
 */
@Service
public class DeleteAchievementCommandHandler implements IDeleteAchievementCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    /**
     * Handles the command to delete an existing achievement.
     * Validates the command, verifies user and achievement exist, checks
     * authorization,
     * and permanently deletes the achievement and all its dependencies.
     * 
     * @param cmd The command containing the achievement key and user key
     * @return HandlerResponse with success confirmation or error details
     */
    public HandlerResponse<Object> handle(DeleteAchievementCommand cmd) {
        try {
            // Validate the command
            var validationErrors = cmd.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Retrieve user by userKey - throw not found error if missing
            User user = userRepository.findByKey(cmd.getUserKey(), User.class);
            if (user == null) {
                return HandlerResponse.error("LOCALIZE: USER NOT FOUND", ResponseType.NOT_FOUND);
            }

            // Retrieve achievement by achievementKey - return No Content if missing (per
            // prompt requirement)
            Achievement achievement = achievementRepository.getAchievementByKey(
                    Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.noContent();
            }

            // Verify that the user is authorized to delete this achievement (user must own
            // the achievement)
            if (!achievement.getUser().getEntityKey().equals(cmd.getUserKey())) {
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT",
                        ResponseType.UNAUTHORIZED);
            }

            // Delete the achievement and all its dependencies in proper order
            achievementRepository.deleteWithDependencies(achievement);

            // Return success confirmation (HTTP 200 OK with no content data)
            return HandlerResponse.success(null);

        } catch (Exception ex) {
            return HandlerResponse.error("LOCALIZE: " + ex.getMessage().toUpperCase(), ResponseType.INTERNAL_ERROR);
        }
    }
}
