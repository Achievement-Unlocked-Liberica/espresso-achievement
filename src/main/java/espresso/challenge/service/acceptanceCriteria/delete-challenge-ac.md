# Delete Challenge - Acceptance Criteria

## Feature: Delete Challenge Endpoint

### User Story
As a player, I want to delete my challenges, so that I can permanently remove them and all associated data from the database when they are no longer needed.

### Endpoint Details
- **Method**: DELETE
- **URL**: `/api/cmd/challenge/{challengeKey}`
- **Authentication**: JWT token required (userKey extracted automatically)
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Challenge Deletion
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** a challenge exists with key "8NctRKY" owned by user "ABC1234"
**When** the DELETE request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Extract challengeKey "8NctRKY" from URL path parameter
- Validate command via CommonCommand.validateCommand()
- Look up user by key "ABC1234" via IUserRepository.findByKey()
- Retrieve challenge by key "8NctRKY" via IChallengeRepository.getChallengeByKey()
- Verify user "ABC1234" owns the challenge via challenge.isCreator()
- Call challenge.delete() to raise domain events before deletion
- Delete challenge and dependencies via IChallengeRepository.deleteWithDependencies()
- Return HTTP 200 OK
- Return response with entity key:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "responseType": "SUCCESS"
}
```

### AC2: Challenge Not Found (No Content Response)
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** no challenge exists with key "MISSING"
**When** the DELETE request is made to `/api/cmd/challenge/MISSING`
**Then** the system should:
- Extract userKey from JWT token
- Look up user successfully
- Attempt challenge lookup via IChallengeRepository.getChallengeByKey("MISSING")
- Find challenge is null
- Return HandlerResponse.noContent()
- Return HTTP 204 No Content
- Take no action (no deletion performed)

### AC3: Authentication Failures

#### AC3.1: Missing JWT Token
**Given** no JWT token is provided in the request
**When** the DELETE request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no deletion performed)

#### AC3.2: Invalid or Expired JWT Token
**Given** an invalid or expired JWT token is provided
**When** the DELETE request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no deletion performed)

### AC4: User Not Found
**Given** a valid JWT token with userKey "XYZ9999"
**And** no user exists in the system with key "XYZ9999"
**When** the DELETE request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Extract userKey from JWT token
- Attempt user lookup via IUserRepository.findByKey("XYZ9999", UserKto.class)
- Return HandlerResponse.error("User not found", ResponseType.NOT_FOUND)
- Return HTTP 404 Not Found
- Return error response with correlation ID:
```json
{
  "success": false,
  "error": "User not found",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no deletion performed)

