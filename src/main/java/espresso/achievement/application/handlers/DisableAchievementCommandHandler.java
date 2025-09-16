package espresso.achievement.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IDisableAchievementCommandHandler;
import espresso.achievement.domain.commands.DisableAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;

/**
 * Handles the command to disable an existing achievement.
 */
@Service
public class DisableAchievementCommandHandler implements IDisableAchievementCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    /**
     * Handles the command to disable an existing achievement.
     * Validates the command, verifies user and achievement exist, disables the
     * achievement,
     * and persists the changes to the repository.
     * 
     * @param cmd The command containing the achievement key and user key
     * @return HandlerResponse with the disabled achievement or error details
     */
    public HandlerResponse<Object> handle(DisableAchievementCommand cmd) {
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
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DISABLE THIS ACHIEVEMENT",
                        ResponseType.UNAUTHORIZED);
            }

            // Check if achievement is already disabled
            if (!achievement.isEnabled()) {
                return HandlerResponse.noContent();
            }

            // Call disable method on achievement to update the entity
            achievement.disable();

            // Save updated achievement via achievementRepository.update (since we
            // modified the entity)
            Achievement disabledAchievement = achievementRepository.update(achievement);

            return HandlerResponse.success(disabledAchievement);

        } catch (Exception ex) {
            return HandlerResponse.error("LOCALIZE: " + ex.getMessage().toUpperCase(), ResponseType.INTERNAL_ERROR);
        }
    }
}
