# Delete Achievement - Acceptance Criteria

## Feature: Delete Achievement Endpoint

### User Story
As a player, I want to delete my achievements, so that I can permanently remove them and all associated data from the database when they are no longer needed.

### Endpoint Details
- **Method**: DELETE
- **URL**: `/api/cmd/achievement/{achievementKey}`
- **Authentication**: JWT token required (userKey extracted automatically)
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Achievement Deletion
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** an achievement exists with key "8NctRKY" owned by user "ABC1234"
**When** the DELETE request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Extract achievementKey "8NctRKY" from URL path parameter
- Validate command via CommonCommand.validateCommand()
- Look up user by key "ABC1234" via IUserRepository.findByKey()
- Retrieve achievement by key "8NctRKY" via IAchievementRepository.getAchievementByKey()
- Verify user "ABC1234" owns the achievement via achievement.isCreator()
- Call achievement.delete() to raise domain events before deletion
- Delete achievement and dependencies via IAchievementRepository.deleteWithDependencies()
- Return HTTP 200 OK
- Return response with entity key:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "httpStatus": "OK"
}
```

### AC2: Achievement Not Found
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** no achievement exists with key "MISSING"
**When** the DELETE request is made to `/api/cmd/achievement/MISSING`
**Then** the system should:
- Extract userKey from JWT token
- Look up user successfully
- Attempt achievement lookup via IAchievementRepository.getAchievementByKey("MISSING")
- Find achievement is null
- Return HandlerResponse.error("achievement.not.found", ResponseType.NOT_FOUND)
- Return HTTP 404 Not Found
- Return error response:
```json
{
  "success": false,
  "data": "achievement.not.found",
  "httpStatus": "NOT_FOUND"
}
```
- Take no action (no deletion performed)

### AC3: Authentication Failures

#### AC3.1: Missing JWT Token
**Given** no JWT token is provided in the request
**When** the DELETE request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no deletion performed)

#### AC3.2: Invalid or Expired JWT Token
**Given** an invalid or expired JWT token is provided
**When** the DELETE request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no deletion performed)

### AC4: User Not Found
**Given** a valid JWT token with userKey "XYZ9999"
**And** no user exists in the system with key "XYZ9999"
**When** the DELETE request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Extract userKey from JWT token
- Attempt user lookup via IUserRepository.findByKey("XYZ9999", UserKto.class)
- Return HandlerResponse.error("User not found", ResponseType.NOT_FOUND)
- Return HTTP 404 Not Found
- Return error response:
```json
{
  "success": false,
  "data": "User not found",
  "httpStatus": "NOT_FOUND"
}
```
- Take no action (no deletion performed)

### AC5: Unauthorized Access (Not Owner)
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** an achievement exists with key "8NctRKY" owned by user "OTHER123"
**When** the DELETE request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Extract userKey "ABC1234" from JWT token
- Look up user "ABC1234" successfully
- Retrieve achievement "8NctRKY" successfully
- Check achievement.isCreator(User.fromKto(userKto)) returns false
- Return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT", ResponseType.UNAUTHORIZED)
- Return HTTP 401 Unauthorized
- Return error response:
```json
{
  "success": false,
  "data": "LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS ACHIEVEMENT",
  "httpStatus": "UNAUTHORIZED"
}
```
- Take no action (no deletion performed)

### AC6: JSR-303 Validation Failures

#### AC6.1: Invalid Achievement Key Format
**Given** a valid JWT token with userKey "ABC1234"
**And** an achievement key "SHORT" that doesn't meet validation requirements
**When** the DELETE request is made to `/api/cmd/achievement/SHORT`
**Then** the system should:
- Validate achievementKey via @Size(min = 7, max = 7) annotation
- Return HTTP 400 Bad Request
- Return validation error: "LOCALIZE: ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS"
- Take no action (no deletion performed)

#### AC6.2: Invalid User Key Format  
**Given** a JWT token with userKey "TOOLONG123" (10 characters)
**When** the DELETE request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Validate userKey via @Size(min = 7, max = 7) annotation
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS"
- Take no action (no deletion performed)

### AC7: Database and System Errors
**Given** a valid deletion request that should succeed
**And** a database error occurs during the deletion process
**When** the DELETE request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Catch exception in AchievementHandlerExceptionPolicy.handleException()
- Return HTTP 400 Bad Request
- Return error response with friendly error message
- Roll back any partial changes to maintain data integrity
- Log error details for traceability

### AC8: Proper Deletion Order and Domain Events
**Given** a valid deletion request for achievement with associated data
**And** the achievement has comments, celebrations, and media
**When** the DELETE operation is executed successfully
**Then** the system should:
1. Call achievement.delete() to raise domain events before deletion
2. Execute IAchievementRepository.deleteWithDependencies(achievement)
3. Delete in proper dependency order:
   - Achievement comments first (referential integrity)
   - Achievement celebrations second
   - Achievement media third  
   - Achievement record last
4. Use database transactions to ensure atomicity
5. Ensure no foreign key constraint violations occur
6. Return success response with entity key

## Implementation Details

### Command Handler: DeleteAchievementCommandHandler
- **Base Class**: extends CommonCommandHandler<DeleteAchievementCommand>
- **Dependencies**: IAchievementRepository, IUserRepository, AchievementHandlerExceptionPolicy
- **Validation**: Inherits validateCommand() from CommonCommandHandler
- **Response**: HandlerResponse.success(achievement.toKto()) for success, HandlerResponse.error("achievement.not.found", ResponseType.NOT_FOUND) for not found

### Command Model: DeleteAchievementCommand
- **Base Class**: extends CommonCommand
- **JWT Extraction**: userKey automatically populated from JWT token
- **Path Parameter**: achievementKey from URL path parameter
- **Validation Annotations**: @NotBlank, @Size(min = 7, max = 7) for both keys
- **No Custom Validation**: Only JSR-303 annotations needed

### Entity Deletion Flow
- **Domain Events**: achievement.delete() raises domain events before deletion
- **Dependency Deletion**: IAchievementRepository.deleteWithDependencies(achievement)
- **Transaction Management**: Atomic operation with rollback on failure
- **Cascade Order**: Comments → Celebrations → Media → Achievement

### Repository Operations
- **User Lookup**: IUserRepository.findByKey(userKey, UserKto.class)
- **Achievement Retrieval**: IAchievementRepository.getAchievementByKey(Achievement.class, achievementKey)
- **Cascading Delete**: IAchievementRepository.deleteWithDependencies(achievement)
- **Authorization Check**: achievement.isCreator(User.fromKto(userKto))

### Error Handling: AchievementHandlerExceptionPolicy
- **Exception Mapping**: Maps domain exceptions to HTTP status codes
- **Correlation Tracking**: Includes correlation ID in all error responses
- **Structured Responses**: Consistent error format across all failures

## Data Validation Rules

### Required Fields (JSR-303)
- `userKey`: 7 characters exactly (from JWT token)
- `achievementKey`: 7 characters exactly (from URL path parameter)

### No Optional Fields
- DeleteAchievementCommand contains only identification keys

## Response Format

### Success Response (HTTP 200 OK)
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "httpStatus": "OK"
}
```

### Not Found Response (HTTP 404 Not Found)
```json
{
  "success": false,
  "data": "achievement.not.found",
  "httpStatus": "NOT_FOUND"
}
```

### Error Response Format
```json
{
  "success": false,
  "data": "Error message text",
  "httpStatus": "BAD_REQUEST"
}
```

## HTTP Status Codes

### Success Codes
- **200 OK**: Achievement deleted successfully, returns entity key

### Client Error Codes (4xx)
- **400 Bad Request**: JSR-303 validation failures (invalid key format), database errors
- **401 Unauthorized**: Missing/invalid JWT token, user not authorized to delete achievement
- **404 Not Found**: User not found, achievement not found

### Server Error Codes (5xx)
- **500 Internal Server Error**: Unexpected system exceptions

## Security & Traceability

### Authentication & Authorization
- JWT token required with automatic userKey extraction
- User must own the achievement (achievement.isCreator() check)
- No cross-user deletion allowed

### Cascade Deletion Safety
- Proper dependency order prevents constraint violations
- Database transactions ensure atomicity
- Domain events raised before deletion for cleanup
