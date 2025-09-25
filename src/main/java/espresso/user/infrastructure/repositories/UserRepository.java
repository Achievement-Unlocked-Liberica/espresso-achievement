package espresso.user.infrastructure.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;
import espresso.user.domain.operational.exceptionPolicy.UserException;
import espresso.user.domain.operational.validationPolicy.UserValidator;

@Repository
public class UserRepository implements IUserRepository {

    @Autowired
    private UserPSQLProvider userJpaRepository;

    @Override
    public User save(User user) {
        try {
            UserValidator.validateUser(user);
            
            return userJpaRepository.save(user);
            
        } catch (UserException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataIntegrityViolationException e) {
            // Handle unique constraint violations
            if (e.getMessage().contains("username")) {
                throw UserException.alreadyExists("username", user.getUsername());
            } else if (e.getMessage().contains("email")) {
                throw UserException.alreadyExists("email", user.getEmail());
            }
            throw UserException.profileUpdateFailed(user.getEntityKey(), "Data integrity violation during save operation");
        } catch (DataAccessException e) {
            throw UserException.profileUpdateFailed(user.getEntityKey(), "Database error during save operation");
        } catch (Exception e) {
            throw UserException.profileUpdateFailed(
                user != null ? user.getEntityKey() : "unknown", 
                "Unexpected error occurred while saving user"
            );
        }
    }

    @Override
    public User findByUsername(String username) {
        try {
            UserValidator.validateUsername(username);
            
            return userJpaRepository.findByUsername(username);
            
        } catch (UserException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            throw UserException.profileUpdateFailed("unknown", "Database error while finding user by username");
        } catch (Exception e) {
            throw UserException.profileUpdateFailed("unknown", "Unexpected error occurred while finding user by username");
        }
    }

    @Override
    public User findByEmail(String email) {
        try {
            UserValidator.validateEmail(email);
            
            return userJpaRepository.findByEmail(email);
            
        } catch (UserException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            throw UserException.profileUpdateFailed("unknown", "Database error while finding user by email");
        } catch (Exception e) {
            throw UserException.profileUpdateFailed("unknown", "Unexpected error occurred while finding user by email");
        }
    }

    @Override
    public boolean checkUsernameExists(String username) {
        try {
            UserValidator.validateUsername(username);
            
            return userJpaRepository.checkUsernameExists(username);
            
        } catch (UserException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            throw UserException.profileUpdateFailed("unknown", "Database error while checking username existence");
        } catch (Exception e) {
            throw UserException.profileUpdateFailed("unknown", "Unexpected error occurred while checking username existence");
        }
    }

    @Override
    public boolean checkEmailExists(String email) {
        try {
            UserValidator.validateEmail(email);
            
            return userJpaRepository.checkEmailExists(email);
            
        } catch (UserException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            throw UserException.profileUpdateFailed("unknown", "Database error while checking email existence");
        } catch (Exception e) {
            throw UserException.profileUpdateFailed("unknown", "Unexpected error occurred while checking email existence");
        }
    }

    @Override
    public <T> T findByKey(String entityKey, Class<T> type) {
        try {
            UserValidator.validateEntityKey(entityKey);
            
            T result = this.userJpaRepository.findByKey(entityKey, type);
            
            UserValidator.validateQueryResult(result, entityKey);
            
            return result;
            
        } catch (UserException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (DataAccessException e) {
            throw UserException.profileUpdateFailed("unknown", "Database error while finding user by key: " + entityKey);
        } catch (Exception e) {
            throw UserException.profileUpdateFailed("unknown", "Unexpected error occurred while finding user by key: " + entityKey);
        }
    }
}
