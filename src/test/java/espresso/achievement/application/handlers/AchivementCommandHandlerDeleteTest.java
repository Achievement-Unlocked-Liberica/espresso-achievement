package espresso.achievement.application.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import espresso.achievement.domain.commands.DeleteAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementCmdRepository;
import espresso.achievement.domain.contracts.IAchievementQryRepository;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
class AchivementCommandHandlerDeleteTest {

    @Mock(lenient = true)
    private IAchievementCmdRepository achievementCmdRepository;

    @Mock(lenient = true)
    private IAchievementQryRepository achievementQryRepository;

    @Mock(lenient = true)
    private IUserRepository userRepository;

    @InjectMocks
    private AchivementCommandHandler handler;

    @Test
    void testHandleDeleteAchievementSuccess() {
        // Arrange
        DeleteAchievementCommand command = createValidDeleteCommand();
        User mockUser = createMockUser("USER123");
        Achievement mockAchievement = createMockAchievement("ACHI001", mockUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        doNothing().when(achievementCmdRepository).deleteWithDependencies(mockAchievement);
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(ResponseType.SUCCESS, response.getResponseType());
        assertNull(response.getData()); // Delete operation returns null data
        
        // Verify interactions
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verify(achievementCmdRepository).deleteWithDependencies(mockAchievement);
    }

    @Test
    void testHandleDeleteAchievementWithInvalidCommand() {
        // Arrange
        DeleteAchievementCommand command = createInvalidDeleteCommand();
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.VALIDATION_ERROR, response.getResponseType());
        assertNotNull(response.getData());
        // Check that validation error message exists and is meaningful
        String responseData = response.getData().toString();
        assertTrue(responseData.length() > 0, "Response should contain validation error message");
        assertTrue(responseData.contains("7") || responseData.contains("exactly") || 
                   responseData.contains("characters") || responseData.contains("provided"),
                   "Response should mention validation requirements: " + responseData);
        
        // Verify no repository interactions occurred
        verifyNoInteractions(userRepository);
        verifyNoInteractions(achievementQryRepository);
        verifyNoInteractions(achievementCmdRepository);
    }

    @Test
    void testHandleDeleteAchievementWithNonExistentUser() {
        // Arrange
        DeleteAchievementCommand command = createValidDeleteCommand();
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(null);
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.NOT_FOUND, response.getResponseType());
        assertTrue(response.getData().toString().contains("USER NOT FOUND"));
        
        // Verify interactions
        verify(userRepository).findByKey("USER123", User.class);
        verifyNoInteractions(achievementQryRepository);
        verifyNoInteractions(achievementCmdRepository);
    }

    @Test
    void testHandleDeleteAchievementWithNonExistentAchievement() {
        // Arrange
        DeleteAchievementCommand command = createValidDeleteCommand();
        User mockUser = createMockUser("USER123");
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(null);
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(ResponseType.NO_CONTENT, response.getResponseType());
        assertNull(response.getData());
        
        // Verify interactions
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verifyNoInteractions(achievementCmdRepository);
    }

    @Test
    void testHandleDeleteAchievementUnauthorizedUser() {
        // Arrange
        DeleteAchievementCommand command = createValidDeleteCommand();
        User mockUser = createMockUser("USER123");
        User otherUser = createMockUser("USER456"); // Different user
        Achievement mockAchievement = createMockAchievement("ACHI001", otherUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.UNAUTHORIZED, response.getResponseType());
        assertTrue(response.getData().toString().contains("USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT"));
        
        // Verify interactions
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verifyNoInteractions(achievementCmdRepository);
    }

    @Test
    void testHandleDeleteAchievementRepositoryFailure() {
        // Arrange
        DeleteAchievementCommand command = createValidDeleteCommand();
        User mockUser = createMockUser("USER123");
        Achievement mockAchievement = createMockAchievement("ACHI001", mockUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        doThrow(new RuntimeException("Database connection failed")).when(achievementCmdRepository).deleteWithDependencies(mockAchievement);
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.INTERNAL_ERROR, response.getResponseType());
        assertTrue(response.getData().toString().contains("DATABASE CONNECTION FAILED"));
        
        // Verify interactions
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verify(achievementCmdRepository).deleteWithDependencies(mockAchievement);
    }

    @Test
    void testHandleDeleteAchievementWithNullCommand() {
        // Act
        HandlerResponse<Object> response = handler.handle((DeleteAchievementCommand) null);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.INTERNAL_ERROR, response.getResponseType());
        assertNotNull(response.getData());
    }

    // Helper methods

    private DeleteAchievementCommand createValidDeleteCommand() {
        DeleteAchievementCommand command = new DeleteAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        return command;
    }

    private DeleteAchievementCommand createInvalidDeleteCommand() {
        DeleteAchievementCommand command = new DeleteAchievementCommand();
        command.setAchievementKey(""); // Invalid - empty
        command.setUserKey("SHORT"); // Invalid - wrong length
        return command;
    }

    private User createMockUser(String entityKey) {
        User user = mock(User.class);
        lenient().when(user.getEntityKey()).thenReturn(entityKey);
        return user;
    }

    private Achievement createMockAchievement(String entityKey, User user) {
        Achievement achievement = mock(Achievement.class);
        lenient().when(achievement.getEntityKey()).thenReturn(entityKey);
        lenient().when(achievement.getUser()).thenReturn(user);
        lenient().when(achievement.isEnabled()).thenReturn(true);
        return achievement;
    }
}
