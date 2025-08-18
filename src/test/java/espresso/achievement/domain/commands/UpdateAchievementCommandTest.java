package espresso.achievement.domain.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

class UpdateAchievementCommandTest {

    @Test
    void testValidateWithValidData() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{"str", "dex", "int"});
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        // Should have no skill-related errors (may have other validation errors from parent)
        boolean hasSkillErrors = errors.stream().anyMatch(error -> error.contains("skills["));
        assertFalse(hasSkillErrors, "Should not have skill validation errors for valid skills");
    }

    @Test
    void testValidateWithInvalidSkills() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{"str", "invalid", "dex", "badskill"});
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        // Should have 2 skill-related errors
        long skillErrorCount = errors.stream().filter(error -> error.contains("skills[")).count();
        assertEquals(2, skillErrorCount, "Should have exactly 2 skill validation errors");
        
        // Check specific error messages
        assertTrue(errors.stream().anyMatch(error -> error.contains("invalid")), "Should contain error for 'invalid' skill");
        assertTrue(errors.stream().anyMatch(error -> error.contains("badskill")), "Should contain error for 'badskill' skill");
    }

    @Test
    void testValidateWithMixedCaseSkills() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{"STR", "Dex", "INT"});
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        // Should have no skill-related errors (case should be normalized)
        boolean hasSkillErrors = errors.stream().anyMatch(error -> error.contains("skills["));
        assertFalse(hasSkillErrors, "Should not have skill validation errors for mixed case valid skills");
    }

    @Test
    void testValidateWithNullAndEmptySkills() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{"str", null, "", "  ", "dex"});
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        // Should have no skill-related errors (null and empty strings should be ignored)
        boolean hasSkillErrors = errors.stream().anyMatch(error -> error.contains("skills["));
        assertFalse(hasSkillErrors, "Should not have skill validation errors when nulls and empty strings are present");
    }

    @Test
    void testValidateCallsParentValidation() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        // Don't set required fields to trigger parent validation errors
        command.setSkills(new String[]{"str", "dex"});
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        // Should have validation errors from parent (missing required fields)
        assertFalse(errors.isEmpty(), "Should have validation errors from parent class");
        
        // Should NOT have skill errors since skills are valid
        boolean hasSkillErrors = errors.stream().anyMatch(error -> error.contains("skills["));
        assertFalse(hasSkillErrors, "Should not have skill validation errors for valid skills");
    }

    @Test
    void testValidateWithEmptyAchievementKey() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey(""); // Empty achievement key
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{"str", "dex"});
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for empty achievement key");
        assertTrue(errors.stream().anyMatch(error -> error.contains("ACHIEVEMENT KEY MUST BE PROVIDED")), 
                   "Should contain error for empty achievement key");
    }

    @Test
    void testValidateWithInvalidAchievementKeyLength() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("SHORT"); // Less than 7 characters
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{"str", "dex"});
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for invalid achievement key length");
        assertTrue(errors.stream().anyMatch(error -> error.contains("ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS")), 
                   "Should contain error for achievement key length");
    }

    @Test
    void testValidateWithEmptyTitle() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle(""); // Empty title
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{"str", "dex"});
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for empty title");
        assertTrue(errors.stream().anyMatch(error -> error.contains("A TITLE MUST BE PROVIDED")), 
                   "Should contain error for empty title");
    }

    @Test
    void testValidateWithEmptyDescription() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription(""); // Empty description
        command.setSkills(new String[]{"str", "dex"});
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for empty description");
        assertTrue(errors.stream().anyMatch(error -> error.contains("A DESCRIPTION MUST BE PROVIDED")), 
                   "Should contain error for empty description");
    }

    @Test
    void testValidateWithTooManySkills() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{"str", "dex", "int", "wis", "cha", "con", "per", "luck"}); // 8 skills (max is 7)
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for too many skills");
        // Note: The validation should catch the array size limit
    }

    @Test
    void testValidateWithNoSkills() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{}); // No skills
        command.setIsPublic(true);
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        assertFalse(errors.isEmpty(), "Should have validation errors for no skills");
        assertTrue(errors.stream().anyMatch(error -> error.contains("AT LEAST ONE SKILL MUST BE PROVIDED")), 
                   "Should contain error for no skills provided");
    }

    @Test
    void testConstructorAndGettersSetters() {
        // Arrange & Act
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description for achievement");
        command.setSkills(new String[]{"str", "dex", "int"});
        command.setIsPublic(false);
        
        // Assert
        assertEquals("ACHI001", command.getAchievementKey());
        assertEquals("USER123", command.getUserKey());
        assertEquals("Updated Test Achievement", command.getTitle());
        assertEquals("Updated test description for achievement", command.getDescription());
        assertArrayEquals(new String[]{"str", "dex", "int"}, command.getSkills());
        assertFalse(command.getIsPublic());
    }

    @Test
    void testDefaultIsPublicValue() {
        // Arrange & Act
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        
        // Assert
        assertTrue(command.getIsPublic(), "Default value for isPublic should be true");
    }
}
