package espresso.achievement.infrastructure.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import espresso.achievement.domain.entities.Achievement;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
class AchievementPSQLProviderUpdateTest {

    @Test
    void testUpdateAchievementCallsSave() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement inputAchievement = createMockAchievement("ACHI001", "Updated Title");
        Achievement savedAchievement = createMockAchievement("ACHI001", "Updated Title");
        
        // Mock the save method (which updateAchievement calls)
        when(provider.save(inputAchievement)).thenReturn(savedAchievement);
        
        // Call the actual default method implementation
        when(provider.updateAchievement(inputAchievement)).thenCallRealMethod();

        // Act
        Achievement result = provider.updateAchievement(inputAchievement);

        // Assert
        assertEquals(savedAchievement, result);
        verify(provider).save(inputAchievement);
    }

    @Test
    void testUpdateAchievementDefaultMethodBehavior() {
        // This test verifies that the default method delegates to save()
        // We use mocking to test the behavior
        
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement inputAchievement = createMockAchievement("ACHI001", "Updated Title");
        Achievement savedAchievement = createMockAchievement("ACHI001", "Updated Title");
        
        // Mock save method
        when(provider.save(inputAchievement)).thenReturn(savedAchievement);
        // Call real implementation of updateAchievement (which calls save)
        when(provider.updateAchievement(inputAchievement)).thenCallRealMethod();

        // Act
        Achievement result = provider.updateAchievement(inputAchievement);

        // Assert
        assertEquals(savedAchievement, result);
        verify(provider).save(inputAchievement);
    }

    @Test
    void testUpdateAchievementWithNullHandling() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        
        // Mock save to handle null appropriately
        when(provider.save(any())).thenThrow(new IllegalArgumentException("Achievement cannot be null"));
        when(provider.updateAchievement(any())).thenCallRealMethod();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            provider.updateAchievement(null);
        });
        
        verify(provider).save(any());
    }

    @Test
    void testUpdateAchievementForwardsExceptions() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement inputAchievement = createMockAchievement("ACHI001", "Updated Title");
        RuntimeException expectedException = new RuntimeException("Database error");
        
        when(provider.save(inputAchievement)).thenThrow(expectedException);
        when(provider.updateAchievement(inputAchievement)).thenCallRealMethod();

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            provider.updateAchievement(inputAchievement);
        });

        assertEquals("Database error", thrownException.getMessage());
        verify(provider).save(inputAchievement);
    }

    @Test
    void testUpdateAchievementMultipleEntities() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement achievement1 = createMockAchievement("ACHI001", "Title 1");
        Achievement achievement2 = createMockAchievement("ACHI002", "Title 2");
        Achievement saved1 = createMockAchievement("ACHI001", "Updated Title 1");
        Achievement saved2 = createMockAchievement("ACHI002", "Updated Title 2");
        
        when(provider.save(achievement1)).thenReturn(saved1);
        when(provider.save(achievement2)).thenReturn(saved2);
        when(provider.updateAchievement(any())).thenCallRealMethod();

        // Act
        Achievement result1 = provider.updateAchievement(achievement1);
        Achievement result2 = provider.updateAchievement(achievement2);

        // Assert
        assertEquals(saved1, result1);
        assertEquals(saved2, result2);
        verify(provider).save(achievement1);
        verify(provider).save(achievement2);
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
