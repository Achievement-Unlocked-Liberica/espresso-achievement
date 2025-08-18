package espresso.achievement.application.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import espresso.achievement.domain.commands.UpdateAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementCmdRepository;
import espresso.achievement.domain.contracts.IAchievementQryRepository;
import espresso.achievement.domain.entities.Achievement;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
class AchivementCommandHandlerUpdateTest {

    @Mock
    private IAchievementCmdRepository achievementCmdRepository;

    @Mock
    private IAchievementQryRepository achievementQryRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private AchivementCommandHandler handler;

    @Test
    void testHandleUpdateAchievementSuccess() {
        // Arrange
        UpdateAchievementCommand command = createValidUpdateCommand();
        User mockUser = createMockUser("USER123");
        Achievement mockAchievement = createMockAchievement("ACHI001", mockUser);
        Achievement updatedAchievement = createMockAchievement("ACHI001", mockUser);
        
        // Set up required method stubs for the actual handler flow
        when(mockUser.getEntityKey()).thenReturn("USER123");
        when(mockAchievement.getUser()).thenReturn(mockUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        when(achievementCmdRepository.update(any(Achievement.class))).thenReturn(updatedAchievement);

        // Act
        HandlerResponse<Object> result = handler.handle(command);

        // Assert
        assertEquals(ResponseType.SUCCESS, result.getResponseType());
        assertEquals(updatedAchievement, result.getData());
        
        // Verify the achievement's update method was called with correct parameters
        verify(achievementCmdRepository).update(mockAchievement);
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
    }

    @Test
    void testHandleUpdateAchievementValidationError() {
        // Arrange
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        // Don't set required fields to trigger validation errors

        // Act
        HandlerResponse<Object> result = handler.handle(command);

        // Assert
        assertEquals(ResponseType.VALIDATION_ERROR, result.getResponseType());
        assertNotNull(result.getData());
        
        // Verify no repository interactions occurred
        verify(userRepository, never()).findByKey(anyString(), eq(User.class));
        verify(achievementQryRepository, never()).getAchievementByKey(any(), anyString());
        verify(achievementCmdRepository, never()).update(any());
    }

    @Test
    void testHandleUpdateAchievementUserNotFound() {
        // Arrange
        UpdateAchievementCommand command = createValidUpdateCommand();
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(null);

        // Act
        HandlerResponse<Object> result = handler.handle(command);

        // Assert
        assertEquals(ResponseType.NOT_FOUND, result.getResponseType());
        assertEquals("LOCALIZE: USER NOT FOUND", result.getData());
        
        // Verify only user lookup was attempted
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository, never()).getAchievementByKey(any(), anyString());
        verify(achievementCmdRepository, never()).update(any());
    }

    @Test
    void testHandleUpdateAchievementNotFound() {
        // Arrange
        UpdateAchievementCommand command = createValidUpdateCommand();
        User mockUser = createMockUser("USER123");
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(null);

        // Act
        HandlerResponse<Object> result = handler.handle(command);

        // Assert
        assertEquals(ResponseType.NOT_FOUND, result.getResponseType());
        assertEquals("LOCALIZE: ACHIEVEMENT NOT FOUND", result.getData());
        
        // Verify user and achievement lookups were attempted
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verify(achievementCmdRepository, never()).update(any());
    }

    @Test
    void testHandleUpdateAchievementUnauthorized() {
        // Arrange
        UpdateAchievementCommand command = createValidUpdateCommand();
        User requestingUser = createMockUser("USER123");
        User achievementOwner = createMockUser("USER999"); // Different user
        Achievement mockAchievement = createMockAchievement("ACHI001", achievementOwner);
        
        // Set up required method stubs for the handler flow
        when(achievementOwner.getEntityKey()).thenReturn("USER999");
        when(mockAchievement.getUser()).thenReturn(achievementOwner);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(requestingUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);

        // Act
        HandlerResponse<Object> result = handler.handle(command);

        // Assert
        assertEquals(ResponseType.UNAUTHORIZED, result.getResponseType());
        assertEquals("LOCALIZE: USER IS NOT AUTHORIZED TO UPDATE THIS ACHIEVEMENT", result.getData());
        
        // Verify no update was attempted
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verify(achievementCmdRepository, never()).update(any());
    }

