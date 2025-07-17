package espresso.achievement.application.handlers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IAchievementCommandHandler;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.achievement.domain.contracts.IAchievementCmdRepository;
import espresso.achievement.domain.contracts.IAchievementMediaRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;

@Service
public class AchivementCommandHandler implements IAchievementCommandHandler {

    @Autowired
    private IAchievementCmdRepository achievementRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAchievementMediaRepository achievementMediaRepository;

    public HandlerResponse<Object> handle(CreateAchivementCommand command) {

        try {
            // Validate the command
            var validationErrors = command.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

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

            return HandlerResponse.created(savedEntity);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

    public HandlerResponse<Object> handleUploadMedia(UploadAchievementMediaCommand cmd) {
        try {
            // Validate the command
            var validationErrors = cmd.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Get the achievement by key
            Achievement achievement = achievementRepository.findByKey(cmd.getAchievementKey(), Achievement.class);
            
            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Convert MultipartFile to byte array
            byte[] imageData;
            try {
                imageData = cmd.getImage().getBytes();
            } catch (IOException e) {
                return HandlerResponse.error("Failed to process image: " + e.getMessage(), ResponseType.INTERNAL_ERROR);
            }

            // Create AchievementMedia entity
            AchievementMedia media = AchievementMedia.create(
                achievement,
                cmd.getImage().getOriginalFilename(),
                cmd.getImage().getContentType(),
                imageData
            );

            // Save the media
            AchievementMedia savedMedia = achievementMediaRepository.save(media);

            return HandlerResponse.created(savedMedia);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
