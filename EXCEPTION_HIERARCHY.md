# Exception Hierarchy Documentation

## Overview

This document describes the new domain exception hierarchy implemented for the Espresso Achievement application. The hierarchy provides a consistent and structured approach to handling business logic exceptions across all modules.

## Exception Structure

### Base Class: `DomainException`

Located in: `espresso.common.domain.operational.exceptionPolicy.DomainException`

**Properties:**
- `messageKey`: Localization key for user-friendly error messages
- `messageArgs`: Arguments for parameterized messages
- `scenario`: Business scenario identifier for logging/monitoring
- `errorCode`: Standardized error code for programmatic handling

### Module-Specific Exceptions

#### 1. AchievementException
**Location:** `espresso.achievement.domain.operational.exceptionPolicy.AchievementException`
**Error Code Prefix:** `ACH_`

**Factory Methods:**
- `notFound(String achievementKey)` - ACH_001
- `creationFailed(String reason)` - ACH_002
- `validationFailed(Object validationErrors)` - ACH_003
- `accessDenied(String userKey, String achievementKey)` - ACH_004
- `updateFailed(String achievementKey, String reason)` - ACH_005
- `mediaProcessingFailed(String mediaType, String reason)` - ACH_006

#### 2. UserException
**Location:** `espresso.user.domain.operational.exceptionPolicy.UserException`
**Error Code Prefix:** `USER_`

**Factory Methods:**
- `notFound(String identifier)` - USER_001
- `alreadyExists(String field, String value)` - USER_002
- `accountInactive(String userKey)` - USER_003
- `validationFailed(Object validationErrors)` - USER_004
- `profileUpdateFailed(String userKey, String reason)` - USER_005
- `profilePictureProcessingFailed(String userKey, String reason)` - USER_006
- `passwordValidationFailed(String requirements)` - USER_007

#### 3. SecurityException
**Location:** `espresso.security.domain.operational.exceptionPolicy.SecurityException`
**Error Code Prefix:** `SEC_`

**Factory Methods:**
- `authenticationFailed(String username)` - SEC_001
- `invalidCredentials()` - SEC_002
- `invalidToken(String reason)` - SEC_003
- `tokenExpired()` - SEC_004
- `accessDenied(String resource, String userKey)` - SEC_005
- `insufficientPrivileges(String requiredRole, String userRole)` - SEC_006
- `registrationFailed(String reason)` - SEC_007
- `passwordValidationFailed(String requirements)` - SEC_008

## Usage Examples

### In Command Handlers

```java
@Service
public class CreateAchievementCommandHandler extends CommonCommandHandler {
    
    public HandlerResponse<Object> handle(CreateAchievementCommand cmd) {
        // Validation
        var validationResult = validateCommand(cmd);
        if (validationResult != null)
            return validationResult;
        
        // Business logic with specific exceptions
        UserKto userKto = userRepository.findByKey(cmd.getUserKey(), UserKto.class);
        if (userKto == null) {
            throw UserException.notFound(cmd.getUserKey());
        }
        
        try {
            Achievement entity = Achievement.create(/*...*/);
            Achievement savedEntity = achievementRepository.save(entity);
            return HandlerResponse.created(savedEntity);
        } catch (DataAccessException ex) {
            throw AchievementException.creationFailed("Database error: " + ex.getMessage());
        }
    }
}
```

### In Repository Layer

```java
@Repository
public class AchievementRepository implements IAchievementRepository {
    
    @Override
    public Achievement save(Achievement achievement) {
        try {
            if (achievement == null) {
                throw AchievementException.validationFailed("Achievement cannot be null");
            }
            return achievementPSQLProvider.save(achievement);
        } catch (DataIntegrityViolationException ex) {
            throw AchievementException.creationFailed("Achievement with this key already exists");
        } catch (DataAccessException ex) {
            throw AchievementException.creationFailed("Failed to save achievement");
        }
    }
}
```

### In Security Layer

```java
@Service
public class CredentialsCommandHandler extends CommonCommandHandler {
    
    @Override
    public HandlerResponse<Object> handle(AuthCredentialsCommand command) {
        // Find user by username
        User user = userRepository.findByUsername(command.getUsername());
        if (user == null) {
            throw SecurityException.invalidCredentials();
        }
        
        // Check if user is active
        if (!user.isActive()) {
            throw UserException.accountInactive(user.getEntityKey());
        }
        
        // Verify password
        if (!user.verifyPassword(command.getPassword())) {
            throw SecurityException.authenticationFailed(command.getUsername());
        }
        
        // Generate JWT token
        JWTUserToken jwtToken = jwtAuthToken.generateToken(user);
        return HandlerResponse.success(jwtToken);
    }
}
```

## Message Properties Required

Add these keys to your `messages/*.properties` files:

```properties
# Achievement messages
achievement.not.found=Achievement with key ''{0}'' was not found
achievement.creation.failed=Failed to create achievement: {0}
achievement.validation.failed=Achievement validation failed: {0}
achievement.access.denied=User ''{0}'' does not have access to achievement ''{1}''
achievement.update.failed=Failed to update achievement ''{0}'': {1}
achievement.media.processing.failed=Failed to process {0} media: {1}

# User messages
user.not.found=User ''{0}'' was not found
user.already.exists=A user with this {0} already exists: {1}
user.account.inactive=User account ''{0}'' is inactive
user.validation.failed=User validation failed: {0}
user.profile.update.failed=Failed to update profile for user ''{0}'': {1}
user.profile.picture.processing.failed=Failed to process profile picture for user ''{0}'': {1}
user.password.validation.failed=Password validation failed: {0}

# Security messages
security.authentication.failed=Authentication failed for user ''{0}''
security.invalid.credentials=Invalid username or password
security.token.invalid=Invalid token: {0}
security.token.expired=Token has expired
security.access.denied=Access denied to resource ''{0}'' for user ''{1}''
security.insufficient.privileges=Insufficient privileges: required ''{0}'', actual ''{1}''
security.registration.failed=User registration failed: {0}
security.password.validation.failed=Password validation failed: {0}
```

## Benefits

1. **Consistent Error Handling**: All exceptions follow the same structure
2. **Localization Support**: Built-in support for internationalized error messages
3. **Programmatic Error Handling**: Standardized error codes for client applications
4. **Enhanced Monitoring**: Scenario and error code information for logging and analytics
5. **Type Safety**: Module-specific exceptions prevent accidental misuse
6. **Factory Methods**: Easy-to-use static methods for common error scenarios

## Next Steps

1. Implement Global Exception Handler to process these exceptions
2. Update existing command and query handlers to use new exceptions
3. Remove generic try-catch blocks in favor of specific exception throwing
4. Add message properties for all supported locales