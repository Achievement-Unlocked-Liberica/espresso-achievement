package espresso.user.domain.contracts;

import espresso.user.domain.entities.UserProfileImage;

public interface IUserProfileImageRepository {
    UserProfileImage save(UserProfileImage userProfileimage);
}
