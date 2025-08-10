package espresso.achievement.application.handlers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import espresso.achievement.domain.contracts.IAchievementCommandHandler;
import espresso.achievement.domain.contracts.IAchievementCommentRepository;
import espresso.achievement.domain.commands.AddAchievementCommentCommand;
import espresso.achievement.domain.commands.CreateAchivementCommand;
import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.achievement.domain.contracts.IAchievementCmdRepository;
import espresso.achievement.domain.contracts.IAchievementMediaRepository;
import espresso.achievement.domain.contracts.IAchievementQryRepository;
import espresso.achievement.domain.entities.AchievementComment;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;

@Service
public class AchivementCommandHandler implements IAchievementCommandHandler {

    @Autowired
    private IAchievementCmdRepository achievementCmdRepository;

    @Autowired
    private IAchievementQryRepository achievementQryRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAchievementMediaRepository achievementMediaRepository;

    @Autowired
    private IAchievementCommentRepository achievementCommentRepository;

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

            Achievement savedEntity = achievementCmdRepository.save(entity);

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
            Achievement achievement = achievementQryRepository.getAchievementByKey(Achievement.class, cmd.getAchievementKey());

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

    /**
     * Handles the command to add a new comment to an achievement.
     * Delegates to the dedicated comment command handler for processing.
     * 
     * @param command The command containing comment data
     * @return HandlerResponse with the created comment or error details
     */
    public HandlerResponse<Object> handleAddComment(AddAchievementCommentCommand command) {
        try {
            // Validate the command
            var validationErrors = command.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Verify the achievement exists
            Achievement achievement = achievementQryRepository.getAchievementByKey(
                Achievement.class, 
                command.getAchievementKey()
            );

            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Verify the user exists
            User user = userRepository.findByKey(command.getUserKey(), User.class);

            if (user == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Create a new achievement comment using the domain model's create operation
            AchievementComment comment = AchievementComment.create(
                command.getCommentText(),
                achievement,
                user
            );

            // Save the comment through the repository
            AchievementComment savedComment = achievementCommentRepository.save(comment);

            // Return success response with the created comment
            return HandlerResponse.created(savedComment);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }
}
