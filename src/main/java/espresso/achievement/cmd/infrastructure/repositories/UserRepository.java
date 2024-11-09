package espresso.achievement.cmd.infrastructure.repositories;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import espresso.achievement.cmd.domain.contracts.IUserRepository;
import espresso.achievement.cmd.domain.entities.UserProfile;

@Repository
public class UserRepository implements IUserRepository {

    public UserRepository() {
    }

    @Override
    public UserProfile getUserByKey(String userKey) {
        // ! Mocking

        UserProfile userProfile = new UserProfile("user name", "first name", "last name", "email");
        userProfile.setId(UUID.randomUUID());
        userProfile.setKey(userKey);
        userProfile.setTimestamp(new Date());

        userProfile.cleanForSerialization();
        
        return userProfile;
    }

}
