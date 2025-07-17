package espresso.user.infrastructure.repositories;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import espresso.user.domain.contracts.IUserProfilePictureRepository;
import espresso.user.domain.entities.UserProfileImage;

@Repository
public class UserProfilePictureRepository implements IUserProfilePictureRepository {

    @Autowired
    private Environment environment;

    @Autowired
    UserProfilePictureS3Provider s3DataProvider;

    @Autowired
    UserProfilePicturePSQLProvider psqlProvider;

    @Override
    public UserProfileImage save(UserProfileImage userProfileImage) {
        // Implementation for saving the user profile image
        try {
            String directory = environment.getProperty("user.profilePicture.directory");

            String objectStoragePath = s3DataProvider.uploadImage(directory, userProfileImage);

            // Clear the image data to avoid sending large binary data in the response
            userProfileImage.setImageData(null);

            userProfileImage.setProfileImageUrl(objectStoragePath);

            UserProfileImage savedEntity = psqlProvider.save(userProfileImage);

            return savedEntity;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userProfileImage; // Placeholder return
    }

}
