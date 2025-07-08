package espresso.security.application.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.security.domain.commands.AuthCredentialsCommand;
import espresso.security.domain.entities.JWTAuthToken;
import espresso.security.domain.entities.JWTUserToken;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
public class CredentialsCommandHandlerTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private JWTAuthToken jwtAuthToken;

    @InjectMocks
    private CredentialsCommandHandler credentialsCommandHandler;

    private AuthCredentialsCommand validCommand;
    private User mockUser;
    private JWTUserToken mockToken;

    @BeforeEach
    void setUp() {
        validCommand = new AuthCredentialsCommand("testuser", "testpassword123");
        
        // Create a real User entity with the same password as the command
        mockUser = User.create("testuser", "test@example.com", "testpassword123", 
                              "Test", "User", java.time.LocalDate.of(1990, 1, 1));
        
        mockToken = new JWTUserToken();
        mockToken.setToken("mockJwtToken");
        mockToken.setUsername("testuser");
    }

    @Test
    void handle_ValidCredentials_ReturnsSuccessWithToken() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);
        when(jwtAuthToken.generateToken(mockUser)).thenReturn(mockToken);

        // Act
        HandlerResponse<Object> result = credentialsCommandHandler.handle(validCommand);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(ResponseType.SUCCESS, result.getResponseType());
        assertNotNull(result.getData());
        assertTrue(result.getData() instanceof JWTUserToken);
        
        verify(userRepository).findByUsername("testuser");
        verify(jwtAuthToken).generateToken(mockUser);
    }

    @Test
    void handle_InvalidUsername_ReturnsUnauthorized() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        // Act
        HandlerResponse<Object> result = credentialsCommandHandler.handle(validCommand);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(ResponseType.UNAUTHORIZED, result.getResponseType());
        assertEquals("LOCALIZE: INVALID USERNAME OR PASSWORD", result.getData());
    }

    @Test
    void handle_InvalidPassword_ReturnsUnauthorized() {
        // Arrange
        User userWithDifferentPassword = User.create("testuser", "test@example.com", 
                                                    "differentpassword", "Test", "User", 
                                                    java.time.LocalDate.of(1990, 1, 1));
        when(userRepository.findByUsername("testuser")).thenReturn(userWithDifferentPassword);

        // Act
        HandlerResponse<Object> result = credentialsCommandHandler.handle(validCommand);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(ResponseType.UNAUTHORIZED, result.getResponseType());
        assertEquals("LOCALIZE: INVALID USERNAME OR PASSWORD", result.getData());
    }

    @Test
    void handle_InactiveUser_ReturnsUnauthorized() {
        // Arrange
        mockUser.setActive(false);
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        // Act
        HandlerResponse<Object> result = credentialsCommandHandler.handle(validCommand);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(ResponseType.UNAUTHORIZED, result.getResponseType());
        assertEquals("LOCALIZE: USER ACCOUNT IS INACTIVE", result.getData());
    }

    @Test
    void handle_EmptyUsername_ReturnsValidationError() {
        // Arrange
        AuthCredentialsCommand invalidCommand = new AuthCredentialsCommand("", "testpassword123");

        // Act
        HandlerResponse<Object> result = credentialsCommandHandler.handle(invalidCommand);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(ResponseType.VALIDATION_ERROR, result.getResponseType());
    }
}
