package espresso.user.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import espresso.user.domain.entities.User;

public interface UserPSQLProvider extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.entityKey = ?1")
    User findByEntityKey(String key);
}
