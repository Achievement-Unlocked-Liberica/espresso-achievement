package espresso.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.domain.responses.ServiceResponse;
import espresso.user.domain.commands.AddUserCommand;
import espresso.user.domain.commands.UpdateProfilePictureCommand;
import espresso.user.domain.contracts.IUserCommandHandler;

/**
 * Unit tests for UserCmdApi class
 * Tests all public methods with various scenarios
 */
@ExtendWith(MockitoExtension.class)
class UserCmdApiTest {

    @Mock
    private IUserCommandHandler userCommandHandler;

    @InjectMocks
    private UserCmdApi userCmdApi;

    private AddUserCommand validAddUserCommand;
    private MockMultipartFile validImageFile;
    private MockMultipartFile invalidImageFile;

    @BeforeEach
    void setUp() {
        // Set up valid test commands
        validAddUserCommand = new AddUserCommand(
            "johndoe123",
            "john.doe@example.com",
            "securePassword123",
            "John",
            "Doe",
            LocalDate.of(1990, 5, 15)
        );

        // Create a valid mock image file
        byte[] imageContent = createValidImageBytes();
        validImageFile = new MockMultipartFile(
            "image", 
            "profile.jpg", 
            "image/jpeg", 
            imageContent
        );

        // Create an invalid mock image file (too large)
        byte[] largeImageContent = new byte[6 * 1024 * 1024]; // 6MB - exceeds 5MB limit
        invalidImageFile = new MockMultipartFile(
            "image",
            "large-profile.jpg",
            "image/jpeg",
            largeImageContent
        );
    }

    /**
     * Creates a minimal valid image byte array for testing
     */
    private byte[] createValidImageBytes() {
        // Simple JPEG header bytes to create a minimal valid image
        return new byte[] {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, // JPEG header
            0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01, // JFIF marker
            0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00, // More JFIF data
            (byte) 0xFF, (byte) 0xD9 // JPEG end marker
        };
    }

    // ========== createUser Tests ==========