### AC5: Unauthorized Access (Not Owner)
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** a challenge exists with key "8NctRKY" owned by user "OTHER123"
**When** the DELETE request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Extract userKey "ABC1234" from JWT token
- Look up user "ABC1234" successfully
- Retrieve challenge "8NctRKY" successfully
- Check challenge.isCreator(User.fromKto(userKto)) returns false
- Return HandlerResponse.error("LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS CHALLENGE", ResponseType.UNAUTHORIZED)
- Return HTTP 401 Unauthorized
- Return error response with correlation ID:
```json
{
  "success": false,
  "error": "LOCALIZE: USER IS NOT AUTHORIZED TO DELETE THIS CHALLENGE",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no deletion performed)

### AC6: JSR-303 Validation Failures

#### AC6.1: Invalid Challenge Key Format
**Given** a valid JWT token with userKey "ABC1234"
**And** a challenge key "SHORT" that doesn't meet validation requirements
**When** the DELETE request is made to `/api/cmd/challenge/SHORT`
**Then** the system should:
- Validate challengeKey via @Size(min = 7, max = 7) annotation
- Return HTTP 400 Bad Request
- Return validation error: "LOCALIZE: CHALLENGE KEY MUST BE EXACTLY 7 CHARACTERS"
- Take no action (no deletion performed)

#### AC6.2: Invalid User Key Format  
**Given** a JWT token with userKey "TOOLONG123" (10 characters)
**When** the DELETE request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Validate userKey via @Size(min = 7, max = 7) annotation
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS"
- Take no action (no deletion performed)

### AC7: Database and System Errors
**Given** a valid deletion request that should succeed
**And** a database error occurs during the deletion process
**When** the DELETE request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Catch exception in ChallengeHandlerExceptionPolicy.handleException()
- Return HTTP 500 Internal Server Error
- Return error response with correlation ID for traceability
- Roll back any partial changes to maintain data integrity
- Log error details with correlation ID

### AC8: Proper Deletion Order and Domain Events
**Given** a valid deletion request for challenge with associated data
**And** the challenge has comments, participations, and media
**When** the DELETE operation is executed successfully
**Then** the system should:
1. Call challenge.delete() to raise domain events before deletion
2. Execute IChallengeRepository.deleteWithDependencies(challenge)
3. Delete in proper dependency order:
   - Challenge comments first (referential integrity)
   - Challenge participations second
   - Challenge media third  
   - Challenge record last
4. Use database transactions to ensure atomicity
5. Ensure no foreign key constraint violations occur
6. Return success response with entity key

## Implementation Details

### Command Handler: DeleteChallengeCommandHandler
- **Base Class**: extends CommonCommandHandler
- **Dependencies**: IChallengeRepository, IUserRepository, ChallengeHandlerExceptionPolicy
- **Validation**: Inherits validateCommand() from CommonCommandHandler
- **Response**: HandlerResponse.success(challenge.toKto()) for success, HandlerResponse.noContent() for not found

### Command Model: DeleteChallengeCommand
- **Base Class**: extends CommonCommand
- **JWT Extraction**: userKey automatically populated from JWT token
- **Path Parameter**: challengeKey from URL path parameter
- **Validation Annotations**: @NotBlank, @Size(min = 7, max = 7) for both keys
- **No Custom Validation**: Only JSR-303 annotations needed

### Entity Deletion Flow
- **Domain Events**: challenge.delete() raises domain events before deletion
- **Dependency Deletion**: IChallengeRepository.deleteWithDependencies(challenge)
- **Transaction Management**: Atomic operation with rollback on failure
- **Cascade Order**: Comments → Participations → Media → Challenge

### Repository Operations
- **User Lookup**: IUserRepository.findByKey(userKey, UserKto.class)
- **Challenge Retrieval**: IChallengeRepository.getChallengeByKey(Challenge.class, challengeKey)
- **Cascading Delete**: IChallengeRepository.deleteWithDependencies(challenge)
- **Authorization Check**: challenge.isCreator(User.fromKto(userKto))

### Error Handling: ChallengeHandlerExceptionPolicy
- **Exception Mapping**: Maps domain exceptions to HTTP status codes
- **Correlation Tracking**: Includes correlation ID in all error responses
- **Structured Responses**: Consistent error format across all failures

## Data Validation Rules

### Required Fields (JSR-303)
- `userKey`: 7 characters exactly (from JWT token)
- `challengeKey`: 7 characters exactly (from URL path parameter)

### No Optional Fields
- DeleteChallengeCommand contains only identification keys

## Response Format

### Success Response (HTTP 200 OK)
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "responseType": "SUCCESS"
}
```

### No Content Response (HTTP 204 No Content)
```json
{
  "success": true,
  "data": null,
  "responseType": "NO_CONTENT"
}
```

### Error Response Format
```json
{
  "success": false,
  "error": "Error message text",
  "correlationId": "uuid-correlation-id",
  "timestamp": "2025-09-26T10:30:00Z"
}
```

## HTTP Status Codes

### Success Codes
- **200 OK**: Challenge deleted successfully, returns entity key
- **204 No Content**: Challenge not found, no action taken

### Client Error Codes (4xx)
- **400 Bad Request**: JSR-303 validation failures (invalid key format)
- **401 Unauthorized**: Missing/invalid JWT token, user not authorized to delete challenge
- **404 Not Found**: User not found

### Server Error Codes (5xx)
- **500 Internal Server Error**: Database errors, unexpected system exceptions

## Security & Traceability

### Authentication & Authorization
- JWT token required with automatic userKey extraction
- User must own the challenge (challenge.isCreator() check)
- No cross-user deletion allowed

### Cascade Deletion Safety
- Proper dependency order prevents constraint violations
- Database transactions ensure atomicity
- Domain events raised before deletion for cleanup

### Correlation Tracking
- Correlation ID included in all error responses
- End-to-end request tracing for debugging
- Audit logging for compliance
