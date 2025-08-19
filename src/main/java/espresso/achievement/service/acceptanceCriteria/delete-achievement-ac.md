# Delete Achievement - Acceptance Criteria

## Feature: Delete Achievement Endpoint

### User Story
As a player, I want to delete an achievement, so that I can permanently remove it and all associated data from the database.

### Endpoint Details
- **Method**: DELETE
- **URL**: `/api/cmd/achievement/{key}`
- **Authentication**: JWT token required
- **Content-Type**: application/json
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Achievement Deletion
**Given** a valid JWT token and achievement key
**And** the user owns the achievement
**And** the achievement exists in the database
**When** the DELETE request is made to `/api/cmd/achievement/{key}`
**Then** the system should:
- Delete all comments associated with the achievement
- Delete all media files associated with the achievement  
- Delete the achievement record itself
- Return HTTP 200 OK
- Return a success response confirming deletion

### AC2: Achievement Doesn't Exist
**Given** a valid JWT token and achievement key
**And** the achievement does not exist in the database
**When** the DELETE request is made to `/api/cmd/achievement/{key}`
**Then** the system should:
- Return HTTP 204 No Content
- Take no action (no deletion performed)

### AC3: Unauthorized User Access
**Given** a valid JWT token and achievement key
**And** the user does not own the achievement
**When** the DELETE request is made to `/api/cmd/achievement/{key}`
**Then** the system should:
- Return HTTP 403 Forbidden
- Return an error message indicating lack of authorization
- Take no action (no deletion performed)

### AC4: Invalid JWT Token
**Given** an invalid or missing JWT token
**When** the DELETE request is made to `/api/cmd/achievement/{key}`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return an error message about authentication
- Take no action (no deletion performed)

### AC5: Invalid Achievement Key Format
**Given** a valid JWT token
**And** an achievement key that doesn't meet validation requirements (not exactly 7 characters)
**When** the DELETE request is made to `/api/cmd/achievement/{key}`
**Then** the system should:
- Return HTTP 400 Bad Request
- Return validation error messages
- Take no action (no deletion performed)

### AC6: User Not Found
**Given** a JWT token with a userKey that doesn't exist in the system
**When** the DELETE request is made to `/api/cmd/achievement/{key}`
**Then** the system should:
- Return HTTP 404 Not Found
- Return an error message indicating user not found
- Take no action (no deletion performed)

### AC7: Database Transaction Failure
**Given** a valid request that should succeed
**And** a database error occurs during the deletion process
**When** the DELETE request is made to `/api/cmd/achievement/{key}`
**Then** the system should:
- Return HTTP 500 Internal Server Error
- Roll back any partial changes
- Maintain data integrity
- Return an appropriate error message

### AC8: Proper Deletion Order
**Given** a valid deletion request
**And** the achievement has associated comments and media
**When** the DELETE operation is executed
**Then** the system should:
- Delete comments first (to maintain referential integrity)
- Delete media files second (to maintain referential integrity)
- Delete the achievement record last
- Use database transactions to ensure atomicity
- Ensure no foreign key constraint violations occur

## Data Validation Rules
- Achievement key must be exactly 7 alphanumeric characters
- User key must be exactly 7 alphanumeric characters
- JWT token must be valid and not expired
- User must own the achievement being deleted

## Response Codes
- **200 OK**: Achievement successfully deleted
- **204 No Content**: Achievement didn't exist, no action taken
- **400 Bad Request**: Validation errors in request
- **401 Unauthorized**: Invalid or missing authentication
- **403 Forbidden**: User not authorized to delete this achievement
- **404 Not Found**: User or achievement not found
- **500 Internal Server Error**: Server error during deletion

## Security Considerations
- Users can only delete their own achievements
- Proper authentication and authorization validation required
- All deletion operations must be logged for audit purposes
- Cascading deletion must maintain referential integrity

## Performance Considerations
- Deletion operations should be atomic (all or nothing)
- Database transactions should be used to ensure consistency
- Proper indexing on foreign key relationships for efficient cascading deletes
