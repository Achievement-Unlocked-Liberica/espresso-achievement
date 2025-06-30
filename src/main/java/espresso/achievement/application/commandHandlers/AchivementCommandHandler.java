package espresso.achievement.application.commandHandlers;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IAchievementCommandHandler;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.contracts.IAchievementCmdRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;

@Service
public class AchivementCommandHandler implements IAchievementCommandHandler {

    @Autowired
    private IAchievementCmdRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    public HandlerResponse<Object> handle(CreateAchivementCommand command) {

        try {
            // Validate the command
            var validationErrors = command.validate();
            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Get the profile of the user that is creating the achievemnet
            User user = userRepository.findByKey(command.getUserKey());

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

            return HandlerResponse.created(savedEntity);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
