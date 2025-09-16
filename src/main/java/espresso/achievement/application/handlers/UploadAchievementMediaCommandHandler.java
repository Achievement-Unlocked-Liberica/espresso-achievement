package espresso.achievement.application.handlers;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import espresso.achievement.domain.contracts.IUploadAchievementMediaCommandHandler;
import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.contracts.IAchievementMediaRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;

/**
 * Handles the upload of media files for an achievement.
 */
@Service
public class UploadAchievementMediaCommandHandler implements IUploadAchievementMediaCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IAchievementMediaRepository achievementMediaRepository;

    public HandlerResponse<Object> handle(UploadAchievementMediaCommand cmd) {
        try {
            // Validate the command
            var validationErrors = cmd.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Get the achievement by key
            Achievement achievement = achievementRepository.getAchievementByKey(Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Check if the requester is the owner of the achievement
            User achievementOwner = achievement.getUser();

            if (achievementOwner == null) {
                return HandlerResponse.error("Achievement owner not found", ResponseType.NOT_FOUND);
            }

            String ownerEntityKey = achievementOwner.getEntityKey();
            String requesterEntityKey = cmd.getUserKey();

            if (!ownerEntityKey.equals(requesterEntityKey)) {
                return HandlerResponse.error("The requester is not the owner of the achievment",
                        ResponseType.UNAUTHORIZED);
            }

            // Process each image in the array
            for (MultipartFile image : cmd.getImages()) {
                // Convert MultipartFile to byte array
                byte[] imageData;
                try {
                    imageData = image.getBytes();
                } catch (IOException e) {
                    return HandlerResponse.error("Failed to process image: " + e.getMessage(),
                            ResponseType.INTERNAL_ERROR);
                }

                // Create AchievementMedia entity
                AchievementMedia media = AchievementMedia.create(
                        achievement,
                        image.getOriginalFilename(),
                        image.getContentType(),
                        imageData);

                // Save the media
                achievementMediaRepository.save(achievement, media);
            }

            // Return the achievement instance
            return HandlerResponse.created(achievement);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
