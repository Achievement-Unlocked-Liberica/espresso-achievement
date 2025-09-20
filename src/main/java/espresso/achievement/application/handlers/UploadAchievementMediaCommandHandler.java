package espresso.achievement.application.handlers;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import espresso.achievement.domain.contracts.IUploadAchievementMediaCommandHandler;
import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.contracts.IContentSafetyAIService;
import espresso.achievement.domain.contracts.IAchievementMediaRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;
import espresso.common.application.handlers.CommonCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
// Validation centralized in CommonCommandHandler

/**
 * Handles the upload of media files for an achievement.
 */
@Service
public class UploadAchievementMediaCommandHandler extends CommonCommandHandler
        implements IUploadAchievementMediaCommandHandler {

    @Autowired
    private IAchievementRepository achievementRepository;

    @Autowired
    private IAchievementMediaRepository achievementMediaRepository;

    @Autowired
    private IContentSafetyAIService contentSafetyAIService;

    public HandlerResponse<Object> handle(UploadAchievementMediaCommand cmd) {
        try {
            // Validate the command using shared Validator and command-specific checks
            var validationResult = validateCommand(cmd);
            if (validationResult != null)
                return validationResult;

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

                // Validate the image content safety
                byte[] contentToVerify = image.getBytes();
                contentSafetyAIService.verifyImageContent(contentToVerify);

                // Create AchievementMedia entity
                AchievementMedia media = AchievementMedia.create(
                        achievement,
                        image.getOriginalFilename(),
                        image.getContentType(),
                        image.getBytes());

                // Save the media
                AchievementMedia savedMedia = achievementMediaRepository.save(achievement, media);

                // Add media to achievement (this will raise domain events)
                achievement.addMedia(savedMedia);
            }

            this.publishDomainEvents(achievement);

            // Return the achievement instance
            return HandlerResponse.created(achievement.getMedia());

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
