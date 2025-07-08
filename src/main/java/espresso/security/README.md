# Security Module

This module contains authentication and authorization functionality for the Espresso Achievement application.

## Features

- JWT-based authentication
- User credential validation
- Secure password verification
- Token generation with user information
- **Automatic JWT token validation for all API endpoints**
- **Authorization header-based security**

## Components

### Commands
- `AuthCredentialsCommand`: Validates and holds user login credentials (username and password)

### Entities
- `JWTUserToken`: Domain entity that encapsulates JWT token information including:
  - Token string
  - Token type (Bearer)
  - Expiration time
  - User information (userKey, username, email, firstName, lastName)
- `JWTAuthToken`: Domain service for JWT token generation and validation

### Application Layer
- `CredentialsCommandHandler`: Processes authentication commands

### Infrastructure Layer
- `JWTAuthenticationFilter`: Intercepts HTTP requests and validates JWT tokens
- `JWTAuthenticationEntryPoint`: Handles authentication errors
- `CustomUserDetailsService`: Loads user details for Spring Security

### Service Layer  
- `SecurityCmdApi`: REST API controller for authentication endpoints

### Contracts
- `ISecurityCommandHandler`: Interface for security command handling

## API Endpoints

### POST /api/cmd/security/auth
Authenticates user credentials and returns a JWT token.

**Request Body:**
```json
{
  "username": "string (5-50 characters)",
  "password": "string (8-100 characters)"
}
```

**Responses:**
- `200 OK`: Authentication successful, returns JWT token with user information
- `400 BAD_REQUEST`: Validation error in request
- `401 UNAUTHORIZED`: Invalid credentials or inactive user account
- `500 INTERNAL_SERVER_ERROR`: Internal error occurred

**Success Response Example:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresAt": "2025-07-07T10:30:00Z",
    "userKey": "ABC123",
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }
}
```

## Configuration

JWT settings can be configured in `application.properties`:

```properties
# JWT Configuration
app.jwt.secret=your-secret-key-here
app.jwt.expiration=86400000  # 24 hours in milliseconds
```

## Security Features

- Passwords are securely hashed using the existing `PasswordService`
- JWT tokens include user claims and expiration via `JWTAuthToken` domain service
- Active user validation
- Comprehensive input validation with localized error messages
- Domain-driven JWT token creation and validation
- **Automatic protection of all `/api/**` endpoints (except `/api/cmd/security/auth`)**
- **JWT token validation on every request via `JWTAuthenticationFilter`**
- **Proper error handling for missing or invalid tokens**

## API Security

### Protected Endpoints
All API endpoints under `/api/**` require a valid JWT token in the Authorization header, except:
- `/api/cmd/security/auth` (authentication endpoint)
- `/swagger-ui/**` (API documentation)
- `/actuator/**` (health checks)

### Authorization Header Format
```
Authorization: Bearer <your-jwt-token-here>
```

### Authentication Flow
1. **Login**: POST to `/api/cmd/security/auth` with username/password
2. **Get Token**: Receive JWT token in response
3. **Use Token**: Include token in Authorization header for all subsequent API calls
4. **Token Validation**: Every request is automatically validated by `JWTAuthenticationFilter`

## Testing

Unit tests are provided for the `CredentialsCommandHandler` covering:
- Valid credential authentication
- Invalid username handling
- Invalid password handling
- Inactive user handling
- Input validation errors
