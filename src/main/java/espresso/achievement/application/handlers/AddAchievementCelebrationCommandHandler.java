package espresso.achievement.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IAddAchievementCelebrationCommandHandler;
import espresso.achievement.domain.commands.AddAchievementCelebrationCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.entities.AchievementCelebration;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.application.handlers.CommonCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
// Validation centralized in CommonCommandHandler

/**
 * Handles the command to add a celebration to an existing achievement.
 */
@Service
public class AddAchievementCelebrationCommandHandler extends CommonCommandHandler implements IAddAchievementCelebrationCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    // Removed unused achievementCelebrationRepository (not needed after refactor)

    // validator provided by base class

    /**
     * Handles the command to add a celebration to an existing achievement.
     * Validates the command, verifies dependent entities exist, creates the
     * celebration,
     * adds it to the achievement, saves to repository, and emits to message queue.
     * 
     * @param cmd The command containing celebration data
     * @return HandlerResponse with success status or error details
     */
    public HandlerResponse<Object> handle(AddAchievementCelebrationCommand cmd) {
        try {
            // Validate the command using shared Validator and command-specific checks
            var invalid = validateCommand(cmd);
            if (invalid != null) return invalid;

            // Verify achievement exists
            Achievement achievement = achievementRepository.getAchievementByKey(Achievement.class,
                    cmd.getAchievementKey());
            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Verify user exists
            User user = userRepository.findByKey(cmd.getUserKey(), User.class);
            if (user == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Create the celebration
            AchievementCelebration entity = AchievementCelebration.create(
                    cmd.getCount(),
                    achievement,
                    user);

            // Add celebration to achievement (this will raise domain events)
            achievement.addCelebration(entity);

            // Save the celebration, only trigger JPA to cause the event to be emitted
            //achievementCelebrationRepository.save(celebration);

            // We do need to call 'publish events' explicitly, 
            // there is no 'save' operation as part of this handler, the celebration will be saved by the aggregator
            this.publishDomainEvents(achievement);

            // Return success response
            return HandlerResponse.created(entity);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
