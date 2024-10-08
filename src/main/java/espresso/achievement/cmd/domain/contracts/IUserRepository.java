package espresso.achievement.cmd.domain.contracts;

import espresso.achievement.cmd.domain.entities.UserProfile;

public interface IUserRepository {
    
    UserProfile getUserByKey(String userKey);
}
