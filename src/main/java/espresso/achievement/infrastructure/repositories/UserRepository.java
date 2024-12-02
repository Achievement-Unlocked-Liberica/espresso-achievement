package espresso.achievement.infrastructure.repositories;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import espresso.achievement.domain.contracts.IUserRepository;
import espresso.achievement.domain.entities.UserProfile;

@Repository
public class UserRepository implements IUserRepository {

    public UserRepository() {
    }

    @Override
    public UserProfile getUserByKey(String userKey) {
        // ! Mocking

        UserProfile userProfile = new UserProfile(null, "user name", "first name", "last name", "email");
        userProfile.setId(UUID.randomUUID());
        userProfile.setKey(userKey);
        userProfile.setTimestamp(new Date());

        userProfile.cleanForSerialization();
        
        return userProfile;
    }

}
