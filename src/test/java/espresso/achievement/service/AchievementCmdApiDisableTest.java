package espresso.achievement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import espresso.achievement.domain.commands.DisableAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.domain.responses.ServiceResponse;
import espresso.achievement.domain.entities.Achievement;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
public class AchievementCmdApiDisableTest {

    @Mock
    private IAchievementCommandHandler achivementCommandHandler;

    @InjectMocks
    private AchievementCmdApi achievementCmdApi;

    @Test
    void testDisableAchievementSuccess() {
        // Arrange
        String achievementKey = "ACHI001";
        
        Achievement disabledAchievement = createMockDisabledAchievement(achievementKey, "USER123");
        HandlerResponse<Object> handlerResponse = HandlerResponse.success(disabledAchievement);
        
        when(achivementCommandHandler.handle(any(DisableAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.disableAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(disabledAchievement, body.getData());
        
        // Verify handler was called
        verify(achivementCommandHandler).handle(any(DisableAchievementCommand.class));
    }

    @Test
    void testDisableAchievementAlreadyDisabled() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.noContent();
        
        when(achivementCommandHandler.handle(any(DisableAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.disableAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // NO_CONTENT responses typically have null body
        
        // Verify handler was called
        verify(achivementCommandHandler).handle(any(DisableAchievementCommand.class));
    }

    @Test
    void testDisableAchievementValidationError() {
        // Arrange
        String achievementKey = "SHORT"; // Invalid key
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.error("LOCALIZE: ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS", ResponseType.VALIDATION_ERROR);
        
        when(achivementCommandHandler.handle(any(DisableAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.disableAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertTrue(body.getData().toString().contains("CHARACTERS") || 
                   body.getData().toString().contains("must be exactly") ||
                   body.getData().toString().contains("must be provided"),
                   "Response should contain validation error message, but got: " + body.getData());
        
        // Verify handler was called
        verify(achivementCommandHandler).handle(any(DisableAchievementCommand.class));
    }

    @Test
    void testDisableAchievementNotFound() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.error("LOCALIZE: ACHIEVEMENT NOT FOUND", ResponseType.NOT_FOUND);
        
        when(achivementCommandHandler.handle(any(DisableAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.disableAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertTrue(body.getData().toString().contains("NOT FOUND"));
        
        // Verify handler was called
        verify(achivementCommandHandler).handle(any(DisableAchievementCommand.class));
    }

    @Test
    void testDisableAchievementUnauthorized() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DISABLE THIS ACHIEVEMENT", ResponseType.UNAUTHORIZED);
        
        when(achivementCommandHandler.handle(any(DisableAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.disableAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertTrue(body.getData().toString().contains("NOT AUTHORIZED"));
        
        // Verify handler was called
        verify(achivementCommandHandler).handle(any(DisableAchievementCommand.class));
    }

    @Test
    void testDisableAchievementInternalError() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.error("LOCALIZE: INTERNAL SERVER ERROR", ResponseType.INTERNAL_ERROR);
        
        when(achivementCommandHandler.handle(any(DisableAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.disableAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertTrue(body.getData().toString().contains("INTERNAL SERVER ERROR"));
        
        // Verify handler was called
        verify(achivementCommandHandler).handle(any(DisableAchievementCommand.class));
    }

    @Test
    void testDisableAchievementHandlerException() {
        // Arrange
        String achievementKey = "ACHI001";
        
        when(achivementCommandHandler.handle(any(DisableAchievementCommand.class))).thenThrow(new RuntimeException("Database connection failed"));

        // Act - The API layer may catch exceptions and return error responses
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.disableAchievement(achievementKey);
        
        // Assert - Check if the exception is handled gracefully or propagated
        assertNotNull(response);
        // The API might return 500 status or handle the exception differently
        assertTrue(response.getStatusCode().is5xxServerError() || response.getStatusCode().is4xxClientError(),
                  "Should return an error status code");
        
        // Verify handler was called
        verify(achivementCommandHandler).handle(any(DisableAchievementCommand.class));
    }

    @Test
    void testDisableAchievementEndpointSignature() {
        // This test verifies that the method signature accepts the correct parameters
        
        // Arrange
        String achievementKey = "ACHI001";
        
        Achievement mockAchievement = createMockDisabledAchievement(achievementKey, "USER123");
        HandlerResponse<Object> handlerResponse = HandlerResponse.success(mockAchievement);
        
        when(achivementCommandHandler.handle(any(DisableAchievementCommand.class))).thenReturn(handlerResponse);

        // Act - This should compile and work without issues
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.disableAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify the handler received the correct command type
        verify(achivementCommandHandler).handle(any(DisableAchievementCommand.class));
    }

    // Helper methods

    private Achievement createMockDisabledAchievement(String achievementKey, String userKey) {
        Achievement achievement = new Achievement();
        achievement.setEntityKey(achievementKey);
        achievement.setTitle("Test Achievement");
        achievement.setDescription("Test Description");
        achievement.setEnabled(false);
        
        User user = new User();
        user.setEntityKey(userKey);
        achievement.setUser(user);
        
        return achievement;
    }
}
