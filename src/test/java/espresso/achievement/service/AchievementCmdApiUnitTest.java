package espresso.achievement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import espresso.achievement.domain.commands.UploadAchievementMediaCommand;
import espresso.achievement.domain.commands.UpdateAchievementCommand;
import espresso.achievement.domain.contracts.IAchievementCommandHandler;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ServiceResponse;

@ExtendWith(MockitoExtension.class)
public class AchievementCmdApiUnitTest {

    @Mock
    private IAchievementCommandHandler achivementCommandHandler;

    @InjectMocks
    private AchievementCmdApi achievementCmdApi;

    @Test
    void testUploadAchievementMediaEndpointSignature() {
        // Arrange
        String achievementKey = "ACHI001";
        MockMultipartFile mockFile1 = new MockMultipartFile(
            "images", 
            "test1.jpg", 
            "image/jpeg", 
            "test content 1".getBytes()
        );
        MockMultipartFile mockFile2 = new MockMultipartFile(
            "images", 
            "test2.jpg", 
            "image/jpeg", 
            "test content 2".getBytes()
        );
        MockMultipartFile[] mockFiles = {mockFile1, mockFile2};

        // Mock the handler response
        when(achivementCommandHandler.handle(any(UploadAchievementMediaCommand.class)))
            .thenReturn(HandlerResponse.created("Media uploaded successfully"));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = 
            achievementCmdApi.uploadAchievementMedia(mockFiles, achievementKey);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUploadAchievementMediaCommandCreation() {
        // Arrange
        String achievementKey = "ACHI001";
        String userKey = "USER123";
        MockMultipartFile mockFile1 = new MockMultipartFile(
            "images", 
            "test1.jpg", 
            "image/jpeg", 
            "test content 1".getBytes()
        );
        MockMultipartFile mockFile2 = new MockMultipartFile(
            "images", 
            "test2.jpg", 
            "image/jpeg", 
            "test content 2".getBytes()
        );
        MockMultipartFile[] mockFiles = {mockFile1, mockFile2};

        // This test verifies that the command is created correctly with the path variable
        UploadAchievementMediaCommand command = new UploadAchievementMediaCommand(achievementKey, userKey, mockFiles);

        // Assert
        assertEquals(achievementKey, command.getAchievementKey());
        assertEquals(userKey, command.getUserKey());
        assertEquals(mockFiles, command.getImages());
    }

    @Test
    void testUpdateAchievementEndpointSignature() {
        // Arrange
        String achievementKey = "ACHI001";
        UpdateAchievementCommand updateCommand = new UpdateAchievementCommand();
        updateCommand.setTitle("Updated Achievement Title");
        updateCommand.setDescription("Updated achievement description");
        updateCommand.setSkills(new String[]{"str", "dex", "int"});
        updateCommand.setIsPublic(true);

        // Mock the handler response
        when(achivementCommandHandler.handle(any(UpdateAchievementCommand.class)))
            .thenReturn(HandlerResponse.success("Achievement updated successfully"));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = 
            achievementCmdApi.updateAchievement(achievementKey, updateCommand);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdateAchievementCommandSetup() {
        // Arrange
        String achievementKey = "ACHI001";
        UpdateAchievementCommand command = new UpdateAchievementCommand();
        command.setTitle("Updated Achievement Title");
        command.setDescription("Updated achievement description");
        command.setSkills(new String[]{"str", "dex", "int"});
        command.setIsPublic(false);

        // Act - simulate what the controller does
        command.setAchievementKey(achievementKey);
        command.setUserKey("USER123"); // This would come from getAuthenticatedUserKey()

        // Assert
        assertEquals(achievementKey, command.getAchievementKey());
        assertEquals("USER123", command.getUserKey());
        assertEquals("Updated Achievement Title", command.getTitle());
        assertEquals("Updated achievement description", command.getDescription());
        assertArrayEquals(new String[]{"str", "dex", "int"}, command.getSkills());
        assertFalse(command.getIsPublic());
    }

    @Test
    void testUpdateAchievementWithValidationError() {
        // Arrange
        String achievementKey = "ACHI001";
        UpdateAchievementCommand updateCommand = new UpdateAchievementCommand();
        // Don't set required fields to trigger validation error
        
        // Mock the handler response for validation error
        when(achivementCommandHandler.handle(any(UpdateAchievementCommand.class)))
            .thenReturn(HandlerResponse.error("Validation failed", espresso.common.domain.responses.ResponseType.VALIDATION_ERROR));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = 
            achievementCmdApi.updateAchievement(achievementKey, updateCommand);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
