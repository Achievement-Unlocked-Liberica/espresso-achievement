package espresso.challenge.infrastructure.repositories;

import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import org.springframework.beans.factory.annotation.Value;

import espresso.challenge.domain.contracts.IChallengeMediaRepository;
import espresso.challenge.domain.entities.Challenge;
import espresso.challenge.domain.entities.ChallengeMedia;
import espresso.challenge.domain.operational.exceptionPolicy.ChallengeException;
import espresso.challenge.domain.operational.validationPolicy.ChallengeValidator;
import espresso.user.domain.entities.User;

@Repository
public class ChallengeMediaRepository implements IChallengeMediaRepository {

    private final Environment environment;
    private final ChallengeMediaS3Provider s3DataProvider;
    private final ChallengeMediaPSQLProvider psqlProvider;
    private final String mediaDirectory;

    /**
     * Constructor for dependency injection.
     * 
     * @param environment Spring environment for configuration properties
     * @param s3DataProvider S3 data provider for media storage operations
     * @param psqlProvider PostgreSQL data provider for challenge media operations
     * @param mediaDirectory Directory path for storing challenge media
     */
    public ChallengeMediaRepository(
            Environment environment,
            ChallengeMediaS3Provider s3DataProvider,
            ChallengeMediaPSQLProvider psqlProvider,
            @Value("${challenge.media.directory}") String mediaDirectory) {
        this.environment = environment;
        this.s3DataProvider = s3DataProvider;
        this.psqlProvider = psqlProvider;
        this.mediaDirectory = mediaDirectory;
    }

    @Override
    public ChallengeMedia save(Challenge challenge, ChallengeMedia challengeMedia) {
        try {
            // Implementation for saving the challenge media
            User user = challenge.getUser();

            ChallengeValidator.validateForPersistence(challenge);
            ChallengeValidator.validateChallengeMedia(challengeMedia);

            // Construct the directory path for storing the media
            String directory = mediaDirectory + "/" + user.getEntityKey();

            String objectStoragePath = s3DataProvider.uploadImage(directory, challengeMedia);

            // Clear the image data to avoid sending large binary data in the response
            challengeMedia.setImageData(null);
            challengeMedia.setMediaUrl(objectStoragePath);

            return psqlProvider.save(challengeMedia);

        } catch (ChallengeException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            throw ChallengeException.mediaProcessingFailed("image", "Database error during media save operation");
        } catch (Exception e) {
            throw ChallengeException.mediaProcessingFailed("image", "Unexpected error occurred while saving challenge media");
        }
    }
}
