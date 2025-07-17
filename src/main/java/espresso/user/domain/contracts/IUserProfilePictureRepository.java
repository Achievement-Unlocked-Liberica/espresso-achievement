package espresso.user.domain.contracts;

import espresso.user.domain.entities.UserProfileImage;

public interface IUserProfilePictureRepository {
    UserProfileImage save(UserProfileImage userProfileimage);
}
