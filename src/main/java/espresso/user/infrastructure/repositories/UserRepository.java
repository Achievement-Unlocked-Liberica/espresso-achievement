package espresso.user.infrastructure.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

@Repository
public class UserRepository implements IUserRepository {

    @Autowired
    private UserPSQLProvider userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userJpaRepository.checkUsernameExists(username);
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userJpaRepository.checkEmailExists(email);
    }

    @Override
    public <T> T findByKey(String entityKey, Class<T> type) {

        return this.userJpaRepository.findByKey(entityKey, type);
    }

    @Override
    public void updateProfilePicture(Long id, String pictureUrl) {

        this.userJpaRepository.updateProfilePicture(id, pictureUrl);
    }
}
