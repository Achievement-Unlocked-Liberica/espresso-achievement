package espresso.achievement.application.handlers;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IUpdateAchievementCommandHandler;
import espresso.achievement.domain.commands.UpdateAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;

/**
 * Handles the command to update an existing achievement.
 */
@Service
public class UpdateAchievementCommandHandler implements IUpdateAchievementCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    /**
     * Handles the command to update an existing achievement.
     * Validates the command, verifies user and achievement exist, updates the
     * achievement,
     * and persists the changes to the repository.
     * 
     * @param cmd The command containing updated achievement data
     * @return HandlerResponse with the updated achievement or error details
     */
    public HandlerResponse<Object> handle(UpdateAchievementCommand cmd) {
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

            // Retrieve achievement by achievementKey - throw not found error if missing
            Achievement achievement = achievementRepository.getAchievementByKey(
                    Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.error("LOCALIZE: ACHIEVEMENT NOT FOUND", ResponseType.NOT_FOUND);
            }

            // Verify that the user owns the achievement
            if (!achievement.getUser().getEntityKey().equals(cmd.getUserKey())) {
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO UPDATE THIS ACHIEVEMENT",
                        ResponseType.UNAUTHORIZED);
            }

            // Convert skills array to list for the update method
            List<String> skillsList = Arrays.asList(cmd.getSkills());

            // Call update method on achievement with command properties
            achievement.update(
                    cmd.getTitle(),
                    cmd.getDescription(),
                    skillsList,
                    cmd.getIsPublic());

            // Save updated achievement via achievementRepository.update
            Achievement updatedAchievement = achievementRepository.update(achievement);

            return HandlerResponse.success(updatedAchievement);

        } catch (Exception ex) {
            return HandlerResponse.error("LOCALIZE: " + ex.getMessage().toUpperCase(), ResponseType.INTERNAL_ERROR);
        }
    }
}
