package espresso.user.domain.queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for GetUserNameExistsQuery class
 * Tests validation logic and property handling
 */
@ExtendWith(MockitoExtension.class)
class GetUserNameExistsQueryTest {

    private GetUserNameExistsQuery validQuery;
    private String validUsername;

    @BeforeEach
    void setUp() {
        validUsername = "johndoe123"; // Valid username between 5-50 chars
        validQuery = new GetUserNameExistsQuery(validUsername);
    }

    // ========== Constructor and Getter Tests ==========

    @Test
    void constructor_ValidParameter_Test() {
        // Arrange & Act
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("testuser");

        // Assert
        assertNotNull(query);
        assertEquals("testuser", query.getUsername());
    }

    @Test
    void getUsername_ValidUsername_Test() {
        // Act
        String result = validQuery.getUsername();

        // Assert
        assertEquals(validUsername, result);
    }

    // ========== Validation Tests - Valid Scenarios ==========

    @Test
    void validate_ValidUsername_Test() {
        // Act
        Set<String> violations = validQuery.validate();

        // Assert
        assertNotNull(violations);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_MinimumValidLength_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("abcde"); // exactly 5 chars

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_MaximumValidLength_Test() {
        // Arrange
        String maxUsername = "a".repeat(50); // exactly 50 chars
        GetUserNameExistsQuery query = new GetUserNameExistsQuery(maxUsername);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_UsernameWithNumbers_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("user123");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_UsernameWithUnderscores_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("user_name_123");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_UsernameWithMixedCase_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("UserName123");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Validation Tests - Invalid Scenarios ==========

    @Test
    void validate_NullUsername_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery(null);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EmptyUsername_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_BlankUsername_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("   ");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_UsernameTooShort_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("abcd"); // 4 chars - too short

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("BETWEEN 5 AND 50 CHARACTERS")));
    }

    @Test
    void validate_UsernameTooLong_Test() {
        // Arrange
        String longUsername = "a".repeat(51); // 51 chars - too long
        GetUserNameExistsQuery query = new GetUserNameExistsQuery(longUsername);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("username") && v.contains("BETWEEN 5 AND 50 CHARACTERS")));
    }

    // ========== Edge Case Tests ==========

    @Test
    void validate_UsernameWithSpecialCharacters_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("user@123"); // contains @

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty(), "Special characters should be allowed in username");
    }

    @Test
    void validate_UsernameWithSpaces_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("user name"); // contains space

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty(), "Spaces should be allowed in username");
    }

    @Test
    void validate_UsernameWithDashes_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("user-name-123");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_UsernameWithDots_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("user.name.123");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Boundary Value Tests ==========

    @Test
    void validate_BoundaryValues_Test() {
        // Test values around the boundaries of 5-50 characters
        String[] testUsernames = {
            "abcd",                    // 4 chars - invalid
            "abcde",                   // 5 chars - valid
            "a".repeat(50),            // 50 chars - valid
            "a".repeat(51)             // 51 chars - invalid
        };
        
        boolean[] expectedValid = {false, true, true, false};
        
        for (int i = 0; i < testUsernames.length; i++) {
            // Arrange
            GetUserNameExistsQuery query = new GetUserNameExistsQuery(testUsernames[i]);

            // Act
            Set<String> violations = query.validate();

            // Assert
            if (expectedValid[i]) {
                assertTrue(violations.isEmpty(), "Username '" + testUsernames[i] + "' (length: " + testUsernames[i].length() + ") should be valid");
            } else {
                assertFalse(violations.isEmpty(), "Username '" + testUsernames[i] + "' (length: " + testUsernames[i].length() + ") should be invalid");
            }
        }
    }

    // ========== Multiple Violation Tests ==========

    @Test
    void validate_MultipleViolations_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery(""); // blank and wrong length

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        // Should have at least the @NotBlank violation
        assertTrue(violations.stream().anyMatch(v -> v.contains("MUST NOT BE BLANK")));
    }

    // ========== Inheritance Tests ==========

    @Test
    void validate_InheritsFromCommonQuery_Test() {
        // Assert
        assertTrue(validQuery instanceof GetUserNameExistsQuery);
        assertTrue(validQuery instanceof espresso.common.domain.queries.CommonQuery);
    }

    @Test
    void validate_UsesParentValidationMethod_Test() {
        // Arrange
        GetUserNameExistsQuery validQuery = new GetUserNameExistsQuery("validuser");
        GetUserNameExistsQuery invalidQuery = new GetUserNameExistsQuery("");

        // Act
        Set<String> validViolations = validQuery.validate();
        Set<String> invalidViolations = invalidQuery.validate();

        // Assert
        assertTrue(validViolations.isEmpty());
        assertFalse(invalidViolations.isEmpty());
        
        // Verify that the parent validation method is working
        assertNotNull(validQuery.validate());
        assertNotNull(invalidQuery.validate());
    }

    // ========== Common Username Patterns Tests ==========

    @Test
    void validate_CommonUsernamePatterns_Test() {
        String[] commonPatterns = {
            "admin123",
            "test_user",
            "john.doe",
            "user-2024",
            "MyUserName",
            "user123456",
            "first.last.2024"
        };

        for (String pattern : commonPatterns) {
            // Arrange
            GetUserNameExistsQuery query = new GetUserNameExistsQuery(pattern);

            // Act
            Set<String> violations = query.validate();

            // Assert
            assertTrue(violations.isEmpty(), "Common username pattern '" + pattern + "' should be valid");
        }
    }

    @Test
    void validate_UsernameOnlyNumbers_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("12345");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty(), "Username with only numbers should be valid");
    }

    @Test
    void validate_UsernameUnicode_Test() {
        // Arrange
        GetUserNameExistsQuery query = new GetUserNameExistsQuery("user测试");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty(), "Username with unicode characters should be valid");
    }
}
