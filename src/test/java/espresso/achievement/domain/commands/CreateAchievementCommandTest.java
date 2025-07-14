package espresso.achievement.domain.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import java.util.Date;

class CreateAchievementCommandTest {

    @Test
    void testValidateWithValidSkills() {
        // Arrange
        CreateAchivementCommand command = new CreateAchivementCommand();
        command.setUserKey("USER123");
        command.setTitle("Test Achievement");
        command.setDescription("Test Description");
        command.setCompletedDate(new Date());
        command.setSkills(new String[]{"str", "dex", "int"});
        
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
        CreateAchivementCommand command = new CreateAchivementCommand();
        command.setUserKey("USER123");
        command.setTitle("Test Achievement");
        command.setDescription("Test Description");
        command.setCompletedDate(new Date());
        command.setSkills(new String[]{"str", "invalid", "dex", "badskill"});
        
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
        CreateAchivementCommand command = new CreateAchivementCommand();
        command.setUserKey("USER123");
        command.setTitle("Test Achievement");
        command.setDescription("Test Description");
        command.setCompletedDate(new Date());
        command.setSkills(new String[]{"STR", "Dex", "INT"});
        
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
        CreateAchivementCommand command = new CreateAchivementCommand();
        command.setUserKey("USER123");
        command.setTitle("Test Achievement");
        command.setDescription("Test Description");
        command.setCompletedDate(new Date());
        command.setSkills(new String[]{"str", null, "", "  ", "dex"});
        
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
        CreateAchivementCommand command = new CreateAchivementCommand();
        // Don't set required fields to trigger parent validation errors
        command.setSkills(new String[]{"str", "dex"});
        
        // Act
        Set<String> errors = command.validate();
        
        // Assert
        // Should have validation errors from parent (missing required fields)
        assertFalse(errors.isEmpty(), "Should have validation errors from parent class");
        
        // Should NOT have skill errors since skills are valid
        boolean hasSkillErrors = errors.stream().anyMatch(error -> error.contains("skills["));
        assertFalse(hasSkillErrors, "Should not have skill validation errors for valid skills");
    }
}
