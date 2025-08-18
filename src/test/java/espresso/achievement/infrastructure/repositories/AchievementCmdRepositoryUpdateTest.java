package espresso.achievement.infrastructure.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import espresso.achievement.domain.entities.Achievement;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
class AchievementCmdRepositoryUpdateTest {

    @Mock
    private AchievementPSQLProvider achievementPSQLProvider;

    @InjectMocks
    private AchievementCmdRepository repository;

    @Test
    void testUpdateAchievementSuccess() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Updated Title");
        Achievement updatedAchievement = createMockAchievement("ACHI001", "Updated Title");
        
        when(achievementPSQLProvider.updateAchievement(inputAchievement)).thenReturn(updatedAchievement);

        // Act
        Achievement result = repository.update(inputAchievement);

        // Assert
        assertNotNull(result);
        assertEquals(updatedAchievement, result);
        verify(achievementPSQLProvider).updateAchievement(inputAchievement);
    }

    @Test
    void testUpdateAchievementWithNullInput() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repository.update(null);
        });

        // Verify provider was never called
        verify(achievementPSQLProvider, never()).updateAchievement(any());
    }

    @Test
    void testUpdateAchievementProviderThrowsException() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Updated Title");
        RuntimeException expectedException = new RuntimeException("Database connection error");
        
        when(achievementPSQLProvider.updateAchievement(inputAchievement)).thenThrow(expectedException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            repository.update(inputAchievement);
        });

        assertEquals("Database connection error", thrownException.getMessage());
        verify(achievementPSQLProvider).updateAchievement(inputAchievement);
    }

    @Test
    void testUpdateAchievementForwardsToProvider() {
        // Arrange
        Achievement achievement1 = createMockAchievement("ACHI001", "Title 1");
        Achievement achievement2 = createMockAchievement("ACHI002", "Title 2");
        Achievement updatedAchievement1 = createMockAchievement("ACHI001", "Updated Title 1");
        Achievement updatedAchievement2 = createMockAchievement("ACHI002", "Updated Title 2");
        
        when(achievementPSQLProvider.updateAchievement(achievement1)).thenReturn(updatedAchievement1);
        when(achievementPSQLProvider.updateAchievement(achievement2)).thenReturn(updatedAchievement2);

        // Act
        Achievement result1 = repository.update(achievement1);
        Achievement result2 = repository.update(achievement2);

        // Assert
        assertEquals(updatedAchievement1, result1);
        assertEquals(updatedAchievement2, result2);
        
        verify(achievementPSQLProvider).updateAchievement(achievement1);
        verify(achievementPSQLProvider).updateAchievement(achievement2);
    }

    @Test
    void testUpdateAchievementReturnsSameInstance() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Updated Title");
        
        // Mock provider to return the same instance (simulating in-place update)
        when(achievementPSQLProvider.updateAchievement(inputAchievement)).thenReturn(inputAchievement);

        // Act
        Achievement result = repository.update(inputAchievement);

        // Assert
        assertSame(inputAchievement, result);
        verify(achievementPSQLProvider).updateAchievement(inputAchievement);
    }

    @Test
    void testUpdateAchievementReturnsDifferentInstance() {
        // Arrange
        Achievement inputAchievement = createMockAchievement("ACHI001", "Original Title");
        Achievement updatedAchievement = createMockAchievement("ACHI001", "Updated Title");
        
        when(achievementPSQLProvider.updateAchievement(inputAchievement)).thenReturn(updatedAchievement);

        // Act
        Achievement result = repository.update(inputAchievement);

        // Assert
        assertNotSame(inputAchievement, result);
        assertEquals(updatedAchievement, result);
        verify(achievementPSQLProvider).updateAchievement(inputAchievement);
    }

    // Helper method to create mock Achievement
    private Achievement createMockAchievement(String key, String title) {
        Achievement achievement = mock(Achievement.class);
        when(achievement.getEntityKey()).thenReturn(key);
        when(achievement.getTitle()).thenReturn(title);
        
        // Mock user
        User mockUser = mock(User.class);
        when(mockUser.getEntityKey()).thenReturn("USER123");
        when(achievement.getUser()).thenReturn(mockUser);
        
        return achievement;
    }
}
