package espresso.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import espresso.common.domain.queries.QuerySizeType;
import espresso.common.domain.responses.HandlerResponse;
import espresso.common.domain.responses.ResponseType;
import espresso.common.domain.responses.ServiceResponse;
import espresso.user.domain.contracts.IUserQueryHandler;
import espresso.user.domain.queries.GetEmailExistsQuery;
import espresso.user.domain.queries.GetUserByKeyQuery;
import espresso.user.domain.queries.GetUserNameExistsQuery;

/**
 * Unit tests for UserQryApi class
 * Tests all public methods with various scenarios
 */
@ExtendWith(MockitoExtension.class)
class UserQryApiTest {

    @Mock
    private IUserQueryHandler usersQueryHandler;

    @InjectMocks
    private UserQryApi userQryApi;

    private GetUserByKeyQuery validUserByKeyQuery;
    private GetUserNameExistsQuery validUsernameExistsQuery;
    private GetEmailExistsQuery validEmailExistsQuery;

    @BeforeEach
    void setUp() {
        // Set up valid test queries
        validUserByKeyQuery = new GetUserByKeyQuery("abc123d", QuerySizeType.sm);
        validUsernameExistsQuery = new GetUserNameExistsQuery("johndoe123");
        validEmailExistsQuery = new GetEmailExistsQuery("john.doe@example.com");
    }

    // ========== getUserByKey Tests ==========

