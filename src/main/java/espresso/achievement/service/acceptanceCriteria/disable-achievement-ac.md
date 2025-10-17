# Disable Achievement - Acceptance Criteria

## Feature: Disable Achievement Endpoint

### User Story
As a player, I want to disable my achievements, so that I can remove them from public visibility and searches while keeping the data in the database.

### Endpoint Details
- **Method**: PATCH
- **URL**: `/api/cmd/achievement/{achievementKey}/disable`
- **Authentication**: JWT token required (userKey extracted automatically)
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Achievement Disable
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** an achievement exists with key "8NctRKY" owned by user "ABC1234"
**And** the achievement is currently enabled (enabled=true)
**When** the PATCH request is made to `/api/cmd/achievement/8NctRKY/disable`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Extract achievementKey "8NctRKY" from URL path parameter
- Validate command via CommonCommand.validateCommand()
- Look up user by key "ABC1234" via IUserRepository.findByKey()
- Retrieve achievement by key "8NctRKY" via IAchievementRepository.getAchievementByKey()
- Verify user "ABC1234" owns the achievement via achievement.isCreator()
- Check achievement is enabled via achievement.isEnabled()
- Call achievement.disable() to set enabled=false
- Save updated achievement via IAchievementRepository.update()
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

#### AC2: Achievement Key Validation
**Given** a valid authenticated user  
**When** the user sends a PATCH request with an invalid achievement key (not 7 characters)  
**Then** the response status should be 400 BAD REQUEST  
**And** the response should contain:
```json
{
  "success": false,
  "data": "INVALID ACHIEVEMENT KEY",
  "httpStatus": "BAD_REQUEST"
}
```
**And** no achievement should be modified  

#### AC3: Achievement Not Found
**Given** a valid authenticated user  
**When** the user sends a PATCH request for an achievement key that doesn't exist  
**Then** the response status should be 404 NOT FOUND  
**And** the response should contain:
```json
{
  "success": false,
  "data": "LOCALIZE: ACHIEVEMENT NOT FOUND",
  "httpStatus": "NOT_FOUND"
}
```

#### AC4: User Not Found
**Given** an authenticated user with an invalid/non-existent user key in JWT  
**When** the user sends a PATCH request to disable an achievement  
**Then** the response status should be 404 NOT FOUND  
**And** the response should contain:
```json
{
  "success": false,
  "data": "LOCALIZE: USER NOT FOUND",
  "httpStatus": "NOT_FOUND"
}
```

#### AC5: Unauthorized Access - Not Owner
**Given** a valid authenticated user "USER123"  
**And** an achievement "ACHI001" exists but is owned by a different user "USER999"  
**When** user "USER123" sends a PATCH request to disable achievement "ACHI001"  
**Then** the response status should be 401 UNAUTHORIZED  
**And** the response should contain:
```json
{
  "success": false,
  "data": "LOCALIZE: USER IS NOT AUTHORIZED TO DISABLE THIS ACHIEVEMENT",
  "httpStatus": "UNAUTHORIZED"
}
```
**And** the achievement should remain unchanged  

#### AC6: Missing Authentication
**Given** no authentication token is provided  
**When** a request is sent to disable an achievement  
**Then** the response status should be 401 UNAUTHORIZED  
**And** the response should contain:
```json
{
  "success": false,
  "data": "LOCALIZE: AUTHENTICATION REQUIRED",
  "httpStatus": "UNAUTHORIZED"
}
```

#### AC7: Already Disabled Achievement
**Given** a valid authenticated user  
**And** the user owns an achievement that is already disabled  
**When** the user sends a PATCH request to disable the achievement again  
**Then** the response status should be 200 OK  
**And** the response should contain:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "httpStatus": "OK"
}
```
**And** the achievement should remain disabled  
**And** no error should occur (idempotent operation)  

#### AC8: Internal Server Error Handling
**Given** a valid request to disable an achievement  
**When** an unexpected server error occurs during processing  
**Then** the response status should be 500 INTERNAL SERVER ERROR  
**And** the response should contain:
```json
{
  "success": false,
  "data": "LOCALIZE: INTERNAL SERVER ERROR",
  "httpStatus": "INTERNAL_SERVER_ERROR"
}
```
**And** the achievement should remain in its original state  

### Technical Requirements

1. **Request Format**:
   - No request body required
   - Achievement key provided as path parameter
   - User key extracted from JWT token

2. **Response Format**:
   ```json
   {
     "success": true,
     "data": {
       "entityKey": "ACHI001"
     },
     "httpStatus": "OK"
   }
   ```

3. **Database Operations**:
   - SET enabled = false WHERE entityKey = :achievementKey AND user.entityKey = :userKey
   - No record deletion
   - Preserve all other achievement properties

4. **Authorization**:
   - JWT token validation
   - User ownership verification
   - Only achievement owner can disable

5. **Validation**:
   - Achievement key format (7 alphanumeric characters)
   - User key format (7 alphanumeric characters)
   - Entity existence validation

### Non-Functional Requirements

1. **Performance**: Response time should be under 500ms
2. **Security**: Only authenticated and authorized users can disable achievements
3. **Idempotency**: Multiple disable requests should have the same effect
4. **Logging**: All disable operations should be logged with appropriate detail level
5. **Media Handling**: Achievement media files are not affected by disable operation

### Test Coverage

- Unit tests for command validation
- Unit tests for handler business logic
- Unit tests for repository operations
- Integration tests for complete disable flow
- API tests for all HTTP response scenarios
