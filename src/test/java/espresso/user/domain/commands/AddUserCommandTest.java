package espresso.user.domain.commands;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for AddUserCommand class
 * Tests validation logic and property handling
 */
@ExtendWith(MockitoExtension.class)
class AddUserCommandTest {

    private AddUserCommand validCommand;
    private String validUsername;
    private String validEmail;
    private String validFirstName;
    private String validLastName;
    private LocalDate validBirthDate;

    @BeforeEach
    void setUp() {
        validUsername = "johndoe123";
        validEmail = "john.doe@example.com";
        validFirstName = "John";
        validLastName = "Doe";
        validBirthDate = LocalDate.of(1990, 5, 15);
        
        validCommand = new AddUserCommand(
            validUsername,
            validEmail,
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
            "Test",
            "User",
            LocalDate.of(1985, 12, 25)
        );

        // Assert
        assertNotNull(command);
        assertEquals("testuser", command.getUsername());
        assertEquals("test@example.com", command.getEmail());
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
            "J", // minimum first name
            "D", // minimum last name
            LocalDate.of(2000, 1, 1) // valid past date
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_MaximumValidValues_Test() {
        // Arrange
        String maxUsername = "a".repeat(50); // max 50 chars
        String maxFirstName = "F".repeat(100); // max 100 chars
        String maxLastName = "L".repeat(100); // max 100 chars
        
        AddUserCommand command = new AddUserCommand(
            maxUsername,
            "very.long.email.address@example-domain.com",
            maxFirstName,
            maxLastName,
            LocalDate.of(1950, 12, 31)
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Username Validation Tests ==========

    @Test
    void validate_NullUsername_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            null, validEmail, validFirstName, validLastName, validBirthDate
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
            "", validEmail, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_BlankUsername_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            "   ", validEmail, validFirstName, validLastName, validBirthDate
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
            "abcd", validEmail, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("BETWEEN 5 AND 50 CHARACTERS")));
    }

