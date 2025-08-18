package espresso.achievement.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import espresso.user.domain.entities.User;

class AchievementUpdateTest {

    @Test
    void testUpdateWithPublicVisibility() {
        // Arrange
        Achievement achievement = createTestAchievement();
        String newTitle = "Updated Achievement Title";
        String newDescription = "Updated achievement description with more details";
        List<String> newSkills = Arrays.asList("str", "dex", "int", "wis");
        boolean isPublic = true;

        // Act
        achievement.update(newTitle, newDescription, newSkills, isPublic);

        // Assert
        assertEquals(newTitle, achievement.getTitle());
        assertEquals(newDescription, achievement.getDescription());
        assertEquals(newSkills, achievement.getSkills());
        assertEquals(AchievementVisibilityStatus.EVERYONE, achievement.getAchievementVisibility());
    }

    @Test
    void testUpdateWithPrivateVisibility() {
        // Arrange
        Achievement achievement = createTestAchievement();
        String newTitle = "Private Achievement";
        String newDescription = "This is a private achievement";
        List<String> newSkills = Arrays.asList("con", "cha");
        boolean isPublic = false;

        // Act
        achievement.update(newTitle, newDescription, newSkills, isPublic);

        // Assert
        assertEquals(newTitle, achievement.getTitle());
        assertEquals(newDescription, achievement.getDescription());
        assertEquals(newSkills, achievement.getSkills());
        assertEquals(AchievementVisibilityStatus.PRIVATE, achievement.getAchievementVisibility());
    }

    @Test
    void testUpdateChangesAllFields() {
        // Arrange
        Achievement achievement = createTestAchievement();
        
        // Store original values
        String originalTitle = achievement.getTitle();
        String originalDescription = achievement.getDescription();
        List<String> originalSkills = achievement.getSkills();
        AchievementVisibilityStatus originalVisibility = achievement.getAchievementVisibility();
        
        // New values
        String newTitle = "Completely New Title";
        String newDescription = "Completely new description";
        List<String> newSkills = Arrays.asList("per", "luck");
        boolean isPublic = !originalVisibility.equals(AchievementVisibilityStatus.EVERYONE);

        // Act
        achievement.update(newTitle, newDescription, newSkills, isPublic);

        // Assert - verify all fields changed
        assertNotEquals(originalTitle, achievement.getTitle());
        assertNotEquals(originalDescription, achievement.getDescription());
        assertNotEquals(originalSkills, achievement.getSkills());
        assertNotEquals(originalVisibility, achievement.getAchievementVisibility());
        
        // Verify new values
        assertEquals(newTitle, achievement.getTitle());
        assertEquals(newDescription, achievement.getDescription());
        assertEquals(newSkills, achievement.getSkills());
        assertEquals(isPublic ? AchievementVisibilityStatus.EVERYONE : AchievementVisibilityStatus.PRIVATE, 
                     achievement.getAchievementVisibility());
    }

    @Test
    void testUpdateWithEmptySkillsList() {
        // Arrange
        Achievement achievement = createTestAchievement();
        List<String> emptySkills = Arrays.asList();

        // Act
        achievement.update("New Title", "New Description", emptySkills, true);

        // Assert
        assertEquals("New Title", achievement.getTitle());
        assertEquals("New Description", achievement.getDescription());
        assertEquals(emptySkills, achievement.getSkills());
        assertTrue(achievement.getSkills().isEmpty());
    }

    @Test
    void testUpdateWithSingleSkill() {
        // Arrange
        Achievement achievement = createTestAchievement();
        List<String> singleSkill = Arrays.asList("str");

        // Act
        achievement.update("Single Skill Achievement", "Achievement with only one skill", singleSkill, false);

        // Assert
        assertEquals("Single Skill Achievement", achievement.getTitle());
        assertEquals("Achievement with only one skill", achievement.getDescription());
        assertEquals(singleSkill, achievement.getSkills());
        assertEquals(1, achievement.getSkills().size());
        assertEquals("str", achievement.getSkills().get(0));
        assertEquals(AchievementVisibilityStatus.PRIVATE, achievement.getAchievementVisibility());
    }

    @Test
    void testUpdateWithMaximumSkills() {
        // Arrange
        Achievement achievement = createTestAchievement();
        List<String> maxSkills = Arrays.asList("str", "dex", "int", "wis", "cha", "con", "per");

        // Act
        achievement.update("Max Skills Achievement", "Achievement with maximum skills", maxSkills, true);

        // Assert
        assertEquals("Max Skills Achievement", achievement.getTitle());
        assertEquals("Achievement with maximum skills", achievement.getDescription());
        assertEquals(maxSkills, achievement.getSkills());
        assertEquals(7, achievement.getSkills().size());
        assertEquals(AchievementVisibilityStatus.EVERYONE, achievement.getAchievementVisibility());
    }

    @Test
    void testUpdatePreservesUnchangedFields() {
        // Arrange
        Achievement achievement = createTestAchievement();
        
        // Store values that should not change
        String originalEntityKey = achievement.getEntityKey();
        User originalUser = achievement.getUser();
        Date originalCompletedDate = achievement.getCompletedDate();
        OffsetDateTime originalRegisteredAt = achievement.getRegisteredAt();

        // Act
        achievement.update("New Title", "New Description", Arrays.asList("str", "dex"), true);

        // Assert - verify unchanged fields remain the same
        assertEquals(originalEntityKey, achievement.getEntityKey());
        assertSame(originalUser, achievement.getUser());
        assertEquals(originalCompletedDate, achievement.getCompletedDate());
        assertEquals(originalRegisteredAt, achievement.getRegisteredAt());
    }

    @Test
    void testUpdateMultipleTimes() {
        // Arrange
        Achievement achievement = createTestAchievement();

        // Act - Update multiple times
        achievement.update("First Update", "First description", Arrays.asList("str"), true);
        achievement.update("Second Update", "Second description", Arrays.asList("dex", "int"), false);
        achievement.update("Final Update", "Final description", Arrays.asList("wis", "cha", "con"), true);

        // Assert - verify final state
        assertEquals("Final Update", achievement.getTitle());
        assertEquals("Final description", achievement.getDescription());
        assertEquals(Arrays.asList("wis", "cha", "con"), achievement.getSkills());
        assertEquals(AchievementVisibilityStatus.EVERYONE, achievement.getAchievementVisibility());
    }

    @Test
    void testUpdateVisibilityToggling() {
        // Arrange
        Achievement achievement = createTestAchievement();
        
        // Ensure starting state
        achievement.update("Test", "Test", Arrays.asList("str"), true);
        assertEquals(AchievementVisibilityStatus.EVERYONE, achievement.getAchievementVisibility());

        // Act & Assert - Toggle to private
        achievement.update("Test", "Test", Arrays.asList("str"), false);
        assertEquals(AchievementVisibilityStatus.PRIVATE, achievement.getAchievementVisibility());

        // Act & Assert - Toggle back to public
        achievement.update("Test", "Test", Arrays.asList("str"), true);
        assertEquals(AchievementVisibilityStatus.EVERYONE, achievement.getAchievementVisibility());
    }

    // Helper method to create a test achievement
    private Achievement createTestAchievement() {
        // Create a mock user
        User user = new User();
        user.setEntityKey("USER123");
        user.setEmail("test@example.com");

        // Create achievement using the static factory method
        Achievement achievement = Achievement.create(
                "Original Title",
                "Original description",
                new Date(),
                true,
                user,
                Arrays.asList("str", "dex")
        );

        return achievement;
    }
}
