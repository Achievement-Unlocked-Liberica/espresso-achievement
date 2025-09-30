package espresso.achievement.application.commandHandlers;

import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IDeleteAchievementCommandHandler;
import espresso.achievement.domain.commands.DeleteAchievementCommand;
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
// Validation centralized in CommonCommandHandler

/**
 * Handles the command to delete an existing achievement.
 */
@Slf4j
@Service
public class DeleteAchievementCommandHandler extends CommonCommandHandler implements IDeleteAchievementCommandHandler {

    private final IAchievementRepository achievementRepository;
    private final IUserRepository userRepository;
    private final AchievementHandlerExceptionPolicy exceptionPolicy;

    /**
     * Constructor for dependency injection.
     * 
     * @param achievementRepository Repository for achievement entity persistence operations
     * @param userRepository Repository for user entity queries and operations
     * @param exceptionPolicy Centralized exception handling policy
     */
    public DeleteAchievementCommandHandler(
            IAchievementRepository achievementRepository,
            IUserRepository userRepository,
            AchievementHandlerExceptionPolicy exceptionPolicy) {
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    // validator provided by base class

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

            // Retrieve achievement by achievementKey - return No Content if missing (per
            // prompt requirement)
            Achievement achievement = achievementRepository.getAchievementByKey(
                    Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.noContent();
            }

            // Verify that the user is authorized to delete this achievement (user must own the achievement)
            if (!achievement.isCreator(User.fromKto(userKto))) {
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT",
                        ResponseType.UNAUTHORIZED);
            }

            // Call delete method on achievement to raise domain events before deletion
            achievement.delete();

            // Delete the achievement and all its dependencies in proper order
            achievementRepository.deleteWithDependencies(achievement);

            // Return success confirmation (HTTP 200 OK with no content data)
            return HandlerResponse.success(achievement.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "delete achievement");
        }
    }
}
