package espresso.user.domain.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for AddUserCommand class
 * Tests validation logic and property handling with password support
 */
class AddUserCommandTest {

    private AddUserCommand validCommand;
    private String validUsername;
    private String validEmail;
    private String validPassword;
    private String validFirstName;
    private String validLastName;
    private LocalDate validBirthDate;

    @BeforeEach
    void setUp() {
        validUsername = "johndoe123";
        validEmail = "john.doe@example.com";
        validPassword = "securePassword123";
        validFirstName = "John";
        validLastName = "Doe";
        validBirthDate = LocalDate.of(1990, 5, 15);
        
        validCommand = new AddUserCommand(
            validUsername,
            validEmail,
            validPassword,
            validFirstName,
            validLastName,
            validBirthDate
        );
    }

    // ========== Constructor Tests ==========

    @Test
    void constructor_AllArgsConstructor_Test() {
        // Arrange & Act
        AddUserCommand command = new AddUserCommand(
            "testuser",
            "test@example.com",
            "password123",
            "Test",
            "User",
            LocalDate.of(1985, 12, 25)
        );

        // Assert
        assertNotNull(command);
        assertEquals("testuser", command.getUsername());
        assertEquals("test@example.com", command.getEmail());
        assertEquals("password123", command.getPassword());
        assertEquals("Test", command.getFirstName());
        assertEquals("User", command.getLastName());
        assertEquals(LocalDate.of(1985, 12, 25), command.getBirthDate());
    }

    @Test
    void constructor_NoArgsConstructor_Test() {
        // Arrange & Act
        AddUserCommand command = new AddUserCommand();

        // Assert
        assertNotNull(command);
        assertNull(command.getUsername());
        assertNull(command.getEmail());
        assertNull(command.getPassword());
        assertNull(command.getFirstName());
        assertNull(command.getLastName());
        assertNull(command.getBirthDate());
    }

    // ========== Getter Tests ==========

    @Test
    void getUsername_ValidUsername_Test() {
        // Act
        String result = validCommand.getUsername();

        // Assert
        assertEquals(validUsername, result);
    }

    @Test
    void getEmail_ValidEmail_Test() {
        // Act
        String result = validCommand.getEmail();

        // Assert
        assertEquals(validEmail, result);
    }

    @Test
    void getPassword_ValidPassword_Test() {
        // Act
        String result = validCommand.getPassword();

        // Assert
        assertEquals(validPassword, result);
    }

    @Test
    void getFirstName_ValidFirstName_Test() {
        // Act
        String result = validCommand.getFirstName();

        // Assert
        assertEquals(validFirstName, result);
    }

    @Test
    void getLastName_ValidLastName_Test() {
        // Act
        String result = validCommand.getLastName();

        // Assert
        assertEquals(validLastName, result);
    }

    @Test
    void getBirthDate_ValidBirthDate_Test() {
        // Act
        LocalDate result = validCommand.getBirthDate();

        // Assert
        assertEquals(validBirthDate, result);
    }

    // ========== Validation Tests - Valid Scenarios ==========

    @Test
    void validate_ValidCommand_Test() {
        // Act
        Set<String> violations = validCommand.validate();

        // Assert
        assertNotNull(violations);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_MinimumValidValues_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            "abcde", // minimum 5 chars
            "a@b.co", // minimum valid email
            "12345678", // minimum 8 chars password
            "J", // minimum first name
            "D", // minimum last name
            LocalDate.of(1990, 1, 1) // valid past date
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Validation Tests - Username ==========

    @Test
    void validate_NullUsername_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            null, validEmail, validPassword, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EmptyUsername_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            "", validEmail, validPassword, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_UsernameTooShort_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            "abcd", validEmail, validPassword, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("BETWEEN 5 AND 50 CHARACTERS")));
    }

    // ========== Validation Tests - Email ==========

    @Test
    void validate_NullEmail_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, null, validPassword, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_InvalidEmail_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, "invalid-email", validPassword, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("VALID EMAIL ADDRESS")));
    }

    // ========== Validation Tests - Password ==========

    @Test
    void validate_NullPassword_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, null, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("password") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EmptyPassword_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, "", validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("password") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_PasswordTooShort_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, "1234567", validFirstName, validLastName, validBirthDate // 7 chars - too short
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("password") && v.contains("BETWEEN 8 AND 100 CHARACTERS")));
    }

    @Test
    void validate_PasswordTooLong_Test() {
        // Arrange
        String longPassword = "a".repeat(101); // 101 chars - too long
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, longPassword, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("password") && v.contains("BETWEEN 8 AND 100 CHARACTERS")));
    }

    @Test
    void validate_ValidPasswordBoundaries_Test() {
        // Test minimum valid password length (8 chars)
        AddUserCommand command1 = new AddUserCommand(
            validUsername, validEmail, "12345678", validFirstName, validLastName, validBirthDate
        );
        Set<String> violations1 = command1.validate();
        assertTrue(violations1.isEmpty(), "Password with 8 characters should be valid");

        // Test maximum valid password length (100 chars)
        String maxPassword = "a".repeat(100);
        AddUserCommand command2 = new AddUserCommand(
            validUsername, validEmail, maxPassword, validFirstName, validLastName, validBirthDate
        );
        Set<String> violations2 = command2.validate();
        assertTrue(violations2.isEmpty(), "Password with 100 characters should be valid");
    }

    // ========== Validation Tests - FirstName ==========

    @Test
    void validate_NullFirstName_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validPassword, null, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("firstName") && v.contains("MUST NOT BE BLANK")));
    }

    // ========== Validation Tests - LastName ==========

    @Test
    void validate_NullLastName_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validPassword, validFirstName, null, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("lastName") && v.contains("MUST NOT BE BLANK")));
    }

    // ========== Validation Tests - BirthDate ==========

    @Test
    void validate_NullBirthDate_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validPassword, validFirstName, validLastName, null
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("birthDate") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_FutureBirthDate_Test() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(1);
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validPassword, validFirstName, validLastName, futureDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("birthDate") && v.contains("PAST DATE")));
    }

    // ========== Equals and HashCode Tests ==========

    @Test
    void equals_SameValues_Test() {
        // Arrange
        AddUserCommand command1 = new AddUserCommand(
            validUsername, validEmail, validPassword, validFirstName, validLastName, validBirthDate
        );
        AddUserCommand command2 = new AddUserCommand(
            validUsername, validEmail, validPassword, validFirstName, validLastName, validBirthDate
        );

        // Act & Assert
        assertEquals(command1, command2);
        assertEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    void equals_DifferentPasswords_Test() {
        // Arrange
        AddUserCommand command1 = new AddUserCommand(
            validUsername, validEmail, "password123", validFirstName, validLastName, validBirthDate
        );
        AddUserCommand command2 = new AddUserCommand(
            validUsername, validEmail, "differentPassword", validFirstName, validLastName, validBirthDate
        );

        // Act & Assert
        assertNotEquals(command1, command2);
    }

    // ========== Inheritance Tests ==========

    @Test
    void validate_InheritsFromCommonCommand_Test() {
        // Assert
        assertTrue(validCommand instanceof AddUserCommand);
        assertTrue(validCommand instanceof espresso.common.domain.commands.CommonCommand);
    }
}
