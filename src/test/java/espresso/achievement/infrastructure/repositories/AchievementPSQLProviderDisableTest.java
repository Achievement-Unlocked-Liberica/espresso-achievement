package espresso.achievement.infrastructure.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import espresso.achievement.domain.entities.Achievement;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
class AchievementPSQLProviderDisableTest {

    @Test
    void testUpdateAchievementCallsSaveForDisable() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        inputAchievement.setEnabled(false); // Simulate disabled achievement
        Achievement savedAchievement = createMockAchievement("ACHI001", "Test Achievement");
        savedAchievement.setEnabled(false);
        
        // Mock the save method (which updateAchievement calls)
        when(provider.save(inputAchievement)).thenReturn(savedAchievement);
        
        // Call the actual default method implementation
        when(provider.updateAchievement(inputAchievement)).thenCallRealMethod();

        // Act
        Achievement result = provider.updateAchievement(inputAchievement);

        // Assert
        assertEquals(savedAchievement, result);
        assertFalse(result.isEnabled(), "Achievement should be disabled");
        verify(provider).save(inputAchievement);
    }

    @Test
    void testUpdateAchievementDefaultMethodBehaviorForDisable() {
        // This test verifies that the default method delegates to save() for disabled achievements
        // We use mocking to test the behavior
        
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement disabledAchievement = createMockAchievement("ACHI001", "Disabled Achievement");
        disabledAchievement.setEnabled(false);
        
        Achievement savedResult = createMockAchievement("ACHI001", "Disabled Achievement");
        savedResult.setEnabled(false);
        
        when(provider.save(disabledAchievement)).thenReturn(savedResult);
        when(provider.updateAchievement(disabledAchievement)).thenCallRealMethod();

        // Act
        Achievement result = provider.updateAchievement(disabledAchievement);

        // Assert
        assertNotNull(result);
        assertEquals(savedResult, result);
        assertFalse(result.isEnabled());
        
        // Verify save was called exactly once
        verify(provider, times(1)).save(disabledAchievement);
    }

    @Test
    void testUpdateAchievementWithNullInput() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement nullAchievement = null;
        
        when(provider.updateAchievement(nullAchievement)).thenCallRealMethod();
        when(provider.save(any())).thenThrow(new IllegalArgumentException("Achievement cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> provider.updateAchievement(nullAchievement));
    }

    @Test
    void testUpdateAchievementSaveThrowsException() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement achievement = createMockAchievement("ACHI001", "Test Achievement");
        achievement.setEnabled(false);
        
        when(provider.save(achievement)).thenThrow(new RuntimeException("Database connection failed"));
        when(provider.updateAchievement(achievement)).thenCallRealMethod();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> provider.updateAchievement(achievement));
        
        verify(provider).save(achievement);
    }

    @Test
    void testEnabledPropertyPersistence() {
        // Test that the enabled property is properly handled during save operations
        
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement enabledAchievement = createMockAchievement("ACHI001", "Enabled Achievement");
        enabledAchievement.setEnabled(true);
        
        Achievement disabledAchievement = createMockAchievement("ACHI002", "Disabled Achievement");
        disabledAchievement.setEnabled(false);
        
        // Mock save to return the input (simulating successful persistence)
        when(provider.save(any(Achievement.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(provider.updateAchievement(any(Achievement.class))).thenCallRealMethod();

        // Act
        Achievement enabledResult = provider.updateAchievement(enabledAchievement);
        Achievement disabledResult = provider.updateAchievement(disabledAchievement);

        // Assert
        assertTrue(enabledResult.isEnabled(), "Enabled achievement should remain enabled");
        assertFalse(disabledResult.isEnabled(), "Disabled achievement should remain disabled");
        
        verify(provider).save(enabledAchievement);
        verify(provider).save(disabledAchievement);
    }

    // Helper methods

    private Achievement createMockAchievement(String entityKey, String title) {
        Achievement achievement = new Achievement();
        achievement.setEntityKey(entityKey);
        achievement.setTitle(title);
        achievement.setEnabled(true); // Default to enabled
        
        User mockUser = new User();
        mockUser.setEntityKey("USER123");
        achievement.setUser(mockUser);
        
        return achievement;
    }
}
