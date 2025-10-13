# Disable Challenge - Acceptance Criteria

## Feature: Disable Challenge Endpoint

### User Story
As a player, I want to disable my challenges, so that I can remove them from public visibility and searches while keeping the data in the database.

### Endpoint Details
- **Method**: PATCH
- **URL**: `/api/cmd/challenge/{challengeKey}/disable`
- **Authentication**: JWT token required (userKey extracted automatically)
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Challenge Disable
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** a challenge exists with key "8NctRKY" owned by user "ABC1234"
**And** the challenge is currently enabled (enabled=true)
**When** the PATCH request is made to `/api/cmd/challenge/8NctRKY/disable`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Extract challengeKey "8NctRKY" from URL path parameter
- Validate command via CommonCommand.validateCommand()
- Look up user by key "ABC1234" via IUserRepository.findByKey()
- Retrieve challenge by key "8NctRKY" via IChallengeRepository.getChallengeByKey()
- Verify user "ABC1234" owns the challenge via challenge.isCreator()
- Check challenge is enabled via challenge.isEnabled()
- Call challenge.disable() to set enabled=false
- Save updated challenge via IChallengeRepository.update()
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

#### AC2: Challenge Key Validation
**Given** a valid authenticated user  
**When** the user sends a PATCH request with an invalid challenge key (not 7 characters)  
**Then** the response status should be 400 BAD REQUEST  
**And** the response should contain validation error messages  
**And** no challenge should be modified  

#### AC3: Challenge Not Found
**Given** a valid authenticated user  
**When** the user sends a PATCH request for a challenge key that doesn't exist  
**Then** the response status should be 404 NOT FOUND  
**And** the response should contain "LOCALIZE: CHALLENGE NOT FOUND" message  

#### AC4: User Not Found
**Given** an authenticated user with an invalid/non-existent user key in JWT  
**When** the user sends a PATCH request to disable a challenge  
**Then** the response status should be 404 NOT FOUND  
**And** the response should contain "User not found" message  

#### AC5: Unauthorized Access - Not Owner
**Given** a valid authenticated user "USER123"  
**And** a challenge "CHAL001" exists but is owned by a different user "USER999"  
**When** user "USER123" sends a PATCH request to disable challenge "CHAL001"  
**Then** the response status should be 401 UNAUTHORIZED  
**And** the response should contain "LOCALIZE: USER IS NOT AUTHORIZED TO DISABLE THIS CHALLENGE" message  
**And** the challenge should remain unchanged  

#### AC6: Missing Authentication
**Given** no authentication token is provided  
**When** a request is sent to disable a challenge  
**Then** the response status should be 401 UNAUTHORIZED  
**And** appropriate authentication error message should be returned  

#### AC7: Already Disabled Challenge
**Given** a valid authenticated user  
**And** the user owns a challenge that is already disabled  
**When** the user sends a PATCH request to disable the challenge again  
**Then** the response status should be 204 NO CONTENT  
**And** the challenge should remain disabled  
**And** no error should occur (idempotent operation)  

#### AC8: Internal Server Error Handling
**Given** a valid request to disable a challenge  
**When** an unexpected server error occurs during processing  
**Then** the response status should be 500 INTERNAL SERVER ERROR  
**And** the response should contain an appropriate error message  
**And** the challenge should remain in its original state  

### Technical Requirements

1. **Request Format**:
   - No request body required
   - Challenge key provided as path parameter
   - User key extracted from JWT token

2. **Response Format**:
   ```json
   {
     "success": true,
     "data": {
       "entityKey": "CHAL001"
     },
     "responseType": "SUCCESS"
   }
   ```

3. **Database Operations**:
   - SET enabled = false WHERE entityKey = :challengeKey AND user.entityKey = :userKey
   - No record deletion
   - Preserve all other challenge properties

4. **Authorization**:
   - JWT token validation
   - User ownership verification
   - Only challenge owner can disable

5. **Validation**:
   - Challenge key format (7 alphanumeric characters)
   - User key format (7 alphanumeric characters)
   - Entity existence validation

### Non-Functional Requirements

1. **Performance**: Response time should be under 500ms
2. **Security**: Only authenticated and authorized users can disable challenges
3. **Idempotency**: Multiple disable requests should have the same effect
4. **Logging**: All disable operations should be logged with appropriate detail level
5. **Media Handling**: Challenge media files are not affected by disable operation

### Test Coverage

- Unit tests for command validation
- Unit tests for handler business logic
- Unit tests for repository operations
- Integration tests for complete disable flow
- API tests for all HTTP response scenarios

## Implementation Details

### Command Handler: DisableChallengeCommandHandler
- **Base Class**: extends CommonCommandHandler
- **Dependencies**: IChallengeRepository, IUserRepository, ChallengeHandlerExceptionPolicy
- **Validation**: Inherits validateCommand() from CommonCommandHandler
- **Response**: HandlerResponse.success(challenge.toKto()) for success, HandlerResponse.noContent() for already disabled

### Command Model: DisableChallengeCommand
- **Base Class**: extends CommonCommand
- **JWT Extraction**: userKey automatically populated from JWT token
- **Path Parameter**: challengeKey from URL path parameter
- **Validation Annotations**: @NotBlank, @Size(min = 7, max = 7) for both keys
- **No Custom Validation**: Only JSR-303 annotations needed

### Entity Disable: challenge.disable()
- **Disable Method**: challenge.disable() sets enabled=false
- **Domain Events**: Raises ChallengeDisabled domain event
- **State Change**: Updates enabled field only
- **Metadata Preservation**: Maintains all other challenge properties

### Repository Operations
- **User Lookup**: IUserRepository.findByKey(userKey, UserKto.class)
- **Challenge Retrieval**: IChallengeRepository.getChallengeByKey(Challenge.class, challengeKey)
- **Challenge Update**: IChallengeRepository.update(challenge)
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
- DisableChallengeCommand contains only identification keys

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
- **200 OK**: Challenge disabled successfully, returns entity key
- **204 No Content**: Challenge already disabled, no action taken

### Client Error Codes (4xx)
- **400 Bad Request**: JSR-303 validation failures (invalid key format)
- **401 Unauthorized**: Missing/invalid JWT token, user not authorized to disable challenge
- **404 Not Found**: User not found, challenge not found

### Server Error Codes (5xx)
- **500 Internal Server Error**: Database errors, unexpected system exceptions
