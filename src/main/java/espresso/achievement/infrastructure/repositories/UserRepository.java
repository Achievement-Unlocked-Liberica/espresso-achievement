package espresso.achievement.infrastructure.repositories;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import espresso.achievement.domain.contracts.IUserRepository;
import espresso.achievement.domain.entities.UserProfile;

@Component
public class UserRepository implements IUserRepository {

    public UserRepository() {
    }

    @Override
    public UserProfile getUserByKey(String userKey) {
        // ! Mocking

        UserProfile userProfile = new UserProfile("user name", "first name", "last name", "email");
        userProfile.setId(Math.round(1000L * ThreadLocalRandom.current().nextDouble()));
        userProfile.setEntityKey(userKey);
        userProfile.setTimeStamp(new Date());

        userProfile.cleanForSerialization();
        
        return userProfile;
    }

}
