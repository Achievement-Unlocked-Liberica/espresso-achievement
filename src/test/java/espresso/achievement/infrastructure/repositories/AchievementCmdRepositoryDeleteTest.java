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
class AchievementCmdRepositoryDeleteTest {

    @Mock
    private AchievementPSQLProvider achievementPSQLProvider;

    @InjectMocks
    private AchievementCmdRepository repository;

    @Test
    void testDeleteWithDependenciesSuccess() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        
        doNothing().when(achievementPSQLProvider).deleteAchievementWithDependencies(inputAchievement);

        // Act
        assertDoesNotThrow(() -> repository.deleteWithDependencies(inputAchievement));

        // Assert
        verify(achievementPSQLProvider).deleteAchievementWithDependencies(inputAchievement);
    }

    @Test
    void testDeleteWithDependenciesWithNullAchievement() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.deleteWithDependencies(null);
        });
        
        assertEquals("The achievement is null", exception.getMessage());
        verifyNoInteractions(achievementPSQLProvider);
    }

    @Test
    void testDeleteWithDependenciesProviderException() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        
        doThrow(new RuntimeException("Database connection failed")).when(achievementPSQLProvider).deleteAchievementWithDependencies(inputAchievement);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> repository.deleteWithDependencies(inputAchievement));
        
        verify(achievementPSQLProvider).deleteAchievementWithDependencies(inputAchievement);
    }

    @Test
    void testDeleteWithDependenciesProperDelegation() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        
        doNothing().when(achievementPSQLProvider).deleteAchievementWithDependencies(inputAchievement);

        // Act
        repository.deleteWithDependencies(inputAchievement);

        // Assert - Verify the method is called exactly once with the correct parameter
        verify(achievementPSQLProvider, times(1)).deleteAchievementWithDependencies(inputAchievement);
        verifyNoMoreInteractions(achievementPSQLProvider);
    }

    // Helper methods

    private Achievement createMockAchievement(String entityKey, String title) {
        Achievement achievement = new Achievement();
        achievement.setEntityKey(entityKey);
        achievement.setTitle(title);
        achievement.setDescription("Test description");
        achievement.setEnabled(true);
        achievement.setActive(true);
        
        // Create a mock user
        User mockUser = new User();
        mockUser.setEntityKey("USER123");
        achievement.setUser(mockUser);
        
        return achievement;
    }
}
