# Disable Achievement - Acceptance Criteria

## Feature: Disable Achievement Endpoint

### User Story
As a player, I want to disable an achievement, so that I can remove it from all filters, searches, and visibility.

### Endpoint Details
- **Method**: PATCH
- **URL**: `/api/cmd/achievement/{key}/disable`
- **Authentication**: JWT token required
- **Content-Type**: application/json
- **API Version**: X-API-Version header required

### Acceptance Criteria

#### AC1: Successful Achievement Disable
**Given** a valid authenticated user  
**And** the user owns an achievement with key "ACHI001"  
**And** the achievement is currently enabled  
**When** the user sends a PATCH request to `/api/cmd/achievement/ACHI001/disable`  
**Then** the response status should be 200 OK  
**And** the response should contain the disabled achievement  
**And** the achievement's "enabled" property should be set to false  
**And** the achievement should remain in the database  
**And** the achievement should be removed from searches, filters, and public visibility  

#### AC2: Achievement Key Validation
**Given** a valid authenticated user  
**When** the user sends a PATCH request with an invalid achievement key (not 7 characters)  
**Then** the response status should be 400 BAD REQUEST  
**And** the response should contain validation error messages  
**And** no achievement should be modified  

#### AC3: Achievement Not Found
**Given** a valid authenticated user  
**When** the user sends a PATCH request for an achievement key that doesn't exist  
**Then** the response status should be 404 NOT FOUND  
**And** the response should contain "LOCALIZE: ACHIEVEMENT NOT FOUND" message  

#### AC4: User Not Found
**Given** an authenticated user with an invalid/non-existent user key in JWT  
**When** the user sends a PATCH request to disable an achievement  
**Then** the response status should be 404 NOT FOUND  
**And** the response should contain "LOCALIZE: USER NOT FOUND" message  

#### AC5: Unauthorized Access - Not Owner
**Given** a valid authenticated user "USER123"  
**And** an achievement "ACHI001" exists but is owned by a different user "USER999"  
**When** user "USER123" sends a PATCH request to disable achievement "ACHI001"  
**Then** the response status should be 401 UNAUTHORIZED  
**And** the response should contain "LOCALIZE: USER IS NOT AUTHORIZED TO DISABLE THIS ACHIEVEMENT" message  
**And** the achievement should remain unchanged  

#### AC6: Missing Authentication
**Given** no authentication token is provided  
**When** a request is sent to disable an achievement  
**Then** the response status should be 401 UNAUTHORIZED  
**And** appropriate authentication error message should be returned  

#### AC7: Already Disabled Achievement
**Given** a valid authenticated user  
**And** the user owns an achievement that is already disabled  
**When** the user sends a PATCH request to disable the achievement again  
**Then** the response status should be 200 OK  
**And** the achievement should remain disabled  
**And** no error should occur (idempotent operation)  

#### AC8: Internal Server Error Handling
**Given** a valid request to disable an achievement  
**When** an unexpected server error occurs during processing  
**Then** the response status should be 500 INTERNAL SERVER ERROR  
**And** the response should contain an appropriate error message  
**And** the achievement should remain in its original state  

#### AC9: No Action on Already Disabled Achievement
**Given** a valid authenticated user  
**And** the user owns an achievement that is already disabled  
**When** the user sends a PATCH request to disable the achievement  
**Then** the response status should be 204 NO CONTENT  
**And** no changes should be made to the achievement  
**And** the achievement should remain disabled  
**And** no error message should be returned  

### Technical Requirements

1. **Request Format**:
   - No request body required
   - Achievement key provided as path parameter
   - User key extracted from JWT token

2. **Response Format**:
   ```json
   {
     "status": "SUCCESS",
     "data": {
       "id": 1,
       "entityKey": "ACHI001",
       "title": "Achievement Title",
       "description": "Achievement Description",
       "enabled": false,
       "active": true,
       // ... other achievement properties
     }
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
