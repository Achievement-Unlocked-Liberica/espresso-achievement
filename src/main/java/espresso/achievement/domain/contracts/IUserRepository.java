package espresso.achievement.domain.contracts;

import espresso.achievement.domain.entities.UserProfile;

public interface IUserRepository {
    
    UserProfile getUserByKey(String userKey);
}
