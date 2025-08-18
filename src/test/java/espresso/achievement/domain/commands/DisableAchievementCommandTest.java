package espresso.achievement.domain.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class DisableAchievementCommandTest {

    @Test
    void testValidateWithValidData() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertTrue(errors.isEmpty(), "Should have no validation errors for valid data");
    }

    @Test
    void testValidateWithBlankAchievementKey() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("");
        command.setUserKey("USER123");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for blank achievement key");
        boolean hasBlankKeyError = errors.stream()
            .anyMatch(error -> error.contains("ACHIEVEMENT KEY MUST BE PROVIDED"));
        assertTrue(hasBlankKeyError, "Should have blank achievement key error");
    }

    @Test
    void testValidateWithNullAchievementKey() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey(null);
        command.setUserKey("USER123");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for null achievement key");
        boolean hasNullKeyError = errors.stream()
            .anyMatch(error -> error.contains("ACHIEVEMENT KEY MUST BE PROVIDED"));
        assertTrue(hasNullKeyError, "Should have null achievement key error");
    }

    @Test
    void testValidateWithInvalidAchievementKeyLength() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("SHORT");
        command.setUserKey("USER123");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for short achievement key");
        boolean hasLengthError = errors.stream()
            .anyMatch(error -> error.contains("ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS"));
        assertTrue(hasLengthError, "Should have achievement key length error");
    }

    @Test
    void testValidateWithTooLongAchievementKey() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("TOOLONGKEY");
        command.setUserKey("USER123");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for long achievement key");
        boolean hasLengthError = errors.stream()
            .anyMatch(error -> error.contains("ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS"));
        assertTrue(hasLengthError, "Should have achievement key length error");
    }

    @Test
    void testValidateWithBlankUserKey() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for blank user key");
        boolean hasBlankUserKeyError = errors.stream()
            .anyMatch(error -> error.contains("A USER KEY MUST BE PROVIDED"));
        assertTrue(hasBlankUserKeyError, "Should have blank user key error");
    }

    @Test
    void testValidateWithNullUserKey() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey(null);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for null user key");
        boolean hasNullUserKeyError = errors.stream()
            .anyMatch(error -> error.contains("A USER KEY MUST BE PROVIDED"));
        assertTrue(hasNullUserKeyError, "Should have null user key error");
    }

    @Test
    void testValidateWithInvalidUserKeyLength() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("SHORT");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for short user key");
        boolean hasLengthError = errors.stream()
            .anyMatch(error -> error.contains("ENTITY KEY MUST BE EXACTLY 7 CHARACTERS"));
        assertTrue(hasLengthError, "Should have user key length error");
    }

    @Test
    void testValidateWithTooLongUserKey() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("TOOLONGKEY");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for long user key");
        boolean hasLengthError = errors.stream()
            .anyMatch(error -> error.contains("ENTITY KEY MUST BE EXACTLY 7 CHARACTERS"));
        assertTrue(hasLengthError, "Should have user key length error");
    }

    @Test
    void testValidateWithMultipleErrors() {
        // Arrange
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("SHORT");
        command.setUserKey("ALSO");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertTrue(errors.size() >= 2, "Should have multiple validation errors");
        boolean hasAchievementKeyError = errors.stream()
            .anyMatch(error -> error.contains("ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS"));
        boolean hasUserKeyError = errors.stream()
            .anyMatch(error -> error.contains("ENTITY KEY MUST BE EXACTLY 7 CHARACTERS"));
        assertTrue(hasAchievementKeyError, "Should have achievement key error");
        assertTrue(hasUserKeyError, "Should have user key error");
    }

    @Test
    void testConstructorAndGettersSetters() {
        // Test default constructor
        DisableAchievementCommand command1 = new DisableAchievementCommand();
        assertNull(command1.getAchievementKey());
        assertNull(command1.getUserKey());
        
        // Test parameterized constructor
        DisableAchievementCommand command2 = new DisableAchievementCommand("ACHI001", "USER123");
        assertEquals("ACHI001", command2.getAchievementKey());
        assertEquals("USER123", command2.getUserKey());
        
        // Test setters
        command1.setAchievementKey("ACHI002");
        command1.setUserKey("USER456");
        assertEquals("ACHI002", command1.getAchievementKey());
        assertEquals("USER456", command1.getUserKey());
    }
}
