package espresso.common.domain.operational.exceptionPolicy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import espresso.achievement.domain.operational.exceptionPolicy.AchievementException;
import espresso.user.domain.operational.exceptionPolicy.UserException;
import espresso.security.domain.operational.exceptionPolicy.SecurityException;

/**
 * Test class to verify the exception hierarchy works correctly.
 * This is a temporary test to validate our exception implementation.
 */
class ExceptionHierarchyTest {

    @Test
    void testAchievementExceptionCreation() {
        // Test achievement not found exception
        AchievementException notFoundEx = AchievementException.notFound("ACH123");
        
        assertEquals("achievement.not.found", notFoundEx.getMessageKey());
        assertEquals("ACHIEVEMENT_NOT_FOUND", notFoundEx.getScenario());
        assertEquals("ACH_001", notFoundEx.getErrorCode());
        assertArrayEquals(new Object[]{"ACH123"}, notFoundEx.getMessageArgs());
    }

    @Test
    void testUserExceptionCreation() {
        // Test user already exists exception
        UserException alreadyExistsEx = UserException.alreadyExists("username", "testuser");
        
        assertEquals("user.already.exists", alreadyExistsEx.getMessageKey());
        assertEquals("USER_ALREADY_EXISTS", alreadyExistsEx.getScenario());
        assertEquals("USER_002", alreadyExistsEx.getErrorCode());
        assertArrayEquals(new Object[]{"username", "testuser"}, alreadyExistsEx.getMessageArgs());
    }

    @Test
    void testSecurityExceptionCreation() {
        // Test authentication failed exception
        SecurityException authFailedEx = SecurityException.authenticationFailed("john.doe");
        
        assertEquals("security.authentication.failed", authFailedEx.getMessageKey());
        assertEquals("AUTHENTICATION_FAILED", authFailedEx.getScenario());
        assertEquals("SEC_001", authFailedEx.getErrorCode());
        assertArrayEquals(new Object[]{"john.doe"}, authFailedEx.getMessageArgs());
    }

    @Test
    void testExceptionInheritance() {
        AchievementException achievementEx = AchievementException.notFound("TEST");
        UserException userEx = UserException.notFound("TEST");
        SecurityException securityEx = SecurityException.invalidCredentials();

        // All should be instances of DomainException
        assertTrue(achievementEx instanceof DomainException);
        assertTrue(userEx instanceof DomainException);
        assertTrue(securityEx instanceof DomainException);

        // All should be instances of RuntimeException
        assertTrue(achievementEx instanceof RuntimeException);
        assertTrue(userEx instanceof RuntimeException);
        assertTrue(securityEx instanceof RuntimeException);
    }

    @Test
    void testExceptionWithNullArgs() {
        SecurityException ex = SecurityException.invalidCredentials();
        
        assertEquals("security.invalid.credentials", ex.getMessageKey());
        assertEquals("INVALID_CREDENTIALS", ex.getScenario());
        assertEquals("SEC_002", ex.getErrorCode());
        assertEquals(0, ex.getMessageArgs().length); // Should be empty array, not null
    }
}