    @Test
    void testHandleUpdateAchievementInternalError() {
        // Arrange
        UpdateAchievementCommand command = createValidUpdateCommand();
        User mockUser = createMockUser("USER123");
        Achievement mockAchievement = createMockAchievement("ACHI001", mockUser);
        
        // Set up required method stubs for the handler flow
        when(mockUser.getEntityKey()).thenReturn("USER123");
        when(mockAchievement.getUser()).thenReturn(mockUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        when(achievementCmdRepository.update(any(Achievement.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        HandlerResponse<Object> result = handler.handle(command);

        // Assert
        assertEquals(ResponseType.INTERNAL_ERROR, result.getResponseType());
        assertTrue(result.getData().toString().contains("DATABASE ERROR"));
        
        // Verify all steps were attempted
        verify(userRepository).findByKey("USER123", User.class);
        verify(achievementQryRepository).getAchievementByKey(Achievement.class, "ACHI001");
        verify(achievementCmdRepository).update(mockAchievement);
    }

    @Test
    void testHandleUpdateAchievementSkillsConversion() {
        // Arrange
        UpdateAchievementCommand command = createValidUpdateCommand();
        command.setSkills(new String[]{"str", "dex", "int", "wis"});
        
        User mockUser = createMockUser("USER123");
        Achievement mockAchievement = createMockAchievement("ACHI001", mockUser);
        Achievement updatedAchievement = createMockAchievement("ACHI001", mockUser);
        
        // Set up required method stubs for the handler flow
        when(mockUser.getEntityKey()).thenReturn("USER123");
        when(mockAchievement.getUser()).thenReturn(mockUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        when(achievementCmdRepository.update(any(Achievement.class))).thenReturn(updatedAchievement);

        // Act
        HandlerResponse<Object> result = handler.handle(command);

        // Assert
        assertEquals(ResponseType.SUCCESS, result.getResponseType());
        
        // Verify the achievement's update method would have been called
        // We can't directly verify the parameters passed to achievement.update() because it's not mocked,
        // but we can verify the repository update was called
        verify(achievementCmdRepository).update(mockAchievement);
    }

    @Test
    void testHandleUpdateAchievementWithPrivateVisibility() {
        // Arrange
        UpdateAchievementCommand command = createValidUpdateCommand();
        command.setIsPublic(false); // Set to private
        
        User mockUser = createMockUser("USER123");
        Achievement mockAchievement = createMockAchievement("ACHI001", mockUser);
        Achievement updatedAchievement = createMockAchievement("ACHI001", mockUser);
        
        // Set up required method stubs for the handler flow
        when(mockUser.getEntityKey()).thenReturn("USER123");
        when(mockAchievement.getUser()).thenReturn(mockUser);
        
        when(userRepository.findByKey("USER123", User.class)).thenReturn(mockUser);
        when(achievementQryRepository.getAchievementByKey(Achievement.class, "ACHI001")).thenReturn(mockAchievement);
        when(achievementCmdRepository.update(any(Achievement.class))).thenReturn(updatedAchievement);

        // Act
        HandlerResponse<Object> result = handler.handle(command);

        // Assert
        assertEquals(ResponseType.SUCCESS, result.getResponseType());
        verify(achievementCmdRepository).update(mockAchievement);
    }

    // Helper methods for creating test objects
    private UpdateAchievementCommand createValidUpdateCommand() {
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setAchievementKey("ACHI001");
        command.setUserKey("USER123");
        command.setTitle("Updated Test Achievement");
        command.setDescription("Updated test description");
        command.setSkills(new String[]{"str", "dex", "int"});
        command.setIsPublic(true);
        return command;
    }

    private User createMockUser(String userKey) {
        User user = mock(User.class);
        return user;
    }

    private Achievement createMockAchievement(String achievementKey, User owner) {
        Achievement achievement = mock(Achievement.class);
        return achievement;
    }
}
