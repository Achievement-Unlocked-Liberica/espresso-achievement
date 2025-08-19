package espresso.achievement.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AchievementDisableTest {

    @Test
    void testDisableMethodSetsEnabledToFalse() {
        // Arrange
        Achievement achievement = new Achievement();
        // Verify the achievement starts as enabled (default should be true)
        assertTrue(achievement.isEnabled(), "Achievement should start as enabled");
        
        // Act
        achievement.disable();
        
        // Assert
        assertFalse(achievement.isEnabled(), "Achievement should be disabled after calling disable()");
    }

    @Test
    void testDisableOnAlreadyDisabledAchievement() {
        // Arrange
        Achievement achievement = new Achievement();
        achievement.disable(); // Disable it first
        assertFalse(achievement.isEnabled(), "Achievement should be disabled");
        
        // Act
        achievement.disable(); // Disable again
        
        // Assert
        assertFalse(achievement.isEnabled(), "Achievement should still be disabled");
    }

    @Test
    void testEnabledPropertyDefaultValue() {
        // Arrange & Act
        Achievement achievement = new Achievement();
        
        // Assert
        assertTrue(achievement.isEnabled(), "New achievement should be enabled by default");
    }

    @Test
    void testEnabledPropertyCanBeSetDirectly() {
        // Arrange
        Achievement achievement = new Achievement();
        
        // Act - Test setting to false
        achievement.setEnabled(false);
        
        // Assert
        assertFalse(achievement.isEnabled(), "Achievement should be disabled when set to false");
        
        // Act - Test setting back to true
        achievement.setEnabled(true);
        
        // Assert
        assertTrue(achievement.isEnabled(), "Achievement should be enabled when set to true");
    }

    @Test
    void testDisableDoesNotAffectOtherProperties() {
        // Arrange
        Achievement achievement = new Achievement();
        achievement.setTitle("Test Achievement");
        achievement.setDescription("Test Description");
        achievement.setActive(true);
        
        String originalTitle = achievement.getTitle();
        String originalDescription = achievement.getDescription();
        boolean originalActive = achievement.isActive();
        
        // Act
        achievement.disable();
        
        // Assert
        assertFalse(achievement.isEnabled(), "Achievement should be disabled");
        assertEquals(originalTitle, achievement.getTitle(), "Title should not change");
        assertEquals(originalDescription, achievement.getDescription(), "Description should not change");
        assertEquals(originalActive, achievement.isActive(), "Active status should not change");
    }
}
