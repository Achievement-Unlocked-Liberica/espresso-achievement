package espresso.user.infrastructure.repositories;

import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import espresso.user.domain.contracts.IUserProfilePictureRepository;
import espresso.user.domain.entities.UserProfileImage;
import espresso.user.domain.operational.exceptionPolicy.UserException;
import espresso.user.domain.operational.validationPolicy.UserValidator;

@Repository
public class UserProfilePictureRepository implements IUserProfilePictureRepository {

    @Autowired
    private Environment environment;

    @Autowired
    UserProfilePictureS3Provider s3DataProvider;

    @Autowired
    UserProfilePicturePSQLProvider psqlProvider;

    @Override
    public UserProfileImage save(UserProfileImage userProfileImage) throws IOException {
        try {
            UserValidator.validateUserProfileImage(userProfileImage);
            
            // Implementation for saving the user profile image
            String directory = environment.getProperty("user.profilePicture.directory");
            
            UserValidator.validateConfigurationDirectory(directory, "user.profilePicture.directory");

            String objectStoragePath = s3DataProvider.uploadImage(directory, userProfileImage);

            // Clear the image data to avoid sending large binary data in the response
            userProfileImage.setImageData(null);
            userProfileImage.setProfileImageUrl(objectStoragePath);

            UserProfileImage savedEntity = psqlProvider.save(userProfileImage);
            return savedEntity;
            
        } catch (UserException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw UserException.profileUpdateFailed("unknown", "Profile picture already exists or data integrity violation");
        } catch (DataAccessException e) {
            throw UserException.profileUpdateFailed("unknown", "Database error occurred while saving profile picture");
        } catch (IOException e) {
            throw UserException.profileUpdateFailed("unknown", "File upload error: " + e.getMessage());
        } catch (Exception e) {
            throw UserException.profileUpdateFailed("unknown", "Unexpected error occurred while saving profile picture");
        }
    }

}
