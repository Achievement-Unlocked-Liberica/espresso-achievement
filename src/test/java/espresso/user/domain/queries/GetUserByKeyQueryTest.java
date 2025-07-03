package espresso.user.domain.queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import espresso.common.domain.queries.QuerySizeType;

/**
 * Unit tests for GetUserByKeyQuery class
 * Tests validation logic and property handling
 */
@ExtendWith(MockitoExtension.class)
class GetUserByKeyQueryTest {

    private GetUserByKeyQuery validQuery;
    private String validEntityKey;
    private QuerySizeType validQuerySize;

    @BeforeEach
    void setUp() {
        validEntityKey = "abc123d"; // 7 characters
        validQuerySize = QuerySizeType.sm;
        validQuery = new GetUserByKeyQuery(validEntityKey, validQuerySize);
    }

    // ========== Constructor and Getter Tests ==========

    @Test
    void constructor_ValidParameters_Test() {
        // Arrange & Act
        GetUserByKeyQuery query = new GetUserByKeyQuery("test123", QuerySizeType.lg);

        // Assert
        assertNotNull(query);
        assertEquals("test123", query.getEntityKey());
        assertEquals(QuerySizeType.lg, query.getSize());
    }

    @Test
    void getEntityKey_ValidKey_Test() {
        // Act
        String result = validQuery.getEntityKey();

        // Assert
        assertEquals(validEntityKey, result);
    }

    @Test
    void getSize_ValidSize_Test() {
        // Act
        QuerySizeType result = validQuery.getSize();

        // Assert
        assertEquals(validQuerySize, result);
    }

    // ========== Validation Tests - Valid Scenarios ==========

    @Test
    void validate_ValidQuery_Test() {
        // Act
        Set<String> violations = validQuery.validate();

        // Assert
        assertNotNull(violations);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_MinimumValidLength_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("1234567", QuerySizeType.sm);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_MaximumValidLength_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("abcdefg", QuerySizeType.xl);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_DifferentQuerySizeTypes_Test() {
        // Test all QuerySizeType values
        QuerySizeType[] sizes = {QuerySizeType.sm, QuerySizeType.md, QuerySizeType.lg, QuerySizeType.xl};
        
        for (QuerySizeType size : sizes) {
            // Arrange
            GetUserByKeyQuery query = new GetUserByKeyQuery("valid12", size);

            // Act
            Set<String> violations = query.validate();

            // Assert
            assertTrue(violations.isEmpty(), "Validation should pass for QuerySizeType: " + size);
        }
    }

    // ========== Validation Tests - Invalid Scenarios ==========

    @Test
    void validate_NullEntityKey_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery(null, QuerySizeType.sm);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("entityKey") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EmptyEntityKey_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("", QuerySizeType.sm);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("entityKey") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_BlankEntityKey_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("   ", QuerySizeType.sm);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("entityKey") && v.contains("MUST NOT BE BLANK")));
    }

    @Test
    void validate_EntityKeyTooShort_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("abc12", QuerySizeType.sm); // 5 chars - too short

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("entityKey") && v.contains("EXACTLY 7 CHARACTERS")));
    }

    @Test
    void validate_EntityKeyTooLong_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("abcdefgh", QuerySizeType.sm); // 8 chars - too long

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.contains("entityKey") && v.contains("EXACTLY 7 CHARACTERS")));
    }

    @Test
    void validate_EntityKeyExactlySevenCharacters_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("exact17", QuerySizeType.sm); // exactly 7 chars

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Edge Case Tests ==========

    @Test
    void validate_NullQuerySize_Test() {
        // Arrange - null QuerySizeType should be allowed
        GetUserByKeyQuery query = new GetUserByKeyQuery("valid12", null);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty(), "Null QuerySizeType should be allowed");
    }

    @Test
    void validate_EntityKeyWithSpecialCharacters_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("abc@123", QuerySizeType.sm);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty(), "Special characters should be allowed in entity key");
    }

    @Test
    void validate_EntityKeyWithNumbers_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("1234567", QuerySizeType.sm);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_EntityKeyWithMixedCase_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("AbC123d", QuerySizeType.sm);

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertTrue(violations.isEmpty());
    }

    // ========== Multiple Violation Tests ==========

    @Test
    void validate_MultipleViolations_Test() {
        // Arrange
        GetUserByKeyQuery query = new GetUserByKeyQuery("", QuerySizeType.sm); // blank and wrong length

        // Act
        Set<String> violations = query.validate();

        // Assert
        assertFalse(violations.isEmpty());
        // Should have at least the @NotBlank violation
        assertTrue(violations.stream().anyMatch(v -> v.contains("MUST NOT BE BLANK")));
    }

    // ========== Boundary Value Tests ==========

    @Test
    void validate_BoundaryValues_Test() {
        // Test values around the boundary of 7 characters
        String[] testKeys = {
            "123456",   // 6 chars - invalid
            "1234567",  // 7 chars - valid
            "12345678"  // 8 chars - invalid
        };
        
        boolean[] expectedValid = {false, true, false};
        
        for (int i = 0; i < testKeys.length; i++) {
            // Arrange
            GetUserByKeyQuery query = new GetUserByKeyQuery(testKeys[i], QuerySizeType.sm);

            // Act
            Set<String> violations = query.validate();

            // Assert
            if (expectedValid[i]) {
                assertTrue(violations.isEmpty(), "Key '" + testKeys[i] + "' should be valid");
            } else {
                assertFalse(violations.isEmpty(), "Key '" + testKeys[i] + "' should be invalid");
            }
        }
    }

    // ========== Inheritance Tests ==========

    @Test
    void validate_InheritsFromCommonQuery_Test() {
        // Assert
        assertTrue(validQuery instanceof GetUserByKeyQuery);
        assertTrue(validQuery instanceof espresso.common.domain.queries.CommonQuery);
    }

    @Test
    void validate_UsesParentValidationMethod_Test() {
        // Arrange
        GetUserByKeyQuery validQuery = new GetUserByKeyQuery("valid12", QuerySizeType.sm);
        GetUserByKeyQuery invalidQuery = new GetUserByKeyQuery("", QuerySizeType.sm);

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
}
