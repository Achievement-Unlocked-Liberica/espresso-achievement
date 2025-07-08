package espresso.security.application.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.security.domain.commands.RegisterUserCommand;
import espresso.user.domain.contracts.IUserRepository;
import espresso.user.domain.entities.User;

@ExtendWith(MockitoExtension.class)
class RegisterUserCommandHandlerTest {

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private RegisterUserCommandHandler registerUserCommandHandler;

    private RegisterUserCommand validCommand;

    @BeforeEach
    void setUp() {
        validCommand = new RegisterUserCommand("testuser", "TestPass123@", "test@example.com");
    }

    @Test
    void handle_ValidCommand_ShouldReturnSuccessWithUser() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        
        User mockSavedUser = User.createForRegistration("testuser", "test@example.com", "TestPass123@");
        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);

        // Act
        HandlerResponse<Object> response = registerUserCommandHandler.handle(validCommand);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(ResponseType.SUCCESS, response.getResponseType());
        assertNotNull(response.getData());
        assertInstanceOf(User.class, response.getData());

        verify(userRepository).findByUsername("testuser");
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void handle_InvalidCommand_ShouldReturnValidationError() {
        // Arrange
        RegisterUserCommand invalidCommand = new RegisterUserCommand("", "", "");

        // Act
        HandlerResponse<Object> response = registerUserCommandHandler.handle(invalidCommand);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.VALIDATION_ERROR, response.getResponseType());
        assertNotNull(response.getData());

        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void handle_UsernameAlreadyExists_ShouldReturnValidationError() {
        // Arrange
        User existingUser = User.createForRegistration("testuser", "existing@example.com", "TestPass123@");
        when(userRepository.findByUsername("testuser")).thenReturn(existingUser);

        // Act
        HandlerResponse<Object> response = registerUserCommandHandler.handle(validCommand);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.VALIDATION_ERROR, response.getResponseType());
        assertTrue(response.getData().toString().contains("USERNAME ALREADY EXISTS"));

        verify(userRepository).findByUsername("testuser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void handle_EmailAlreadyExists_ShouldReturnValidationError() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(null);
        
        User existingUser = User.createForRegistration("existinguser", "test@example.com", "TestPass123@");
        when(userRepository.findByEmail("test@example.com")).thenReturn(existingUser);

        // Act
        HandlerResponse<Object> response = registerUserCommandHandler.handle(validCommand);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.VALIDATION_ERROR, response.getResponseType());
        assertTrue(response.getData().toString().contains("EMAIL ALREADY EXISTS"));

        verify(userRepository).findByUsername("testuser");
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void handle_RepositoryException_ShouldReturnInternalError() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        HandlerResponse<Object> response = registerUserCommandHandler.handle(validCommand);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(ResponseType.INTERNAL_ERROR, response.getResponseType());
        assertTrue(response.getData().toString().contains("USER REGISTRATION FAILED"));

        verify(userRepository).findByUsername("testuser");
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }
}
