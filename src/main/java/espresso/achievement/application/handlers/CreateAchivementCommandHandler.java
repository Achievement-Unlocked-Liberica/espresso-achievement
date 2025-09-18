package espresso.achievement.application.handlers;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.ICreateAchivementCommandHandler;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
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

    // Validator logic centralized in CommonCommandHandler

    public HandlerResponse<Object> handle(CreateAchivementCommand command) {

        try {
            var invalid = validateCommand(command);
            if (invalid != null) return invalid;

            // Get the profile of the user that is creating the achievemnet
            User user = userRepository.findByKey(command.getUserKey(), User.class);

            if (user == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Get the skills of the achievement to be created
            List<String> skills = Arrays.asList(command.getSkills());

            Achievement entity = Achievement.create(
                    command.getTitle(),
                    command.getDescription(),
                    command.getCompletedDate(),
                    command.getIsPublic(),
                    user,
                    skills);

            Achievement savedEntity = achievementRepository.save(entity);

            // We don't need to call 'publish events' explicitly, 
            // the JPA call to save the entity will take care of the event publishing

            return HandlerResponse.created(savedEntity);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
