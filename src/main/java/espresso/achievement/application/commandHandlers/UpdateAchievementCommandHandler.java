package espresso.achievement.application.commandHandlers;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IUpdateAchievementCommandHandler;
import espresso.achievement.domain.commands.UpdateAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.user.domain.entities.UserKto;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.application.handlers.CommonCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.achievement.domain.operational.exceptionPolicy.AchievementHandlerExceptionPolicy;

import lombok.extern.slf4j.Slf4j;
// Validation now handled by CommonCommandHandler

/**
 * Handles the command to update an existing achievement.
 */
@Slf4j
@Service
public class UpdateAchievementCommandHandler extends CommonCommandHandler implements IUpdateAchievementCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    /**
     * Centralized exception handling policy.
     */
    @Autowired
    private AchievementHandlerExceptionPolicy exceptionPolicy;

    // validator provided by base class

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

            // Retrieve achievement by achievementKey - throw not found error if missing
            // We can't use a Kto instance here, since we need the domain aggregate for its
            // behavior
            Achievement achievement = achievementRepository.getAchievementByKey(
                    Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.error("LOCALIZE: ACHIEVEMENT NOT FOUND", ResponseType.NOT_FOUND);
            }

            // Verify that the user is authorized to delete this achievement (user must own
            // the achievement)
            if (!achievement.isCreator(User.fromKto(userKto))) {
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT",
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
            achievementRepository.update(achievement);

            return HandlerResponse.success(achievement.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "update achievement");
        }
    }
}