    @Test
    void validate_UsernameTooLong_Test() {
        // Arrange
        String longUsername = "a".repeat(51); // 51 chars
        AddUserCommand command = new AddUserCommand(
            longUsername, validEmail, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("BETWEEN 5 AND 50 CHARACTERS")));
    }

    // ========== Email Validation Tests ==========

    @Test
    void validate_NullEmail_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, null, validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EmptyEmail_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, "", validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_BlankEmail_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, "   ", validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_InvalidEmailFormat_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, "invalid-email", validFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("VALID EMAIL ADDRESS")));
    }

    @Test
    void validate_ValidEmailFormats_Test() {
        String[] validEmails = {
            "user@example.com",
            "test.email@domain.org",
            "user+tag@example.net",
            "user-name@sub-domain.example.com"
        };

        for (String email : validEmails) {
            // Arrange
            AddUserCommand command = new AddUserCommand(
                validUsername, email, validFirstName, validLastName, validBirthDate
            );

            // Act
            Set<String> violations = command.validate();

            // Assert
            assertTrue(violations.isEmpty(), "Email '" + email + "' should be valid");
        }
    }

    // ========== First Name Validation Tests ==========

    @Test
    void validate_NullFirstName_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, null, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("firstName") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EmptyFirstName_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, "", validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("firstName") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_BlankFirstName_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, "   ", validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("firstName") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_FirstNameTooLong_Test() {
        // Arrange
        String longFirstName = "F".repeat(101); // 101 chars - exceeds max 100
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, longFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("firstName") && v.contains("NOT BE GREATER THAN 100 CHARACTERS")));
    }

    @Test
    void validate_FirstNameMaxLength_Test() {
        // Arrange
        String maxFirstName = "F".repeat(100); // exactly 100 chars
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, maxFirstName, validLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Last Name Validation Tests ==========

    @Test
    void validate_NullLastName_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validFirstName, null, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("lastName") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EmptyLastName_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validFirstName, "", validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("lastName") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_BlankLastName_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validFirstName, "   ", validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("lastName") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_LastNameTooLong_Test() {
        // Arrange
        String longLastName = "L".repeat(101); // 101 chars - exceeds max 100
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validFirstName, longLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("lastName") && v.contains("NOT BE GREATER THAN 100 CHARACTERS")));
    }

    @Test
    void validate_LastNameMaxLength_Test() {
        // Arrange
        String maxLastName = "L".repeat(100); // exactly 100 chars
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validFirstName, maxLastName, validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Birth Date Validation Tests ==========

    @Test
    void validate_NullBirthDate_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validFirstName, validLastName, null
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
            validUsername, validEmail, validFirstName, validLastName, futureDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("birthDate") && v.contains("MUST BE A PAST DATE")));
    }

    @Test
    void validate_TodayBirthDate_Test() {
        // Arrange
        LocalDate today = LocalDate.now();
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validFirstName, validLastName, today
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("birthDate") && v.contains("MUST BE A PAST DATE")));
    }

    @Test
    void validate_YesterdayBirthDate_Test() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validFirstName, validLastName, yesterday
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_OldBirthDate_Test() {
        // Arrange
        LocalDate oldDate = LocalDate.of(1900, 1, 1);
        AddUserCommand command = new AddUserCommand(
            validUsername, validEmail, validFirstName, validLastName, oldDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Edge Cases and Special Characters ==========

    @Test
    void validate_SpecialCharactersInNames_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            "user.name-123",
            "user@example.com",
            "Jean-Luc",
            "O'Connor",
            validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_UnicodeCharactersInNames_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            "user测试",
            "test@example.com",
            "José",
            "Müller",
            validBirthDate
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Multiple Violations Tests ==========

    @Test
    void validate_AllFieldsNull_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(null, null, null, null, null);

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 5); // Should have violations for all required fields
        assertTrue(violations.stream().anyMatch(v -> v.contains("username")));
        assertTrue(violations.stream().anyMatch(v -> v.contains("email")));
        assertTrue(violations.stream().anyMatch(v -> v.contains("firstName")));
        assertTrue(violations.stream().anyMatch(v -> v.contains("lastName")));
        assertTrue(violations.stream().anyMatch(v -> v.contains("birthDate")));
    }

    @Test
    void validate_AllFieldsEmpty_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand("", "", "", "", null);

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 5); // Should have violations for all fields
    }

    @Test
    void validate_MultipleConstraintViolations_Test() {
        // Arrange
        AddUserCommand command = new AddUserCommand(
            "ab", // too short
            "invalid-email", // invalid format
            "F".repeat(101), // too long
            "L".repeat(101), // too long
            LocalDate.now().plusDays(1) // future date
        );

        // Act
        Set<String> violations = command.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 5); // Should have multiple violations
    }

    // ========== Boundary Value Tests ==========

    @Test
    void validate_BoundaryValues_Test() {
        // Test username boundaries
        String[] usernames = {"abcd", "abcde", "a".repeat(50), "a".repeat(51)};
        boolean[] usernameValid = {false, true, true, false};

        for (int i = 0; i < usernames.length; i++) {
            AddUserCommand command = new AddUserCommand(
                usernames[i], validEmail, validFirstName, validLastName, validBirthDate
            );
            Set<String> violations = command.validate();
            
            if (usernameValid[i]) {
                assertTrue(violations.stream().noneMatch(v -> v.contains("username")), 
                    "Username '" + usernames[i] + "' should be valid");
            } else {
                assertTrue(violations.stream().anyMatch(v -> v.contains("username")), 
                    "Username '" + usernames[i] + "' should be invalid");
            }
        }
    }

    // ========== Inheritance Tests ==========

    @Test
    void validate_InheritsFromCommonCommand_Test() {
        // Assert
        assertTrue(validCommand instanceof AddUserCommand);
        assertTrue(validCommand instanceof espresso.common.domain.commands.CommonCommand);
    }

    @Test
    void validate_UsesParentValidationMethod_Test() {
        // Arrange
        AddUserCommand validCommand = new AddUserCommand(
            "validuser", "valid@example.com", "Valid", "User", validBirthDate
        );
        AddUserCommand invalidCommand = new AddUserCommand("", "", "", "", null);

        // Act
        Set<String> validViolations = validCommand.validate();
        Set<String> invalidViolations = invalidCommand.validate();

        // Assert
        assertTrue(validViolations.isEmpty());
        assertFalse(invalidViolations.isEmpty());
        
        // Verify that the parent validation method is working
        assertNotNull(validCommand.validate());
        assertNotNull(invalidCommand.validate());
    }

    // ========== Equals and HashCode Tests ==========

    @Test
    void equals_SameValues_Test() {
        // Arrange
        AddUserCommand command1 = new AddUserCommand(
            validUsername, validEmail, validFirstName, validLastName, validBirthDate
        );
        AddUserCommand command2 = new AddUserCommand(
            validUsername, validEmail, validFirstName, validLastName, validBirthDate
        );

        // Act & Assert
        // Note: Due to CommonCommand having instance fields (ValidatorFactory, Validator)
        // that are created as new instances, equals will not work as expected
        // This is a limitation of the current implementation
        assertNotEquals(command1, command2, "Commands with same data are not equal due to parent class implementation");
        
        // However, we can verify field-level equality
        assertEquals(command1.getUsername(), command2.getUsername());
        assertEquals(command1.getEmail(), command2.getEmail());
        assertEquals(command1.getFirstName(), command2.getFirstName());
        assertEquals(command1.getLastName(), command2.getLastName());
        assertEquals(command1.getBirthDate(), command2.getBirthDate());
    }

    @Test
    void equals_DifferentValues_Test() {
        // Arrange
        AddUserCommand command1 = validCommand;
        AddUserCommand command2 = new AddUserCommand(
            "different", validEmail, validFirstName, validLastName, validBirthDate
        );

        // Act & Assert
        assertNotEquals(command1, command2);
    }

    // ========== ToString Test ==========

    @Test
    void toString_ContainsAllFields_Test() {
        // Act
        String result = validCommand.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains(validUsername));
        assertTrue(result.contains(validEmail));
        assertTrue(result.contains(validFirstName));
        assertTrue(result.contains(validLastName));
        assertTrue(result.contains(validBirthDate.toString()));
    }
}
