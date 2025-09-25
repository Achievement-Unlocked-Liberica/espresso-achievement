package espresso.achievement.infrastructure.repositories;

import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import espresso.achievement.domain.contracts.IAchievementMediaRepository;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;
import espresso.achievement.domain.operational.exceptionPolicy.AchievementException;
import espresso.achievement.domain.operational.validationPolicy.AchievementValidator;
import espresso.user.domain.entities.User;

@Repository
public class AchievementMediaRepository implements IAchievementMediaRepository {

    @Autowired
    private Environment environment;

    @Autowired
    AchievementMediaS3Provider s3DataProvider;

    @Autowired
    AchievementMediaPSQLProvider psqlProvider;

    @Value("${achievement.media.directory}")
    private String mediaDirectory;

    @Override
    public AchievementMedia save(Achievement achievement, AchievementMedia achievementMedia) {
        try {
            // Implementation for saving the achievement media
            User user = achievement.getUser();

            AchievementValidator.validateForPersistence(achievement);
            AchievementValidator.validateAchievementMedia(achievementMedia);

            // Construct the directory path for storing the media
            String directory = mediaDirectory + "/" + user.getEntityKey();

            String objectStoragePath = s3DataProvider.uploadImage(directory, achievementMedia);

            // Clear the image data to avoid sending large binary data in the response
            achievementMedia.setImageData(null);
            achievementMedia.setMediaUrl(objectStoragePath);

            AchievementMedia savedEntity = psqlProvider.save(achievementMedia);
            return savedEntity;

        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            throw AchievementException.mediaProcessingFailed("image", "Database error during media save operation");
        } catch (Exception e) {
            throw AchievementException.mediaProcessingFailed("image", "Unexpected error occurred while saving achievement media");
        }
    }
}
