package espresso.achievement.infrastructure.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import espresso.achievement.domain.entities.Achievement;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
class AchievementPSQLProviderDeleteTest {

    @Test
    void testDeleteAchievementWithDependenciesCallsAllDeletionMethods() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        
        // Mock all the deletion methods
        doNothing().when(provider).deleteAchievementComments(inputAchievement);
        doNothing().when(provider).deleteAchievementMedia(inputAchievement);
        doNothing().when(provider).delete(inputAchievement);
        
        // Call the actual default method implementation
        doCallRealMethod().when(provider).deleteAchievementWithDependencies(inputAchievement);

        // Act
        provider.deleteAchievementWithDependencies(inputAchievement);

        // Assert - Verify proper deletion order
        verify(provider).deleteAchievementComments(inputAchievement);
        verify(provider).deleteAchievementMedia(inputAchievement);
        verify(provider).delete(inputAchievement);
    }

    @Test
    void testDeleteAchievementWithDependenciesProperDeletionOrder() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        
        // Mock deletion methods
        doNothing().when(provider).deleteAchievementComments(inputAchievement);
        doNothing().when(provider).deleteAchievementMedia(inputAchievement);
        doNothing().when(provider).delete(inputAchievement);
        
        // Call the actual default method implementation
        doCallRealMethod().when(provider).deleteAchievementWithDependencies(inputAchievement);

        // Act
        provider.deleteAchievementWithDependencies(inputAchievement);

        // Assert - Verify the order of operations using InOrder
        org.mockito.InOrder inOrder = inOrder(provider);
        inOrder.verify(provider).deleteAchievementComments(inputAchievement);
        inOrder.verify(provider).deleteAchievementMedia(inputAchievement);
        inOrder.verify(provider).delete(inputAchievement);
    }

    @Test
    void testDeleteAchievementWithDependenciesTransactionalBehavior() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        
        // Mock comments deletion to succeed
        doNothing().when(provider).deleteAchievementComments(inputAchievement);
        
        // Mock media deletion to fail
        doThrow(new RuntimeException("Media deletion failed")).when(provider).deleteAchievementMedia(inputAchievement);
        
        // Call the actual default method implementation
        doCallRealMethod().when(provider).deleteAchievementWithDependencies(inputAchievement);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            provider.deleteAchievementWithDependencies(inputAchievement);
        });

        // Verify that comments deletion was attempted
        verify(provider).deleteAchievementComments(inputAchievement);
        // Verify that media deletion was attempted and failed
        verify(provider).deleteAchievementMedia(inputAchievement);
        // Verify that achievement deletion was never attempted due to failure
        verify(provider, never()).delete(inputAchievement);
    }

    @Test
    void testDeleteAchievementWithDependenciesNullInput() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        
        // Call the actual default method implementation
        doCallRealMethod().when(provider).deleteAchievementWithDependencies(any());

        // Act & Assert
        assertDoesNotThrow(() -> {
            provider.deleteAchievementWithDependencies(null);
        });

        // The method should still attempt to call deletion methods (they may handle null internally)
        verify(provider).deleteAchievementComments(isNull());
        verify(provider).deleteAchievementMedia(isNull());
        verify(provider).delete(isNull());
    }

    @Test
    void testDeleteAchievementWithDependenciesAllMethodsCalledOnce() {
        // Arrange
        AchievementPSQLProvider provider = mock(AchievementPSQLProvider.class);
        Achievement inputAchievement = createMockAchievement("ACHI001", "Test Achievement");
        
        // Mock all deletion methods to succeed
        doNothing().when(provider).deleteAchievementComments(inputAchievement);
        doNothing().when(provider).deleteAchievementMedia(inputAchievement);
        doNothing().when(provider).delete(inputAchievement);
        
        // Call the actual default method implementation
        doCallRealMethod().when(provider).deleteAchievementWithDependencies(inputAchievement);

        // Act
        provider.deleteAchievementWithDependencies(inputAchievement);

        // Assert - Each method should be called exactly once
        verify(provider, times(1)).deleteAchievementComments(inputAchievement);
        verify(provider, times(1)).deleteAchievementMedia(inputAchievement);
        verify(provider, times(1)).delete(inputAchievement);
        verifyNoMoreInteractions(provider);
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
