package espresso.achievement.application.commandHandlers;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.ICreateAchivementCommandHandler;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.contracts.IContentSafetyAIService;
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
 * Handles the creation of new achievements.
 * 
 * This handler validates the provided {@link CreateAchivementCommand}, retrieves the user
 * associated with the command, creates a new achievement entity, and saves it to the repository.
 * The handler also includes placeholder logic for content safety verification of the achievement
 * title and description using AI services.
 */
@Slf4j
@Service
public class CreateAchivementCommandHandler extends CommonCommandHandler implements ICreateAchivementCommandHandler {

    /**
     * Repository for achievement entity persistence operations.
     */
    @Autowired
    private IAchievementRepository achievementRepository;

    /**
     * Repository for user entity queries and operations.
     */
    @Autowired
    private IUserRepository userRepository;

    /**
     * Service for content safety verification using AI.
     */    
    // @Autowired
    // private IContentSafetyAIService contentSafetyAIService;

    /**
     * Centralized exception handling policy.
     */
    @Autowired
    private AchievementHandlerExceptionPolicy exceptionPolicy;

    /**
     * Processes the achievement creation command.
     * 
     * @param cmd The command containing achievement creation data
     * @return HandlerResponse with the created achievement or error information
     */
    public HandlerResponse<Object> handle(CreateAchivementCommand cmd) {

        try {
            var validationResult = validateCommand(cmd);
            if (validationResult != null)
                return validationResult;

            // TODO: Verify content safety for title and description
            // String contentToVerify = command.getTitle() + "|" + command.getDescription();
            // contentSafetyAIService.verifyTextContent(contentToVerify);

            // Get the profile of the user that is creating the achievemnet
            UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);

            if (userKto == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            Achievement achievement = Achievement.create(
                    cmd.getTitle(),
                    cmd.getDescription(),
                    cmd.getCompletedDate(),
                    cmd.getIsPublic(),
                    User.fromKto(userKto),
                    Arrays.asList(cmd.getSkills()));

            Achievement savedAchievement = achievementRepository.save(achievement);

            // We don't need to call 'publish events' explicitly,
            // the JPA call to save the entity will take care of the event publishing

            return HandlerResponse.created(savedAchievement.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "create achievement");
        }
    }
}
