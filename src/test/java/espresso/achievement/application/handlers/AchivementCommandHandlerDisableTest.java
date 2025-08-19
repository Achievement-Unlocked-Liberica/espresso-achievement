package espresso.achievement.application.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import espresso.achievement.domain.commands.DisableAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementCmdRepository;
import espresso.achievement.domain.contracts.IAchievementQryRepository;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
class AchivementCommandHandlerDisableTest {

    @Mock(lenient = true)
    private IAchievementCmdRepository achievementCmdRepository;

    @Mock(lenient = true)
    private IAchievementQryRepository achievementQryRepository;

    @Mock(lenient = true)
    private IUserRepository userRepository;

    @InjectMocks
    private AchivementCommandHandler handler;

    @Test
    void testHandleDisableAchievementSuccess() {
        // Arrange
        DisableAchievementCommand command = createValidDisableCommand();
        User mockUser = createMockUser("USER123");
        Achievement mockAchievement = createMockEnabledAchievement("ACHI001", mockUser);
        Achievement disabledAchievement = createMockDisabledAchievement("ACHI001", mockUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        when(achievementCmdRepository.update(mockAchievement)).thenReturn(disabledAchievement);
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(ResponseType.SUCCESS, response.getResponseType());
        assertEquals(disabledAchievement, response.getData());
        
        // Verify interactions
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verify(mockAchievement).disable();
        verify(achievementCmdRepository).update(mockAchievement);
    }

    @Test
    void testHandleDisableAchievementWithInvalidCommand() {
        // Arrange
        DisableAchievementCommand command = createInvalidDisableCommand();
        
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
    void testHandleDisableAchievementWithNonExistentUser() {
        // Arrange
        DisableAchievementCommand command = createValidDisableCommand();
        
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
    void testHandleDisableAchievementWithNonExistentAchievement() {
        // Arrange
        DisableAchievementCommand command = createValidDisableCommand();
        User mockUser = createMockUser("USER123");
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(null);
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.NOT_FOUND, response.getResponseType());
        assertTrue(response.getData().toString().contains("ACHIEVEMENT NOT FOUND"));
        
        // Verify interactions
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verifyNoInteractions(achievementCmdRepository);
    }

    @Test
    void testHandleDisableAchievementUnauthorizedUser() {
        // Arrange
        DisableAchievementCommand command = createValidDisableCommand();
        User mockUser = createMockUser("USER123");
        User otherUser = createMockUser("OTHER01");
        Achievement mockAchievement = createMockEnabledAchievement("ACHI001", otherUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.UNAUTHORIZED, response.getResponseType());
        assertTrue(response.getData().toString().contains("NOT AUTHORIZED TO DISABLE"));
        
        // Verify interactions
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verifyNoInteractions(achievementCmdRepository);
    }

    @Test
    void testHandleDisableAlreadyDisabledAchievement() {
        // Arrange
        DisableAchievementCommand command = createValidDisableCommand();
        User mockUser = createMockUser("USER123");
        Achievement mockAchievement = createMockDisabledAchievement("ACHI001", mockUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        
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
        // Should not call disable() or update() for already disabled achievement
        verify(mockAchievement, never()).disable();
        verifyNoInteractions(achievementCmdRepository);
    }

    @Test
    void testHandleDisableAchievementRepositoryFailure() {
        // Arrange
        DisableAchievementCommand command = createValidDisableCommand();
        User mockUser = createMockUser("USER123");
        Achievement mockAchievement = createMockEnabledAchievement("ACHI001", mockUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        when(achievementCmdRepository.update(mockAchievement)).thenThrow(new RuntimeException("Database error"));
        
        // Act
        HandlerResponse<Object> response = handler.handle(command);
        
        // Assert - The handler should catch the exception and return an error response
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.INTERNAL_ERROR, response.getResponseType());
        
        // Verify interactions
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verify(mockAchievement).disable();
        verify(achievementCmdRepository).update(mockAchievement);
    }

    // Helper methods

    private DisableAchievementCommand createValidDisableCommand() {
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        return command;
    }

    private DisableAchievementCommand createInvalidDisableCommand() {
        DisableAchievementCommand command = new DisableAchievementCommand();
        command.setAchievementKey(""); // Invalid - empty
        command.setUserKey("SHORT"); // Invalid - wrong length
        return command;
    }

    private User createMockUser(String entityKey) {
        User user = mock(User.class);
        lenient().when(user.getEntityKey()).thenReturn(entityKey);
        return user;
    }

    private Achievement createMockEnabledAchievement(String entityKey, User user) {
        Achievement achievement = mock(Achievement.class);
        lenient().when(achievement.getEntityKey()).thenReturn(entityKey);
        lenient().when(achievement.getUser()).thenReturn(user);
        lenient().when(achievement.isEnabled()).thenReturn(true);
        return achievement;
    }

    private Achievement createMockDisabledAchievement(String entityKey, User user) {
        Achievement achievement = mock(Achievement.class);
        lenient().when(achievement.getEntityKey()).thenReturn(entityKey);
        lenient().when(achievement.getUser()).thenReturn(user);
        lenient().when(achievement.isEnabled()).thenReturn(false);
        return achievement;
    }
}
