package espresso.user.domain.contracts;

import espresso.user.domain.entities.User;

public interface IUserRepository {
    User save(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean checkUsernameExists(String username);
    boolean checkEmailExists(String email);
    <T> T findByKey(String entityKey, Class<T> type);
}