    @SuppressWarnings("null")
    @Test
    void createUser_ValidCommand_Test() {
        // Arrange
        Object mockUserData = new Object();
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(mockUserData);
        when(userCommandHandler.handle(any(AddUserCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(validAddUserCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(validAddUserCommand);
    }

    @SuppressWarnings("null")
    @Test
    void createUser_ValidationError_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.error(null, ResponseType.VALIDATION_ERROR);
        when(userCommandHandler.handle(any(AddUserCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(validAddUserCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(validAddUserCommand);
    }

    @SuppressWarnings("null")
    @Test
    void createUser_HandlerThrowsException_Test() {
        // Arrange
        when(userCommandHandler.handle(any(AddUserCommand.class)))
            .thenThrow(new RuntimeException("Database connection error"));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(validAddUserCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(validAddUserCommand);
    }

    @SuppressWarnings("null")
    @Test
    void createUser_NullCommand_Test() {
        // Arrange
        AddUserCommand nullCommand = null;

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(nullCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(nullCommand);
    }

    @SuppressWarnings("null")
    @Test
    void createUser_UserAlreadyExists_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.error(null, ResponseType.CONFLICT);
        when(userCommandHandler.handle(any(AddUserCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(validAddUserCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(validAddUserCommand);
    }

    // ========== updateProfilePicture Tests ==========

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_ValidCommand_Test() {
        // Arrange
        Object mockResult = new Object();
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(mockResult);
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(validImageFile, "abc123d");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_UserNotFound_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.error(null, ResponseType.NOT_FOUND);
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(validImageFile, "nonexistent");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_ValidationError_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.error(null, ResponseType.VALIDATION_ERROR);
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(invalidImageFile, "abc123d");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_HandlerThrowsException_Test() {
        // Arrange
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class)))
            .thenThrow(new RuntimeException("File processing error"));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(validImageFile, "abc123d");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_NullImage_Test() {
        // Arrange
        MultipartFile nullImage = null;

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(nullImage, "abc123d");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_NullKey_Test() {
        // Arrange
        String nullKey = null;

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(validImageFile, nullKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    // ========== Edge Case Tests ==========

    @SuppressWarnings("null")
    @Test
    void createUser_HandlerReturnsNullResponse_Test() {
        // Arrange
        when(userCommandHandler.handle(any(AddUserCommand.class))).thenReturn(null);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(validAddUserCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(validAddUserCommand);
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_HandlerReturnsNullResponse_Test() {
        // Arrange
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class))).thenReturn(null);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(validImageFile, "abc123d");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    // ========== Integration with executeCommand Tests ==========

    @SuppressWarnings("null")
    @Test
    void createUser_VerifyExecuteCommandIntegration_Test() {
        // Arrange
        Object mockUserData = new Object();
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(mockUserData);
        when(userCommandHandler.handle(any(AddUserCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(validAddUserCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        
        // Verify that the handler is called exactly once with the correct command
        verify(userCommandHandler, times(1)).handle(eq(validAddUserCommand));
        verifyNoMoreInteractions(userCommandHandler);
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_VerifyExecuteCommandIntegration_Test() {
        // Arrange
        Object mockResult = new Object();
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(mockResult);
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(validImageFile, "abc123d");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        
        // Verify that the handler is called exactly once with the correct command
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
        verifyNoMoreInteractions(userCommandHandler);
    }

    // ========== Additional Boundary Tests ==========

    @SuppressWarnings("null")
    @Test
    void createUser_MinimumValidData_Test() {
        // Arrange
        AddUserCommand minCommand = new AddUserCommand(
            "abcde", // minimum 5 chars
            "a@b.co", // minimum valid email
            "password", // minimum 8 chars password
            "J", // minimum first name
            "D", // minimum last name
            LocalDate.of(2000, 1, 1) // valid past date
        );
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(new Object());
        when(userCommandHandler.handle(any(AddUserCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(minCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(minCommand);
    }

    @SuppressWarnings("null")
    @Test
    void createUser_MaximumValidData_Test() {
        // Arrange
        String longUsername = "a".repeat(50); // max 50 chars
        String longFirstName = "F".repeat(100); // max 100 chars
        String longLastName = "L".repeat(100); // max 100 chars
        
        AddUserCommand maxCommand = new AddUserCommand(
            longUsername,
            "very.long.email.address@example-domain.com",
            "a".repeat(100), // max 100 chars password
            longFirstName,
            longLastName,
            LocalDate.of(1950, 12, 31)
        );
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(new Object());
        when(userCommandHandler.handle(any(AddUserCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(maxCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(maxCommand);
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_DifferentImageFormats_Test() {
        // Arrange
        MockMultipartFile pngFile = new MockMultipartFile(
            "image", 
            "profile.png", 
            "image/png", 
            createValidImageBytes()
        );
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(new Object());
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(pngFile, "abc123d");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_EmptyImageFile_Test() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "image", 
            "empty.jpg", 
            "image/jpeg", 
            new byte[0]
        );
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.error(null, ResponseType.VALIDATION_ERROR);
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(emptyFile, "abc123d");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_InvalidKeyFormat_Test() {
        // Arrange
        String invalidKey = "invalid"; // not 7 characters
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.error(null, ResponseType.VALIDATION_ERROR);
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(validImageFile, invalidKey);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }

    @SuppressWarnings("null")
    @Test
    void createUser_BusinessLogicError_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.error(null, ResponseType.UNPROCESSABLE_ENTITY);
        when(userCommandHandler.handle(any(AddUserCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.createUser(validAddUserCommand);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(validAddUserCommand);
    }

    @SuppressWarnings("null")
    @Test
    void updateProfilePicture_BusinessLogicError_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.error(null, ResponseType.UNPROCESSABLE_ENTITY);
        when(userCommandHandler.handle(any(UpdateProfilePictureCommand.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userCmdApi.updateProfilePicture(validImageFile, "abc123d");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(userCommandHandler, times(1)).handle(any(UpdateProfilePictureCommand.class));
    }
}
