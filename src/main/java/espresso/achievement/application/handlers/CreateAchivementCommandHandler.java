package espresso.achievement.application.handlers;

import java.util.Arrays;
import java.util.List;
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
// Validation centralized in CommonCommandHandler

/**
 * Handles the creation of a new achievement.
 * 
 * This handler validates the provided {@link CreateAchivementCommand},
 * retrieves
 * the user
 * associated with the command, and creates a new achievement entity. The
 * created achievement
 * is then saved to the repository.
 */
@Service
public class CreateAchivementCommandHandler extends CommonCommandHandler implements ICreateAchivementCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IContentSafetyAIService contentSafetyAIService;

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

            Achievement entity = Achievement.create(
                    cmd.getTitle(),
                    cmd.getDescription(),
                    cmd.getCompletedDate(),
                    cmd.getIsPublic(),
                    User.fromKto(userKto),
                    Arrays.asList(cmd.getSkills()));

            Achievement savedEntity = achievementRepository.save(entity);

            // We don't need to call 'publish events' explicitly,
            // the JPA call to save the entity will take care of the event publishing

            return HandlerResponse.created(savedEntity);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
