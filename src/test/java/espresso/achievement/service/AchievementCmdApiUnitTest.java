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
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", 
            "test.jpg", 
            "image/jpeg", 
            "test content".getBytes()
        );

        // Mock the handler response
        when(achivementCommandHandler.handleUploadMedia(any(UploadAchievementMediaCommand.class)))
            .thenReturn(HandlerResponse.created("Media uploaded successfully"));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = 
            achievementCmdApi.uploadAchievementMedia(mockFile, achievementKey);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUploadAchievementMediaCommandCreation() {
        // Arrange
        String achievementKey = "ACHI001";
        MockMultipartFile mockFile = new MockMultipartFile(
            "image", 
            "test.jpg", 
            "image/jpeg", 
            "test content".getBytes()
        );

        // This test verifies that the command is created correctly with the path variable
        UploadAchievementMediaCommand command = new UploadAchievementMediaCommand(achievementKey, mockFile);

        // Assert
        assertEquals(achievementKey, command.getAchievementKey());
        assertEquals(mockFile, command.getImage());
    }
}
