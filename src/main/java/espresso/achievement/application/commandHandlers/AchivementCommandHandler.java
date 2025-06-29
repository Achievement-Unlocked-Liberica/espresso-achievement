package espresso.achievement.application.commandHandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IAchievementCommandHandler;
import espresso.achievement.domain.contracts.IMediaQryRepository;
import espresso.achievement.application.response.HandlerResult;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.contracts.IAchievementCmdRepository;
import espresso.achievement.domain.contracts.ISkillRepository;
import espresso.achievement.domain.contracts.IUserRepository;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;
import espresso.achievement.domain.entities.PreMedia;
import espresso.achievement.domain.entities.Skill;
import espresso.achievement.domain.entities.UserProfile;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import lombok.NoArgsConstructor;

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
            UserProfile userProfile = userRepository.getUserByKey(command.getUserKey());

            // Get the skills of the achievement to be created
            List<String> skills = Arrays.asList(command.getSkills());

            Achievement entity = Achievement.create(
                    command.getTitle(),
                    command.getDescription(),
                    command.getCompletedDate(),
                    command.getIsPublic(),
                    userProfile,
                    skills);

            Achievement savedEntity = achievementRepository.save(entity);

            return HandlerResponse.created(savedEntity);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
