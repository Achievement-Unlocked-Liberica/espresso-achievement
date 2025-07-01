package espresso.user.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import espresso.user.domain.entities.User;

@Repository
public interface UserPSQLProvider extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = ?1")
    boolean checkUsernameExists(String username);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = ?1")
    boolean checkEmailExists(String email);

    @Query("SELECT u FROM User u WHERE u.entityKey = :entityKey")
    <T> T findByKey(String entityKey, Class<T> type);
}
