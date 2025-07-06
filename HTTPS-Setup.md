# HTTPS Setup for Espresso Achievement Application

## Overview
This application has been configured to run securely with HTTPS in the local development environment.

## Configuration Details

### SSL Certificate
- **Certificate Type**: Self-signed certificate for local development
- **File**: `espresso-local.p12` (PKCS12 format)
- **Location**: `src/main/resources/espresso-local.p12`
- **Password**: `espresso123`
- **Alias**: `espresso-local`
- **Validity**: 365 days from generation

### Server Configuration
- **HTTPS Port**: 8443
- **HTTP Port**: Disabled (HTTPS only)
- **Certificate Subject**: CN=localhost, OU=Development, O=Espresso, L=Local, ST=Development, C=US

## How to Use

### 1. Starting the Application
```bash
mvnw.cmd spring-boot:run
```

### 2. Accessing the Application
- **Base URL**: `https://localhost:8443`
- **API Endpoints**: `https://localhost:8443/api/...`
- **Swagger UI**: `https://localhost:8443/swagger-ui.html`
- **API Docs**: `https://localhost:8443/api-docs`

### 3. Browser Security Warning
Since this is a self-signed certificate, your browser will show a security warning:
- **Chrome**: Click "Advanced" → "Proceed to localhost (unsafe)"
- **Firefox**: Click "Advanced" → "Accept the Risk and Continue"
- **Edge**: Click "Advanced" → "Continue to localhost (unsafe)"

### 4. Testing with HTTP Client
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
- `application-local.properties` - SSL configuration
- `EspressoSecurityConfig.java` - Security configuration
- `userRestClient.http` - Updated to HTTPS
- `espresso-local.p12` - SSL certificate (generated)
