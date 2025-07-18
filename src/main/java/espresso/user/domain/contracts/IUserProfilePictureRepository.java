package espresso.user.domain.contracts;

import java.io.IOException;

import espresso.user.domain.entities.UserProfileImage;

public interface IUserProfilePictureRepository {
    UserProfileImage save(UserProfileImage userProfileimage) throws IOException;
}
