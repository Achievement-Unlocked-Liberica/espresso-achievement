# Create Achievement - Acceptance Criteria

## Feature: Create Achievement Endpoint

### User Story
As a player, I want to create a new achievement, so that I can share my accomplishments and track my skills and progress over time.

### Endpoint Details
- **Method**: POST
- **URL**: `/api/cmd/achievement`
- **Authentication**: JWT token required (userKey extracted automatically)
- **Content-Type**: application/json
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Achievement Creation
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** the request payload contains valid achievement data:
```json
{
  "title": "Completed Multi File Upload",
  "description": "Successfully implemented multi file upload for achievement media.",
  "completedDate": "2025-01-15",
  "skills": ["int", "wis", "luc"],
  "isPublic": true
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Validate all command fields using CommonCommand validation
- Perform custom skill validation against allowed skills list
- Look up user by key "ABC1234" in the database
- Create new Achievement entity using Achievement.create() factory method
- Generate unique 7-character alphanumeric achievement key
- Set achievement enabled=true and active=true by default
- Record current timestamp as registeredAt
- Save achievement to database via IAchievementRepository
- Return HTTP 201 Created

**Response Validation**:
- ✅ **Content-Type**: `application/json`
- ✅ **HTTP Status**: `201 Created`
- ✅ **Success Flag**: `true`
- ✅ **Response Data**: Contains entity key
- ✅ **HTTP Status in Response**: `CREATED`

```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "httpStatus": "CREATED"
}
```

### AC2: Authentication Failures

#### AC2.1: Missing JWT Token
**Given** no JWT token is provided in the request
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Take no action (no achievement created)

**Response Validation**:
- ✅ **HTTP Status**: `401 Unauthorized`
- ⚠️ **Note**: 401 responses may not include JSON body depending on Spring Security configuration

#### AC2.2: Invalid JWT Token
**Given** an invalid JWT token is provided
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Take no action (no achievement created)

**Response Validation**:
- ✅ **HTTP Status**: `401 Unauthorized`
- ⚠️ **Note**: 401 responses may not include JSON body depending on Spring Security configuration

#### AC2.3: Expired JWT Token
**Given** an expired JWT token is provided
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Take no action (no achievement created)

**Response Validation**:
- ✅ **HTTP Status**: `401 Unauthorized`
- ⚠️ **Note**: 401 responses may not include JSON body depending on Spring Security configuration

### AC3: User Not Found
**Given** a valid JWT token with userKey "XYZ9999"
**And** no user exists in the system with key "XYZ9999"
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Extract userKey from JWT token
- Attempt user lookup via IUserRepository.findByKey("XYZ9999")
- Repository returns null (no exception thrown)
- Handler detects null user and returns HTTP 404 Not Found
- Take no action (no achievement created)

**Response Validation**:
- ✅ **Content-Type**: `application/json`
- ✅ **HTTP Status**: `404 Not Found`
- ✅ **Success Flag**: `false`
- ✅ **Error Message**: Contains "User not found"
- ✅ **HTTP Status in Response**: `NOT_FOUND`

```json
{
  "success": false,
  "data": "User not found",
  "httpStatus": "NOT_FOUND"
}
```

### AC4: JSR-303 Validation Failures

#### AC4.1: Missing Required Fields
**Given** a valid JWT token with userKey "ABC1234"
**And** the request payload is missing required fields:
```json
{
  "description": "Missing title field",
  "completedDate": "2025-01-15",
  "skills": ["int"]
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Validate request via CommonCommand.validateCommand()
- Detect missing title field
- Return HTTP 400 Bad Request
- Take no action (no achievement created)

**Response Validation**:
- ✅ **Content-Type**: `application/json`
- ✅ **HTTP Status**: `400 Bad Request`
- ✅ **Success Flag**: `false`
- ✅ **Error Message**: Contains "TITLE MUST BE PROVIDED"
- ✅ **HTTP Status in Response**: `BAD_REQUEST`

```json
{
  "success": false,
  "data": "LOCALIZE: A TITLE MUST BE PROVIDED",
  "httpStatus": "BAD_REQUEST"
}
```

#### AC4.2: Empty Required Fields
**Given** a valid JWT token and request payload with empty required fields:
```json
{
  "title": "",
  "description": "   ",
  "completedDate": "2025-01-15",
  "skills": ["int"]
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Validate @NotBlank annotations
- Return HTTP 400 Bad Request
- Return validation errors for title and description
- Take no action (no achievement created)

**Response Validation**:
- ✅ **Content-Type**: `application/json`
- ✅ **HTTP Status**: `400 Bad Request`
- ✅ **Success Flag**: `false`
- ✅ **Error Message**: Validation error details
- ✅ **HTTP Status in Response**: `BAD_REQUEST`

#### AC4.3: Field Length Validation
**Given** a valid JWT token and request payload with fields exceeding length limits:
```json
{
  "title": "This is a very long title that exceeds the maximum allowed length of 200 characters...",
  "description": "Valid description",
  "completedDate": "2025-01-15",
  "skills": ["int"]
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Validate @Size annotations
- Return HTTP 400 Bad Request
- Take no action (no achievement created)

**Response Validation**:
- ✅ **Content-Type**: `application/json`
- ✅ **HTTP Status**: `400 Bad Request`
- ✅ **Success Flag**: `false`
- ✅ **Error Message**: Contains "200 CHARACTERS"
- ✅ **HTTP Status in Response**: `BAD_REQUEST`

```json
{
  "success": false,
  "data": "LOCALIZE: TITLE MUST NOT BE GREATER THAN 200 CHARACTERS",
  "httpStatus": "BAD_REQUEST"
}
```

### AC5: Date Validation Failures

#### AC5.1: Future Completed Date
**Given** a valid JWT token and request payload with future completedDate:
```json
{
  "title": "Future Achievement",
  "description": "This achievement was completed in the future",
  "completedDate": "2025-12-31",
  "skills": ["int"]
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Validate @PastOrPresent annotation
- Return HTTP 400 Bad Request
- Take no action (no achievement created)

**Response Validation**:
- ✅ **Content-Type**: `application/json`
- ✅ **HTTP Status**: `400 Bad Request`
- ✅ **Success Flag**: `false`
- ✅ **Error Message**: Contains "COMPLETED DATE CANNOT BE AFTER TODAY"
- ✅ **HTTP Status in Response**: `BAD_REQUEST`

```json
{
  "success": false,
  "data": "LOCALIZE: THE COMPLETED DATE CANNOT BE AFTER TODAY",
  "httpStatus": "BAD_REQUEST"
}
```

#### AC5.2: Invalid Date Format
**Given** a valid JWT token and request payload with invalid date format:
```json
{
  "title": "Test Achievement",
  "description": "Test description",
  "completedDate": "01/15/2025",
  "skills": ["int"]
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Fail @DateTimeFormat(pattern = "yyyy-MM-dd") validation
- Return HTTP 400 Bad Request
- Return date format validation error
- Take no action (no achievement created)

### AC6: Skills Validation Failures

#### AC6.1: Invalid Skills
**Given** a valid JWT token and request payload with invalid skills:
```json
{
  "title": "Test Achievement",
  "description": "Test description",
  "completedDate": "2025-01-15",
  "skills": ["magic", "invalid", "str"]
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Execute validateCustom() method
- Check each skill against ALLOWED_SKILLS set: ["str", "dex", "con", "wis", "int", "cha", "luc"]
- Return HTTP 400 Bad Request
- Return validation errors:
```json
{
  "success": false,
  "error": "skills[0]: LOCALIZE: INVALID SKILL 'magic'. ALLOWED SKILLS ARE: str, dex, con, wis, int, cha, luc",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-01-15T10:30:00Z"
}
```
- Take no action (no achievement created)

#### AC6.2: Too Many Skills
**Given** a valid JWT token and request payload with more than 7 skills:
```json
{
  "title": "Test Achievement",
  "description": "Test description", 
  "completedDate": "2025-01-15",
  "skills": ["str", "dex", "con", "wis", "int", "cha", "luc", "extra"]
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Validate @Size(min = 1, max = 7) annotation
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: AT LEAST ONE SKILL MUST BE PROVIDED"
- Take no action (no achievement created)

#### AC6.3: No Skills Provided
**Given** a valid JWT token and request payload with empty skills array:
```json
{
  "title": "Test Achievement",
  "description": "Test description",
  "completedDate": "2025-01-15", 
  "skills": []
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Validate @Size(min = 1, max = 7) annotation
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: AT LEAST ONE SKILL MUST BE PROVIDED"
- Take no action (no achievement created)

### AC7: Achievement Visibility Settings

#### AC7.1: Explicit Public Setting
**Given** a valid JWT token and complete achievement data
**And** the isPublic field is explicitly set to true:
```json
{
  "title": "Public Achievement",
  "description": "This achievement is public",
  "completedDate": "2025-01-15",
  "skills": ["str"],
  "isPublic": true
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Create achievement with visibility based on isPublic value
- Set achievementVisibility appropriately in Achievement entity
- Return success response with entity key

#### AC7.2: Explicit Private Setting  
**Given** a valid JWT token and complete achievement data
**And** the isPublic field is explicitly set to false:
```json
{
  "title": "Private Achievement", 
  "description": "This achievement is private",
  "completedDate": "2025-01-15",
  "skills": ["wis"],
  "isPublic": false
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Create achievement with private visibility
- Return success response with entity key

#### AC7.3: Default Visibility (isPublic not specified)
**Given** a valid JWT token and complete achievement data
**And** the isPublic field is not included in the request:
```json
{
  "title": "Default Achievement",
  "description": "This achievement uses default visibility",
  "completedDate": "2025-01-15", 
  "skills": ["int"]
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Use default isPublic value of true (from field default)
- Create achievement with public visibility
- Return success response with entity key

### AC8: Database and System Errors

#### AC8.1: Repository Save Failure
**Given** a valid request that should succeed
**And** a database error occurs during achievement save operation
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Attempt achievementRepository.save(achievement) 
- Catch database exception via AchievementHandlerExceptionPolicy.handleException()
- Return HTTP 500 Internal Server Error
- Return error response with correlation ID:
```json
{
  "success": false,
  "error": "Internal server error occurred",
  "correlationId": "correlation-uuid-123", 
  "timestamp": "2025-01-15T10:30:00Z"
}
```
- Ensure database transaction is rolled back
- Take no action (no achievement persisted)

#### AC8.2: Achievement Entity Creation Failure
**Given** a valid request payload and existing user
**And** Achievement.create() factory method fails
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Catch entity creation exception via AchievementHandlerExceptionPolicy.handleException()
- Return appropriate HTTP error status
- Return error response with correlation ID
- Take no action (no achievement created)

#### AC8.3: Unique Key Generation Failure
**Given** a valid request that should succeed
**And** unique key generation fails during achievement creation  
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Handle key generation failure appropriately
- Retry or return error based on implementation
- Maintain data integrity
- Return appropriate error response with correlation ID

### AC9: Edge Cases and Boundary Conditions

#### AC9.1: Skills Case Sensitivity
**Given** a valid JWT token and request payload with mixed-case skills:
```json
{
  "title": "Case Test Achievement",
  "description": "Testing case sensitivity", 
  "completedDate": "2025-01-15",
  "skills": ["STR", "Dex", "INT"]
}
```
**When** the POST request is made to `/api/cmd/achievement`  
**Then** the system should:
- Normalize skills to lowercase via skill.trim().toLowerCase()
- Validate against ALLOWED_SKILLS set (all lowercase)
- Accept "STR" as "str", "Dex" as "dex", "INT" as "int"
- Create achievement successfully
- Return success response with entity key

#### AC9.2: Skills with Whitespace
**Given** a valid JWT token and request payload with whitespace in skills:
```json
{
  "title": "Whitespace Test Achievement",
  "description": "Testing whitespace handling",
  "completedDate": "2025-01-15", 
  "skills": [" str ", "  dex", "int  "]
}
```
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Trim whitespace from each skill via skill.trim()
- Validate trimmed skills against ALLOWED_SKILLS
- Accept skills with surrounding whitespace
- Create achievement successfully
- Return success response with entity key

#### AC9.3: UserKey Validation from JWT
**Given** a valid JWT token with userKey "USR1234"
**And** the userKey is exactly 7 characters
**And** user exists in system
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Extract userKey from JWT token automatically
- Validate @Size(min = 7, max = 7) annotation on userKey
- Proceed with achievement creation
- Return success response

#### AC9.4: UserKey Wrong Length from JWT
**Given** a JWT token with userKey "TOOLONG123" (10 characters)
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Validate userKey length via @Size annotation  
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS"
- Take no action (no achievement created)

### AC10: Request Flow and Handler Execution

#### AC10.1: Complete Success Flow
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234" 
**And** valid request payload with all required fields
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should execute the following flow:
1. Extract userKey from JWT token automatically
2. Execute CommonCommand.validateCommand() - JSR-303 validation
3. Execute CreateAchievementCommand.validateCustom() - skill validation  
4. Call IUserRepository.findByKey("ABC1234") to verify user exists
5. Call Achievement.create() factory method with validated data
6. Generate unique 7-character achievement key
7. Call IAchievementRepository.save(achievement) to persist
8. Call savedAchievement.toKto() to get entity key response
9. Return HandlerResponse.created(achievementKto)
10. Return HTTP 201 Created with entity key response

#### AC10.2: Exception Handling Flow
**Given** any error occurs during request processing
**When** an exception is thrown
**Then** the system should:
- Catch exception in AchievementHandlerExceptionPolicy.handleException()
- Map exception to appropriate HTTP status code
- Include correlation ID in error response
- Log error with correlation ID for tracing
- Return structured error response
- Ensure no partial data is persisted

#### AC10.3: Validation Sequence
**Given** a request with multiple validation errors
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Execute JSR-303 validations first (@NotBlank, @Size, @PastOrPresent)
- If JSR-303 passes, execute validateCustom() for skill validation
- Return first validation error encountered
- Not proceed to database operations if validation fails

## Implementation Details

### Command Handler: CreateAchievementCommandHandler
- **Base Class**: extends CommonCommandHandler<CreateAchievementCommand>
- **Dependencies**: IAchievementRepository, IUserRepository, AchievementHandlerExceptionPolicy
- **Validation**: Inherits validateCommand() from CommonCommandHandler
- **Custom Validation**: CreateAchievementCommand.validateCustom() for skills
- **Response**: HandlerResponse.created(savedAchievement.toKto())

### Command Model: CreateAchievementCommand
- **Base Class**: extends CommonCommand
- **JWT Extraction**: userKey automatically populated from JWT token
- **Validation Annotations**: @NotBlank, @Size, @PastOrPresent, @DateTimeFormat
- **Custom Logic**: validateCustom() method for skill validation
- **Skill Normalization**: trim() and toLowerCase() applied

### Entity Creation: Achievement.create()
- **Factory Method**: Achievement.create(command, user)
- **Key Generation**: Automatic 7-character alphanumeric entityKey
- **Defaults**: enabled=true, active=true, registeredAt=current timestamp
- **Visibility**: Based on command.isPublic value

### Repository Operations
- **User Lookup**: IUserRepository.findByKey(userKey)
- **Achievement Save**: IAchievementRepository.save(achievement)
- **Transaction**: Atomic operation with rollback on failure

### Error Handling: AchievementHandlerExceptionPolicy
- **Exception Mapping**: Maps domain exceptions to HTTP status codes
- **Correlation Tracking**: Includes correlation ID in all error responses
- **Structured Responses**: Consistent error format across all failures

## Data Validation Rules

### Required Fields (JSR-303)
- `userKey`: 7 characters exactly (from JWT token)
- `title`: 1-200 characters, non-blank
- `description`: 1-1000 characters, non-blank
- `completedDate`: yyyy-MM-dd format, past or present date
- `skills`: 1-7 skills array, non-empty

### Optional Fields
- `isPublic`: Boolean, defaults to true

### Custom Validation (validateCustom)
- **Skills**: Must be from ALLOWED_SKILLS set: ["str", "dex", "con", "wis", "int", "cha", "luc"]
- **Normalization**: Skills trimmed and converted to lowercase before validation
- **Error Format**: "skills[index]: LOCALIZE: INVALID SKILL 'value'. ALLOWED SKILLS ARE: ..."

### Generated Fields
- `entityKey`: 7-character unique identifier (auto-generated)
- `registeredAt`: Current timestamp (auto-generated) 
- `enabled`: true (default)
- `active`: true (default)

## Response Format

### Success Response (HTTP 201 Created)
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "responseType": "CREATED"
}
```

### Error Response Format
```json
{
  "success": false,
  "error": "Error message text",
  "correlationId": "uuid-correlation-id",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

## HTTP Status Codes

### Success Codes
- **201 Created**: Achievement created successfully, returns entity key

### Client Error Codes (4xx)
- **400 Bad Request**: 
  - JSR-303 validation failures (missing/invalid fields, length violations)
  - Custom validation failures (invalid skills)
  - Date format or future date errors
- **401 Unauthorized**: 
  - Missing JWT token
  - Invalid JWT token  
  - Expired JWT token
- **404 Not Found**: 
  - User not found by userKey extracted from JWT

### Server Error Codes (5xx)
- **500 Internal Server Error**: 
  - Database errors during save operation
  - Entity creation failures
  - Unique key generation failures
  - Unexpected system exceptions

## Security & Traceability

### Authentication
- JWT token required in Authorization header
- userKey automatically extracted from JWT (not from request body)
- Token validation handled by security framework
- Users can only create achievements for themselves

### Correlation Tracking
- Correlation ID generated for each request
- Included in all error responses for traceability
- Used for log correlation across distributed systems
- Enables end-to-end request tracking

### Audit Logging
- All operations logged with correlation ID
- Success and failure operations tracked
- Security events (authentication failures) logged
- Database operations logged for compliance

## Performance Considerations

### Database Operations
- Single transaction for atomic achievement creation
- Optimized user lookup by unique key index
- Achievement key generation with collision avoidance
- Proper indexing on entityKey for fast lookups

### Validation Performance
- JSR-303 validation performed before database access
- Custom skill validation uses efficient Set.contains()
- Early validation failure prevents unnecessary database calls
- Skill normalization (trim/lowercase) optimized for small arrays

## Sample Test Data

### Valid Request Payload
```json
{
  "title": "Mastered Spring Boot Testing",
  "description": "Successfully implemented comprehensive unit and integration tests for Spring Boot application with 95% code coverage.",
  "completedDate": "2025-01-15",
  "skills": ["int", "wis", "con"],
  "isPublic": true
}
```

### Expected Success Response
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "responseType": "CREATED"
}
```

### Invalid Skills Example
```json
{
  "title": "Invalid Skills Test",
  "description": "Testing invalid skill validation",
  "completedDate": "2025-01-15", 
  "skills": ["magic", "invalid", "str"]
}
```

### Expected Validation Error Response
```json
{
  "success": false,
  "error": "skills[0]: LOCALIZE: INVALID SKILL 'magic'. ALLOWED SKILLS ARE: str, dex, con, wis, int, cha, luc",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2025-01-15T10:30:00Z"
}
```
