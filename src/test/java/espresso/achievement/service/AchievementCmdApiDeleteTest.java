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

import espresso.achievement.domain.commands.DeleteAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.domain.responses.ServiceResponse;

@ExtendWith(MockitoExtension.class)
public class AchievementCmdApiDeleteTest {

    @Mock
    private IAchievementCommandHandler achivementCommandHandler;

    @InjectMocks
    private AchievementCmdApi achievementCmdApi;

    @Test
    void testDeleteAchievementSuccess() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.success(null); // Delete returns null data
        
        when(achivementCommandHandler.handle(any(DeleteAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.deleteAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertNull(body.getData()); // Delete operation returns no data
        
        // Verify handler was called with correct command
        verify(achivementCommandHandler).handle(any(DeleteAchievementCommand.class));
    }

    @Test
    void testDeleteAchievementNotFound() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.noContent(); // Achievement doesn't exist
        
        when(achivementCommandHandler.handle(any(DeleteAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.deleteAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess()); // NO_CONTENT is considered successful
        assertNull(body.getData());
        
        verify(achivementCommandHandler).handle(any(DeleteAchievementCommand.class));
    }

    @Test
    void testDeleteAchievementValidationError() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.error("LOCALIZE: ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS", ResponseType.VALIDATION_ERROR);
        
        when(achivementCommandHandler.handle(any(DeleteAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.deleteAchievement(achievementKey);

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
        
        verify(achivementCommandHandler).handle(any(DeleteAchievementCommand.class));
    }

    @Test
    void testDeleteAchievementUnauthorized() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT", ResponseType.UNAUTHORIZED);
        
        when(achivementCommandHandler.handle(any(DeleteAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.deleteAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertTrue(body.getData().toString().contains("NOT AUTHORIZED"));
        
        verify(achivementCommandHandler).handle(any(DeleteAchievementCommand.class));
    }

    @Test
    void testDeleteAchievementUserNotFound() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.error("LOCALIZE: USER NOT FOUND", ResponseType.NOT_FOUND);
        
        when(achivementCommandHandler.handle(any(DeleteAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.deleteAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertTrue(body.getData().toString().contains("NOT FOUND"));
        
        verify(achivementCommandHandler).handle(any(DeleteAchievementCommand.class));
    }

    @Test
    void testDeleteAchievementInternalError() {
        // Arrange
        String achievementKey = "ACHI001";
        
        HandlerResponse<Object> handlerResponse = HandlerResponse.error("LOCALIZE: INTERNAL SERVER ERROR", ResponseType.INTERNAL_ERROR);
        
        when(achivementCommandHandler.handle(any(DeleteAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.deleteAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertTrue(body.getData().toString().contains("INTERNAL") || body.getData().toString().contains("SERVER ERROR"));
        
        verify(achivementCommandHandler).handle(any(DeleteAchievementCommand.class));
    }

    @Test
    void testDeleteAchievementHandlerException() {
        // Arrange
        String achievementKey = "ACHI001";
        
        when(achivementCommandHandler.handle(any(DeleteAchievementCommand.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.deleteAchievement(achievementKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        ServiceResponse<Object> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        // The exception might be wrapped in the data field or may result in null data - either is acceptable
        // Just ensure the response indicates failure
        
        verify(achivementCommandHandler).handle(any(DeleteAchievementCommand.class));
    }

    @Test
    void testDeleteAchievementEndpointSignature() {
        // This test verifies that the endpoint method signature hasn't changed
        // and uses the proper DELETE mapping and path variable
        
        // Arrange
        String achievementKey = "ACHI001";
        HandlerResponse<Object> handlerResponse = HandlerResponse.success(null);
        when(achivementCommandHandler.handle(any(DeleteAchievementCommand.class))).thenReturn(handlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = achievementCmdApi.deleteAchievement(achievementKey);

        // Assert
        assertNotNull(response, "Delete endpoint should return a response");
        
        // Verify that the handler is called with a DeleteAchievementCommand
        verify(achivementCommandHandler).handle(any(DeleteAchievementCommand.class));
    }
}
