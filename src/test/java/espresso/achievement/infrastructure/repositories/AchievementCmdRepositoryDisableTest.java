package espresso.achievement.infrastructure.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import espresso.achievement.domain.entities.Achievement;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
class AchievementCmdRepositoryDisableTest {

    @Mock
    private AchievementPSQLProvider achievementPSQLProvider;

    @InjectMocks
    private AchievementCmdRepository repository;

    @Test
    void testDisableAchievementSuccess() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        inputAchievement.setEnabled(false); // Simulate the achievement being disabled
        Achievement updatedAchievement = createMockAchievement("ACHI001", "Test Achievement");
        updatedAchievement.setEnabled(false);
        
        when(achievementPSQLProvider.updateAchievement(inputAchievement)).thenReturn(updatedAchievement);

        // Act
        Achievement result = repository.update(inputAchievement);

        // Assert
        assertNotNull(result);
        assertEquals(updatedAchievement, result);
        assertFalse(result.isEnabled(), "Achievement should be disabled");
        verify(achievementPSQLProvider).updateAchievement(inputAchievement);
    }

    @Test
    void testDisableAchievementWithNullInput() {
        // Arrange
        Achievement nullAchievement = null;

        // Act - The update method may handle null differently, let's test the actual behavior
        Achievement result = repository.update(nullAchievement);
        
        // Assert - Test what actually happens (might be null or might throw)
        // The exact behavior depends on the implementation
        assertNull(result, "Update with null input should return null");
        
        // Verify provider interaction - it should still be called with null
        verify(achievementPSQLProvider).updateAchievement(nullAchievement);
    }

    @Test
    void testDisableAchievementProviderFailure() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        inputAchievement.setEnabled(false);
        
        when(achievementPSQLProvider.updateAchievement(inputAchievement)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> repository.update(inputAchievement));
        
        // Verify provider interaction
        verify(achievementPSQLProvider).updateAchievement(inputAchievement);
    }

    @Test
    void testUpdateMethodDelegatesProperly() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        Achievement expectedResult = createMockAchievement("ACHI001", "Test Achievement");
        
        when(achievementPSQLProvider.updateAchievement(inputAchievement)).thenReturn(expectedResult);

        // Act
        Achievement result = repository.update(inputAchievement);

        // Assert
        assertEquals(expectedResult, result);
        verify(achievementPSQLProvider, times(1)).updateAchievement(inputAchievement);
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
