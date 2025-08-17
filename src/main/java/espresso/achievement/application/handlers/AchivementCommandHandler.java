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
import espresso.achievement.domain.commands.UpdateAchievementCommand;
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

/**
 * Handles the creation of a new achievement.
 * 
 * This method validates the provided {@link CreateAchivementCommand}, retrieves
 * the user
 * associated with the command, and creates a new achievement entity. The
 * created achievement
 * is then saved to the repository.
 * 
 * @param command The {@link CreateAchivementCommand} containing the details of
 *                the achievement to be created.
 * @return A {@link HandlerResponse} containing the created achievement or error
 *         details in case of failure.
 *         Possible response types:
 *         <ul>
 *         <li>{@link ResponseType#VALIDATION_ERROR} - If the command contains
 *         validation errors.</li>
 *         <li>{@link ResponseType#NOT_FOUND} - If the user associated with the
 *         command is not found.</li>
 *         <li>{@link ResponseType#INTERNAL_ERROR} - If an unexpected error
 *         occurs during processing.</li>
 *         <li>{@link ResponseType#CREATED} - If the achievement is successfully
 *         created.</li>
 *         </ul>
 */

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


    public HandlerResponse<Object> handle(UploadAchievementMediaCommand cmd) {
        try {
            // Validate the command
            var validationErrors = cmd.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Get the achievement by key
            Achievement achievement = achievementQryRepository.getAchievementByKey(Achievement.class,
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

    /**
     * Handles the command to add a new comment to an achievement.
     * Delegates to the dedicated comment command handler for processing.
     * 
     * @param cmd The command containing comment data
     * @return HandlerResponse with the created comment or error details
     */
    public HandlerResponse<Object> handle(AddAchievementCommentCommand cmd) {
        try {
            // Validate the command
            var validationErrors = cmd.validate();

            if (!validationErrors.isEmpty()) {
                return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Verify the achievement exists
            Achievement achievement = achievementQryRepository.getAchievementByKey(
                    Achievement.class,
                    cmd.getAchievementKey());

            if (achievement == null) {
                return HandlerResponse.error("Achievement not found", ResponseType.NOT_FOUND);
            }

            // Verify the user exists
            User user = userRepository.findByKey(cmd.getUserKey(), User.class);

            if (user == null) {
                return HandlerResponse.error("User not found", ResponseType.NOT_FOUND);
            }

            // Create a new achievement comment using the domain model's create operation
            AchievementComment comment = AchievementComment.create(
                    cmd.getCommentText(),
                    achievement,
                    user);

            // Save the comment through the repository
            AchievementComment savedComment = achievementCommentRepository.save(comment);

            // Return success response with the created comment
            return HandlerResponse.created(savedComment);

        } catch (Exception ex) {
            return HandlerResponse.error(ex.getMessage(), ResponseType.INTERNAL_ERROR);
        }
    }

    /**
     * Handles the command to update an existing achievement.
     * Validates the command, verifies user and achievement exist, updates the achievement,
     * and persists the changes to the repository.
     * 
     * @param cmd The command containing updated achievement data
     * @return HandlerResponse with the updated achievement or error details
     */
    public HandlerResponse<Object> handle(UpdateAchievementCommand cmd) {
        try {
            // Validate the command
            var validationErrors = cmd.validate();

            if (!validationErrors.isEmpty()) {
            return HandlerResponse.error(validationErrors, ResponseType.VALIDATION_ERROR);
            }

            // Retrieve user by userKey - throw not found error if missing
            User user = userRepository.findByKey(cmd.getUserKey(), User.class);
            if (user == null) {
            return HandlerResponse.error("LOCALIZE: USER NOT FOUND", ResponseType.NOT_FOUND);
            }

            // Retrieve achievement by achievementKey - throw not found error if missing
            Achievement achievement = achievementQryRepository.getAchievementByKey(
            Achievement.class, 
            cmd.getAchievementKey()
            );

            if (achievement == null) {
            return HandlerResponse.error("LOCALIZE: ACHIEVEMENT NOT FOUND", ResponseType.NOT_FOUND);
            }

            // Verify that the user owns the achievement
            if (!achievement.getUser().getEntityKey().equals(cmd.getUserKey())) {
            return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO UPDATE THIS ACHIEVEMENT", ResponseType.UNAUTHORIZED);
            }

            // Convert skills array to list for the update method
            List<String> skillsList = Arrays.asList(cmd.getSkills());

            // Call update method on achievement with command properties
            achievement.update(
            cmd.getTitle(), 
            cmd.getDescription(), 
            skillsList, 
            cmd.getIsPublic()
            );

            // Save updated achievement via AchievementCmdRepository.update
            Achievement updatedAchievement = achievementCmdRepository.update(achievement);

            return HandlerResponse.success(updatedAchievement);

        } catch (Exception ex) {
            return HandlerResponse.error("LOCALIZE: " + ex.getMessage().toUpperCase(), ResponseType.INTERNAL_ERROR);
        }
    }
}
