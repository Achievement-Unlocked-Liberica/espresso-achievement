package espresso.achievement.infrastructure.repositories;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import espresso.achievement.domain.contracts.IAchievementMediaRepository;
import espresso.achievement.domain.entities.AchievementMedia;

@Repository
public class AchievementMediaRepository implements IAchievementMediaRepository {

    @Autowired
    private Environment environment;

    @Autowired
    AchievementMediaS3Provider s3DataProvider;

    @Autowired
    AchievementMediaPSQLProvider psqlProvider;

    @Override
    public AchievementMedia save(AchievementMedia achievementMedia) {
        // Implementation for saving the achievement media
        try {
            String directory = environment.getProperty("achievement.media.directory");

            String objectStoragePath = s3DataProvider.uploadImage(directory, achievementMedia);

            // Clear the image data to avoid sending large binary data in the response
            achievementMedia.setImageData(null);

            achievementMedia.setMediaUrl(objectStoragePath);

            AchievementMedia savedEntity = psqlProvider.save(achievementMedia);

            return savedEntity;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return achievementMedia; // Placeholder return
    }
}
