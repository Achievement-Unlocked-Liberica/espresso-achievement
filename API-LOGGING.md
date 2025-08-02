# API Logging Configuration

This application includes a configurable API logging feature that provides detailed, colorful logs of API requests and responses.

## Configuration

The API logging can be controlled via the following property:

```properties
common.service.operational.apilogging.enabled=true/false
```

### Environment-Specific Configuration

**Production (default):**
- File: `src/main/resources/application.properties`
- Setting: `common.service.operational.apilogging.enabled=false`
- Reason: Disabled by default for security and performance in production

**Local Development:**
- File: `src/main/resources/application-local.properties`
- Setting: `common.service.operational.apilogging.enabled=true`
- Reason: Enabled for debugging and development

**Dev/Staging Environments:**
- Use environment variables or profile-specific properties files
- Example: `COMMON_SERVICE_OPERATIONAL_APILOGGING_ENABLED=true`

## Logging Output

When enabled, the `@ApiLogger` annotation on API methods will output:

```
API REQUEST:
Timestamp: 2025-07-11 21:35:45.123
Method: SecurityCmdApi.authenticate()
Endpoint: POST /api/cmd/security/auth
Description: User authentication with credentials
Headers:
  Content-Type: application/json
  User-Agent: curl/7.68.0
Request Body:
  {
    "username": "testuser",
    "password": "***MASKED***"
  }

API RESPONSE:
Method: SecurityCmdApi.authenticate()
Duration: 245ms
Status: SUCCESS
```

## Features

- ✅ **Colorful Output**: Different colors for labels, values, and HTTP methods
- ✅ **Security**: Sensitive headers (Authorization, passwords) are automatically masked
- ✅ **Performance**: Zero overhead when disabled - aspect is completely bypassed
- ✅ **Flexible**: Can be toggled per environment without code changes
- ✅ **Detailed**: Includes timestamps, method info, headers, body, duration, and errors

## Usage

The `@ApiLogger` annotation is already applied to all API endpoints in:
- `SecurityCmdApi` (authentication, registration)
- `UserQryApi` and `UserCmdApi` (user operations)
- `AchievementQryApi` and `AchievementCmdApi` (achievement operations)
- `CommonApi` (health checks)

No additional code changes needed - just configure the property for your environment.

## Implementation Details

- **Aspect**: `ApiLoggerAspect` intercepts methods annotated with `@ApiLogger`
- **Configuration**: `ApiLoggingProperties` manages the enable/disable setting
- **Annotation**: `@ApiLogger` with optional description and log level customization
