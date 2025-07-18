package espresso.achievement.infrastructure.repositories;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import espresso.achievement.domain.contracts.IAchievementMediaRepository;
import espresso.achievement.domain.entities.Achievement;
import espresso.achievement.domain.entities.AchievementMedia;
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
    public AchievementMedia save(Achievement achievement, AchievementMedia achievementMedia) throws IOException {
        // Implementation for saving the achievement media
        User user = achievement.getUser();

        if (user == null) {
            throw new IllegalArgumentException("The user in the achievement cannot be null");
        }

        // Construct the directory path for storing the media
        String directory = mediaDirectory + "/" + user.getEntityKey();

        String objectStoragePath = s3DataProvider.uploadImage(directory, achievementMedia);

        // Clear the image data to avoid sending large binary data in the response
        achievementMedia.setImageData(null);

        achievementMedia.setMediaUrl(objectStoragePath);

        AchievementMedia savedEntity = psqlProvider.save(achievementMedia);

        return savedEntity;
    }
}
