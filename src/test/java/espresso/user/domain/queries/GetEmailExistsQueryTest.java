package espresso.user.domain.queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for GetEmailExistsQuery class
 * Tests validation logic and property handling
 */
@ExtendWith(MockitoExtension.class)
class GetEmailExistsQueryTest {

    private GetEmailExistsQuery validQuery;
    private String validEmail;

    @BeforeEach
    void setUp() {
        validEmail = "john.doe@example.com";
        validQuery = new GetEmailExistsQuery(validEmail);
    }

    // ========== Constructor and Getter Tests ==========

    @Test
    void constructor_ValidParameter_Test() {
        // Arrange & Act
        GetEmailExistsQuery query = new GetEmailExistsQuery("test@example.com");

        // Assert
        assertNotNull(query);
        assertEquals("test@example.com", query.getEmail());
    }

    @Test
    void getEmail_ValidEmail_Test() {
        // Act
        String result = validQuery.getEmail();

        // Assert
        assertEquals(validEmail, result);
    }

    // ========== Validation Tests - Valid Scenarios ==========

    @Test
    void validate_ValidEmail_Test() {
        // Act
        Set<String> violations = validQuery.validate();

        // Assert
        assertNotNull(violations);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_SimpleEmail_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user@domain.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_EmailWithNumbers_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user123@domain123.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_EmailWithDots_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("first.last@example.org");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_EmailWithPlus_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user+tag@example.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_EmailWithHyphens_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user-name@sub-domain.example.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_EmailWithUnderscores_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user_name@example_domain.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_LongValidEmail_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("verylongusernameaddress@verylongdomainname.example.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Validation Tests - Invalid Scenarios ==========

    @Test
    void validate_NullEmail_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery(null);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EmptyEmail_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_BlankEmail_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("   ");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EmailWithoutAtSymbol_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("userdomain.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("VALID EMAIL ADDRESS")));
    }

    @Test
    void validate_EmailWithoutDomain_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user@");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("VALID EMAIL ADDRESS")));
    }

    @Test
    void validate_EmailWithoutLocalPart_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("@domain.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("VALID EMAIL ADDRESS")));
    }

    @Test
    void validate_EmailWithMultipleAtSymbols_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user@@domain.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("VALID EMAIL ADDRESS")));
    }

    @Test
    void validate_EmailWithoutTLD_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user@domain");

        // Act
        Set<String> violations = query.validate();

        // Assert
        // Note: Jakarta Bean Validation @Email annotation is lenient and allows this format
        assertTrue(violations.isEmpty(), "Jakarta @Email annotation allows emails without explicit TLD");
    }

    @Test
    void validate_EmailWithSpaces_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user name@domain.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("VALID EMAIL ADDRESS")));
    }

    @Test
    void validate_EmailWithInvalidCharacters_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user<>@domain.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("email") && v.contains("VALID EMAIL ADDRESS")));
    }

    // ========== Edge Case Tests ==========

    @Test
    void validate_EmailWithNumericDomain_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user@123.456.789.012"); // IP address format

        // Act
        Set<String> violations = query.validate();

        // Assert
        // Note: Jakarta Bean Validation @Email annotation allows numeric domains
        assertTrue(violations.isEmpty(), "Jakarta @Email annotation allows numeric domains");
    }

    @Test
    void validate_EmailWithQuotedLocalPart_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("\"user.name\"@example.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty(), "Quoted local part should be valid");
    }

    @Test
    void validate_EmailWithBrackets_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery("user[name]@domain.com");

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty(), "Brackets in local part should be invalid without quotes");
    }

    // ========== Multiple Violation Tests ==========

    @Test
    void validate_MultipleViolations_Test() {
        // Arrange
        GetEmailExistsQuery query = new GetEmailExistsQuery(""); // blank and invalid format

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
        assertTrue(validQuery instanceof GetEmailExistsQuery);
        assertTrue(validQuery instanceof espresso.common.domain.queries.CommonQuery);
    }

    @Test
    void validate_UsesParentValidationMethod_Test() {
        // Arrange
        GetEmailExistsQuery validQuery = new GetEmailExistsQuery("valid@example.com");
        GetEmailExistsQuery invalidQuery = new GetEmailExistsQuery("");

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

    // ========== Common Email Patterns Tests ==========

    @Test
    void validate_CommonEmailPatterns_Test() {
        String[] commonPatterns = {
            "admin@company.com",
            "support@example.org",
            "info@business.net",
            "contact@website.co.uk",
            "no-reply@service.io",
            "user123@mail.google.com",
            "test.email@sub.domain.com"
        };

        for (String pattern : commonPatterns) {
            // Arrange
            GetEmailExistsQuery query = new GetEmailExistsQuery(pattern);

            // Act
            Set<String> violations = query.validate();

            // Assert
            assertTrue(violations.isEmpty(), "Common email pattern '" + pattern + "' should be valid");
        }
    }

    @Test
    void validate_InternationalDomains_Test() {
        String[] internationalEmails = {
            "user@example.com",
            "test@domain.org",
            "admin@site.net",
            "info@company.co.uk",
            "contact@business.de",
            "support@service.fr"
        };

        for (String email : internationalEmails) {
            // Arrange
            GetEmailExistsQuery query = new GetEmailExistsQuery(email);

            // Act
            Set<String> violations = query.validate();

            // Assert
            assertTrue(violations.isEmpty(), "International domain email '" + email + "' should be valid");
        }
    }

    @Test
    void validate_EducationalDomains_Test() {
        String[] eduEmails = {
            "student@university.edu",
            "professor@college.ac.uk",
            "admin@school.edu.au"
        };

        for (String email : eduEmails) {
            // Arrange
            GetEmailExistsQuery query = new GetEmailExistsQuery(email);

            // Act
            Set<String> violations = query.validate();

            // Assert
            assertTrue(violations.isEmpty(), "Educational domain email '" + email + "' should be valid");
        }
    }

    @Test
    void validate_CaseSensitivity_Test() {
        // Email validation should be case-insensitive
        String[] caseVariations = {
            "User@Example.Com",
            "USER@EXAMPLE.COM",
            "user@EXAMPLE.com",
            "User.Name@Example.Org"
        };

        for (String email : caseVariations) {
            // Arrange
            GetEmailExistsQuery query = new GetEmailExistsQuery(email);

            // Act
            Set<String> violations = query.validate();

            // Assert
            assertTrue(violations.isEmpty(), "Email with different case '" + email + "' should be valid");
        }
    }
}