    @SuppressWarnings("null")
    @Test
    void getUserByKey_ValidQuery_Test() {
        // Arrange
        Object mockUserData = new Object();
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(mockUserData);
        when(usersQueryHandler.handle(any(GetUserByKeyQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.getUserByKey(validUserByKeyQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(validUserByKeyQuery);
    }

    @SuppressWarnings("null")
    @Test
    void getUserByKey_UserNotFound_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.error(null, ResponseType.NOT_FOUND);
        when(usersQueryHandler.handle(any(GetUserByKeyQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.getUserByKey(validUserByKeyQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(validUserByKeyQuery);
    }

    @SuppressWarnings("null")
    @Test
    void getUserByKey_HandlerThrowsException_Test() {
        // Arrange
        when(usersQueryHandler.handle(any(GetUserByKeyQuery.class)))
            .thenThrow(new RuntimeException("Database connection error"));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.getUserByKey(validUserByKeyQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(validUserByKeyQuery);
    }

    @SuppressWarnings("null")
    @Test
    void getUserByKey_NullQuery_Test() {
        // Arrange
        GetUserByKeyQuery nullQuery = null;

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.getUserByKey(nullQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(nullQuery);
    }

    // ========== checkUserNameExists Tests ==========

    @SuppressWarnings("null")
    @Test
    void checkUserNameExists_UsernameExists_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(true);
        when(usersQueryHandler.handle(any(GetUserNameExistsQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkUserNameExists(validUsernameExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(true, response.getBody().getData());
        verify(usersQueryHandler, times(1)).handle(validUsernameExistsQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkUserNameExists_UsernameDoesNotExist_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(false);
        when(usersQueryHandler.handle(any(GetUserNameExistsQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkUserNameExists(validUsernameExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(false, response.getBody().getData());
        verify(usersQueryHandler, times(1)).handle(validUsernameExistsQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkUserNameExists_HandlerThrowsException_Test() {
        // Arrange
        when(usersQueryHandler.handle(any(GetUserNameExistsQuery.class)))
            .thenThrow(new IllegalArgumentException("Invalid username format"));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkUserNameExists(validUsernameExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(validUsernameExistsQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkUserNameExists_NullQuery_Test() {
        // Arrange
        GetUserNameExistsQuery nullQuery = null;

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkUserNameExists(nullQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(nullQuery);
    }

    // ========== checkEmailExists Tests ==========

    @SuppressWarnings("null")
    @Test
    void checkEmailExists_EmailExists_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(true);
        when(usersQueryHandler.handle(any(GetEmailExistsQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkEmailExists(validEmailExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(true, response.getBody().getData());
        verify(usersQueryHandler, times(1)).handle(validEmailExistsQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkEmailExists_EmailDoesNotExist_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(false);
        when(usersQueryHandler.handle(any(GetEmailExistsQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkEmailExists(validEmailExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(false, response.getBody().getData());
        verify(usersQueryHandler, times(1)).handle(validEmailExistsQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkEmailExists_HandlerThrowsException_Test() {
        // Arrange
        when(usersQueryHandler.handle(any(GetEmailExistsQuery.class)))
            .thenThrow(new RuntimeException("Email validation service unavailable"));

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkEmailExists(validEmailExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(validEmailExistsQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkEmailExists_NullQuery_Test() {
        // Arrange
        GetEmailExistsQuery nullQuery = null;

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkEmailExists(nullQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(nullQuery);
    }

    // ========== Edge Case Tests ==========

    @SuppressWarnings("null")
    @Test
    void getUserByKey_HandlerReturnsNullResponse_Test() {
        // Arrange
        when(usersQueryHandler.handle(any(GetUserByKeyQuery.class))).thenReturn(null);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.getUserByKey(validUserByKeyQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(validUserByKeyQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkUserNameExists_HandlerReturnsNullResponse_Test() {
        // Arrange
        when(usersQueryHandler.handle(any(GetUserNameExistsQuery.class))).thenReturn(null);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkUserNameExists(validUsernameExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(validUsernameExistsQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkEmailExists_HandlerReturnsNullResponse_Test() {
        // Arrange
        when(usersQueryHandler.handle(any(GetEmailExistsQuery.class))).thenReturn(null);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkEmailExists(validEmailExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(validEmailExistsQuery);
    }

    // ========== Integration with executeQuery Tests ==========

    @SuppressWarnings("null")
    @Test
    void getUserByKey_VerifyExecuteQueryIntegration_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(new Object());
        when(usersQueryHandler.handle(any(GetUserByKeyQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.getUserByKey(validUserByKeyQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        
        // Verify that the handler is called exactly once with the correct query
        verify(usersQueryHandler, times(1)).handle(eq(validUserByKeyQuery));
        verifyNoMoreInteractions(usersQueryHandler);
    }

    @SuppressWarnings("null")
    @Test
    void checkUserNameExists_VerifyExecuteQueryIntegration_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(true);
        when(usersQueryHandler.handle(any(GetUserNameExistsQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkUserNameExists(validUsernameExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        
        // Verify that the handler is called exactly once with the correct query
        verify(usersQueryHandler, times(1)).handle(eq(validUsernameExistsQuery));
        verifyNoMoreInteractions(usersQueryHandler);
    }

    @SuppressWarnings("null")
    @Test
    void checkEmailExists_VerifyExecuteQueryIntegration_Test() {
        // Arrange
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(false);
        when(usersQueryHandler.handle(any(GetEmailExistsQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkEmailExists(validEmailExistsQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        
        // Verify that the handler is called exactly once with the correct query
        verify(usersQueryHandler, times(1)).handle(eq(validEmailExistsQuery));
        verifyNoMoreInteractions(usersQueryHandler);
    }

    // ========== Additional Boundary Tests ==========

    @SuppressWarnings("null")
    @Test
    void getUserByKey_DifferentQuerySizeTypes_Test() {
        // Arrange
        GetUserByKeyQuery xlQuery = new GetUserByKeyQuery("test123", QuerySizeType.xl);
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(new Object());
        when(usersQueryHandler.handle(any(GetUserByKeyQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.getUserByKey(xlQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        verify(usersQueryHandler, times(1)).handle(xlQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkUserNameExists_MinimumValidUsername_Test() {
        // Arrange
        GetUserNameExistsQuery minUsernameQuery = new GetUserNameExistsQuery("abcde"); // 5 chars minimum
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(false);
        when(usersQueryHandler.handle(any(GetUserNameExistsQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkUserNameExists(minUsernameQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(false, response.getBody().getData());
        verify(usersQueryHandler, times(1)).handle(minUsernameQuery);
    }

    @SuppressWarnings("null")
    @Test
    void checkEmailExists_ValidEmailFormat_Test() {
        // Arrange
        GetEmailExistsQuery emailQuery = new GetEmailExistsQuery("test.email+tag@example.com");
        HandlerResponse<Object> mockHandlerResponse = HandlerResponse.success(true);
        when(usersQueryHandler.handle(any(GetEmailExistsQuery.class))).thenReturn(mockHandlerResponse);

        // Act
        ResponseEntity<ServiceResponse<Object>> response = userQryApi.checkEmailExists(emailQuery);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(true, response.getBody().getData());
        verify(usersQueryHandler, times(1)).handle(emailQuery);
    }
}
