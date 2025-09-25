package espresso.achievement.application.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IAddAchievementCelebrationCommandHandler;
import espresso.achievement.domain.commands.AddAchievementCelebrationCommand;
import espresso.achievement.domain.contracts.IAchievementCelebrationRepository;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.entities.AchievementCelebration;
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
 * Handles the command to add a celebration to an existing achievement.
 */
@Slf4j
@Service
public class AddAchievementCelebrationCommandHandler extends CommonCommandHandler
        implements IAddAchievementCelebrationCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAchievementCelebrationRepository celebrationRepository;

    /**
     * Centralized exception handling policy.
     */
    @Autowired
    private AchievementHandlerExceptionPolicy exceptionPolicy;

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
            var validationResult = validateCommand(cmd);

            if (validationResult != null)
                return validationResult;

            // Get the profile of the user that is creating the achievemnet
            // We use a Kto instance since we just need to know if it exists and its keys,
            UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Verify achievement exists
            Achievement achievement = achievementRepository.getAchievementByKey(
                    Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Create the celebration
            AchievementCelebration celebration = AchievementCelebration.create(
                    cmd.getCount(),
                    achievement,
                    User.fromKto(userKto));

            // Add celebration to achievement (this will raise domain events)
            achievement.addCelebration(celebration);

            AchievementCelebration savedCelebration = celebrationRepository.save(celebration);

            this.publishDomainEvents(achievement);

            // Return success response
            return HandlerResponse.success(savedCelebration);

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "add celebration to achievement");
        }
    }
}
