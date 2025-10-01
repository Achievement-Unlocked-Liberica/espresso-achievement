package espresso.achievement.application.commandHandlers;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import espresso.achievement.domain.contracts.IUploadAchievementMediaCommandHandler;
import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.achievement.domain.contracts.IAchievementRepository;
import espresso.achievement.domain.contracts.IAchievementMediaRepository;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.user.domain.entities.UserKto;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;
import espresso.common.application.handlers.CommonCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.achievement.domain.operational.exceptionPolicy.AchievementHandlerExceptionPolicy;

import lombok.extern.slf4j.Slf4j;
// Validation centralized in CommonCommandHandler

/**
 * Handles the upload of media files for an achievement.
 */
@Slf4j
@Service
public class UploadAchievementMediaCommandHandler extends CommonCommandHandler
        implements IUploadAchievementMediaCommandHandler {

    private final IAchievementRepository achievementRepository;
    private final IAchievementMediaRepository achievementMediaRepository;
    private final IUserRepository userRepository;
    private final AchievementHandlerExceptionPolicy exceptionPolicy;

    /**
     * Constructor for dependency injection.
     * 
     * @param achievementRepository Repository for achievement entity persistence operations
     * @param achievementMediaRepository Repository for achievement media operations
     * @param userRepository Repository for user entity queries and operations
     * @param exceptionPolicy Centralized exception handling policy
     */
    public UploadAchievementMediaCommandHandler(
            IAchievementRepository achievementRepository,
            IAchievementMediaRepository achievementMediaRepository,
            IUserRepository userRepository,
            AchievementHandlerExceptionPolicy exceptionPolicy) {
        this.achievementRepository = achievementRepository;
        this.achievementMediaRepository = achievementMediaRepository;
        this.userRepository = userRepository;
        this.exceptionPolicy = exceptionPolicy;
    }

    public HandlerResponse<Object> handle(UploadAchievementMediaCommand cmd) {
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

            // Get the achievement by key
            Achievement achievement = achievementRepository.getAchievementByKey(Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Verify that the user is authorized to delete this achievement (user must own
            // the achievement)
            if (!achievement.isCreator(User.fromKto(userKto))) {
                return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT",
                        ResponseType.UNAUTHORIZED);
            }

            // Process each image in the array
            for (MultipartFile image : cmd.getImages()) {

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

            return HandlerResponse.success(achievement.toKto());

        } catch (Exception ex) {
            return exceptionPolicy.handleException(ex, "upload media to achievement");
        }
    }
}
