# Dual Protocol Setup for Espresso Achievement Applica### 4. Swagger/Open### 6. Testing with HTTP Client
Two REST client files are provided:
- **`userRestClient.http`**: HTTPS endpoints for secure testing
- **`userRestClient-HTTP.http`**: HTTP endpoints for mobile/development testingI Configuration
The application includes a custom OpenAPI configuration that:
- Sets both HTTP and HTTPS server URLs for Swagger
- Provides proper API documentation for both protocols
- Enables "Try it out" functionality in both HTTP and HTTPS modes
- Allows switching between protocols in the Swagger UI server dropdown
- Automatically configures security permissions for Swagger endpoints

### 5. Browser Security Warning (HTTPS Only)## Overview
This application has been configured to run with **dual protocol support** - both HTTP and HTTPS simultaneously:
- **HTTPS (Port 8443)**: Secure connections with SSL/TLS encryption
- **HTTP (Port 8090)**: Plain HTTP for mobile testing and development

This setup is perfect for scenarios where you need secure HTTPS for production-like testing while also providing HTTP access for mobile apps that have issues with self-signed certificates.

## Configuration Details

### SSL Certificate
- **Certificate Type**: Self-signed certificate for local development
- **File**: `espresso-local.p12` (PKCS12 format)
- **Location**: `src/main/resources/espresso-local.p12`
- **Password**: `espresso123`
- **Alias**: `espresso-local`
- **Validity**: 365 days from generation

### Server Configuration
- **HTTPS Port**: 8443 (Primary with SSL certificate)
- **HTTP Port**: 8090 (Secondary for mobile testing)
- **Certificate Subject**: CN=localhost, OU=Development, O=Espresso, L=Local, ST=Development, C=US

## How to Use

### 1. Starting the Application
```bash
mvnw.cmd spring-boot:run
```

### 2. Accessing the Application

#### HTTPS Access (Secure - Port 8443)
- **Base URL**: `https://localhost:8443`
- **API Endpoints**: `https://localhost:8443/api/...`
- **Swagger UI**: `https://localhost:8443/swagger-ui.html`
- **API Docs**: `https://localhost:8443/api-docs`
- **Health Check**: `https://localhost:8443/actuator/health`

#### HTTP Access (Mobile-friendly - Port 8090)
- **Base URL**: `http://localhost:8090`
- **API Endpoints**: `http://localhost:8090/api/...`
- **Swagger UI**: `http://localhost:8090/swagger-ui.html`
- **API Docs**: `http://localhost:8090/api-docs`
- **Health Check**: `http://localhost:8090/actuator/health`

### 3. Mobile App Testing
For mobile applications that have issues with self-signed certificates:
- Use the **HTTP endpoints** on port 8090
- No SSL certificate warnings or errors
- Full API functionality available
- Use `userRestClient-HTTP.http` for testing HTTP endpoints

### 4. Swagger/OpenAPI Configuration
The application includes a custom OpenAPI configuration that:
- Sets the correct HTTPS server URL for Swagger
- Provides proper API documentation
- Enables "Try it out" functionality in HTTPS mode
- Automatically configures security permissions for Swagger endpoints

### 5. Browser Security Warning
Since this is a self-signed certificate, your browser will show a security warning:
- **Chrome**: Click "Advanced" → "Proceed to localhost (unsafe)"
- **Firefox**: Click "Advanced" → "Accept the Risk and Continue"
- **Edge**: Click "Advanced" → "Continue to localhost (unsafe)"

### 6. Testing with HTTP Client
The `userRestClient.http` file has been updated to use HTTPS endpoints. All requests now use `https://localhost:8443`.

## Security Features

### 1. SSL/TLS Encryption
- All communication between client and server is encrypted
- Prevents man-in-the-middle attacks
- Protects sensitive data like passwords

### 2. Spring Security Configuration
- CSRF protection disabled for API endpoints
- All endpoints are currently public (no authentication required)
- Ready for future authentication implementation

### 3. Password Security
- Passwords are hashed using BCrypt with salt
- Plain text passwords are never stored
- Secure password verification

## Development Notes

### Certificate Regeneration
If you need to regenerate the certificate:
```bash
# Open bash terminal
bash

# Generate new certificate and key
openssl req -newkey rsa:2048 -x509 -keyout espresso-local.key -out espresso-local.crt -days 365 -nodes -subj "/CN=localhost/OU=Development/O=Espresso/L=Local/ST=Development/C=US"

# Convert to PKCS12 format
openssl pkcs12 -export -in espresso-local.crt -inkey espresso-local.key -out espresso-local.p12 -name espresso-local -passout pass:espresso123

# Move to resources directory
mv espresso-local.p12 src/main/resources/
rm espresso-local.crt espresso-local.key
```

### Configuration Files
- **SSL Config**: `application-local.properties`
- **Security Config**: `EspressoSecurityConfig.java`
- **API Requests**: `userRestClient.http`

## Troubleshooting

### Common Issues

1. **Certificate not found**
   - Ensure `espresso-local.p12` is in `src/main/resources/`
   - Check the certificate password in `application-local.properties`

2. **Port already in use**
   - Change the port in `application-local.properties`
   - Kill any process using port 8443

3. **Browser security errors**
   - Accept the security warning for self-signed certificates
   - Add localhost to browser's security exceptions

4. **Swagger UI not loading**
   - Clear browser cache and cookies
   - Ensure you're accessing `https://localhost:8443/swagger-ui.html`
   - Check that the application started without errors
   - Verify the API docs are accessible at `https://localhost:8443/api-docs`
   - **Check for conflicting global exception handlers** - Global exception handlers can interfere with Swagger endpoints

5. **Global Exception Handler Conflicts**
   - If Swagger UI shows errors or doesn't load, check for @ControllerAdvice or @ExceptionHandler classes
   - Ensure exception handlers don't intercept Swagger internal requests
   - Consider excluding Swagger paths from global exception handling

6. **Mixed content warnings**
   - Ensure all requests are using HTTPS
   - Check that the OpenAPI server configuration points to HTTPS

### Logs to Check
- SSL handshake errors
- Port binding errors
- Certificate loading errors

## Production Considerations

For production deployment:
1. Use a proper SSL certificate from a trusted CA
2. Enable proper authentication and authorization
3. Configure security headers
4. Use environment-specific configuration
5. Enable CSRF protection for web forms
6. Implement proper session management

## Files Modified
- `application-local.properties` - SSL and Swagger configuration
- `EspressoSecurityConfig.java` - Security configuration with Swagger endpoints
- `OpenApiConfig.java` - Custom OpenAPI/Swagger configuration for dual protocol
- `DualProtocolConfig.java` - **NEW: Tomcat configuration for HTTP + HTTPS**
- `userRestClient.http` - HTTPS endpoints
- `userRestClient-HTTP.http` - **NEW: HTTP endpoints for mobile testing**
- `espresso-local.p12` - SSL certificate (generated)
- `test-https.sh` - Dual protocol testing script
