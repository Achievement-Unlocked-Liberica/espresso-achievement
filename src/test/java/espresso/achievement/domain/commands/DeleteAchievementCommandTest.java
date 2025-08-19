package espresso.achievement.domain.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class DeleteAchievementCommandTest {

    @Test
    void testValidateWithValidData() {
        // Arrange
        DeleteAchievementCommand command = new DeleteAchievementCommand();
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
        DeleteAchievementCommand command = new DeleteAchievementCommand();
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
        DeleteAchievementCommand command = new DeleteAchievementCommand();
        command.setAchievementKey(null);
        command.setUserKey("USER123");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for null achievement key");
        boolean hasBlankKeyError = errors.stream()
            .anyMatch(error -> error.contains("ACHIEVEMENT KEY MUST BE PROVIDED"));
        assertTrue(hasBlankKeyError, "Should have blank achievement key error");
    }

    @Test
    void testValidateWithBlankUserKey() {
        // Arrange
        DeleteAchievementCommand command = new DeleteAchievementCommand();
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
        DeleteAchievementCommand command = new DeleteAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey(null);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for null user key");
        boolean hasBlankUserKeyError = errors.stream()
            .anyMatch(error -> error.contains("A USER KEY MUST BE PROVIDED"));
        assertTrue(hasBlankUserKeyError, "Should have blank user key error");
    }

    @Test
    void testValidateWithInvalidAchievementKeyLength() {
        // Arrange
        DeleteAchievementCommand command = new DeleteAchievementCommand();
        command.setAchievementKey("SHORT"); // Invalid - too short
        command.setUserKey("USER123");
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for invalid achievement key length");
        boolean hasLengthError = errors.stream()
            .anyMatch(error -> error.contains("EXACTLY 7 CHARACTERS"));
        assertTrue(hasLengthError, "Should have achievement key length error");
    }

    @Test
    void testValidateWithInvalidUserKeyLength() {
        // Arrange
        DeleteAchievementCommand command = new DeleteAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("SHORT"); // Invalid - too short
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for invalid user key length");
        boolean hasLengthError = errors.stream()
            .anyMatch(error -> error.contains("EXACTLY 7 CHARACTERS"));
        assertTrue(hasLengthError, "Should have user key length error");
    }

    @Test
    void testValidateWithMultipleErrors() {
        // Arrange
        DeleteAchievementCommand command = new DeleteAchievementCommand();
        command.setAchievementKey(""); // Invalid - blank
        command.setUserKey("SHORT"); // Invalid - wrong length
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for multiple invalid fields");
        assertTrue(errors.size() >= 2, "Should have multiple validation errors");
    }

    @Test
    void testValidateWithAllNullFields() {
        // Arrange
        DeleteAchievementCommand command = new DeleteAchievementCommand();
        command.setAchievementKey(null);
        command.setUserKey(null);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for all null fields");
        assertTrue(errors.size() >= 2, "Should have multiple validation errors for all null fields");
    }

    @Test
    void testValidateWithMaxLengthValues() {
        // Arrange
        DeleteAchievementCommand command = new DeleteAchievementCommand();
        command.setAchievementKey("ACHI001"); // Exactly 7 characters
        command.setUserKey("USER123"); // Exactly 7 characters
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertTrue(errors.isEmpty(), "Should have no validation errors for exact length values");
    }

    @Test
    void testConstructorAndGettersSetters() {
        // Test default constructor
        DeleteAchievementCommand command1 = new DeleteAchievementCommand();
        assertNull(command1.getAchievementKey());
        assertNull(command1.getUserKey());
        
        // Test parameterized constructor
        DeleteAchievementCommand command2 = new DeleteAchievementCommand("ACHI001", "USER123");
        assertEquals("ACHI001", command2.getAchievementKey());
        assertEquals("USER123", command2.getUserKey());
        
        // Test setters
        command1.setAchievementKey("ACHI002");
        command1.setUserKey("USER456");
        assertEquals("ACHI002", command1.getAchievementKey());
        assertEquals("USER456", command1.getUserKey());
    }
}
