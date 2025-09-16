package espresso.achievement.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IAddAchievementCelebrationCommandHandler;
import espresso.achievement.domain.commands.AddAchievementCelebrationCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.contracts.IAchievementCelebrationRepository;
import espresso.achievement.domain.entities.AchievementCelebration;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.application.handlers.CommonCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;

/**
 * Handles the command to add a celebration to an existing achievement.
 */
@Service
public class AddAchievementCelebrationCommandHandler extends CommonCommandHandler implements IAddAchievementCelebrationCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAchievementCelebrationRepository achievementCelebrationRepository;

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
            // Validate the command
            var validationErrors = cmd.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

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
            AchievementCelebration celebration = AchievementCelebration.create(
                    cmd.getCount(),
                    achievement,
                    user);

            // Add celebration to achievement (this will raise domain events)
            achievement.addCelebration(celebration);

            // Save the celebration, only trigger JPA to cause the event to be emitted
            //achievementCelebrationRepository.save(celebration);

            // Emit to message queue for downstream processing
            //achievementCelebrationRepository.emit(celebration);

            this.publishDomainEvents(achievement);

            // Return success response
            return HandlerResponse.created(celebration);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
