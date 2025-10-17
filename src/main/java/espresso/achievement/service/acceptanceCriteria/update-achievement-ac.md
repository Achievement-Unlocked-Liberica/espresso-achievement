# Update Achievement - Acceptance Criteria

## Feature: Update Achievement Endpoint

### User Story
As a player who has created achievements, I want to update my existing achievements, so that I can correct information, improve descriptions, update skills, or change visibility settings.

### Endpoint Details
- **Method**: PUT
- **URL**: `/api/cmd/achievement/{achievementKey}`
- **Authentication**: JWT token required (userKey extracted automatically)
- **Content-Type**: application/json
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Achievement Update
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** an achievement exists with key "8NctRKY" owned by user "ABC1234"
**And** the request payload contains valid update data:
```json
{
  "title": "Updated Multi File Upload System",
  "description": "Successfully implemented and enhanced multi file upload for achievement media with validation.",
  "skills": ["int", "wis", "con"],
  "isPublic": false
}
```
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Extract achievementKey "8NctRKY" from URL path parameter
- Validate all command fields using CommonCommand validation
- Perform custom skill validation against allowed skills list
- Look up user by key "ABC1234" via IUserRepository.findByKey()
- Retrieve achievement by key "8NctRKY" via IAchievementRepository.getAchievementByKey()
- Verify user "ABC1234" owns the achievement via achievement.isCreator()
- Convert skills array to List for achievement.update() method
- Call achievement.update() with new title, description, skillsList, and isPublic
- Save updated achievement via IAchievementRepository.update()
- Return HTTP 200 OK
- Return response with entity key only:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "httpStatus": "OK"
}
```

### AC2: Authentication Failures

#### AC2.1: Missing JWT Token
**Given** no JWT token is provided in the request
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no achievement updated)

#### AC2.2: Invalid or Expired JWT Token
**Given** an invalid or expired JWT token is provided
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no achievement updated)

### AC3: User Not Found
**Given** a valid JWT token with userKey "XYZ9999"
**And** no user exists in the system with key "XYZ9999"
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Extract userKey from JWT token
- Attempt user lookup via IUserRepository.findByKey("XYZ9999")
- Repository returns null (instead of throwing exception)
- Handler detects null user and returns HandlerResponse.error("User not found", ResponseType.NOT_FOUND)
- Return HTTP 404 Not Found
- Return error response:
```json
{
  "success": false,
  "data": "User not found",
  "httpStatus": "NOT_FOUND"
}
```
- Take no action (no achievement updated)

### AC4: Achievement Not Found
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** no achievement exists with key "INVALID"
**When** the PUT request is made to `/api/cmd/achievement/INVALID`
**Then** the system should:
- Extract userKey from JWT token
- Look up user successfully
- Attempt achievement lookup via IAchievementRepository.getAchievementByKey("INVALID")
- Find achievement is null
- Return HandlerResponse.error("LOCALIZE: ACHIEVEMENT NOT FOUND", ResponseType.NOT_FOUND)
- Return HTTP 404 Not Found
- Return error response:
```json
{
  "success": false,
  "data": "LOCALIZE: ACHIEVEMENT NOT FOUND",
  "httpStatus": "NOT_FOUND"
}
```
- Take no action (no achievement updated)

### AC5: Unauthorized Access (Not Owner)
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists with key "ABC1234"
**And** an achievement exists with key "8NctRKY" owned by user "OTHER123"
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
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
- Take no action (no achievement updated)

### AC6: JSR-303 Validation Failures

#### AC6.1: Missing Required Fields
**Given** a valid JWT token with userKey "ABC1234"
**And** an achievement "8NctRKY" owned by "ABC1234" exists
**And** the request payload is missing required fields:
```json
{
  "description": "Missing title field",
  "skills": ["int"]
}
```
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Validate request via CommonCommand.validateCommand()
- Detect missing title field via @NotBlank annotation
- Return HTTP 400 Bad Request
- Return validation error response:
```json
{
  "success": false,
  "data": "LOCALIZE: A TITLE MUST BE PROVIDED",
  "httpStatus": "BAD_REQUEST"
}
```
- Take no action (no achievement updated)

#### AC6.2: Field Length Validation
**Given** a valid JWT token and achievement ownership
**And** request payload with fields exceeding length limits:
```json
{
  "title": "This is a very long title that exceeds the maximum allowed length of 200 characters and should trigger a validation error because it contains way too many characters and goes beyond the specified limit...",
  "description": "Valid description",
  "skills": ["int"]
}
```
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Validate @Size(max = 200) annotation on title
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: TITLE MUST NOT BE GREATER THAN 200 CHARACTERS"
- Take no action (no achievement updated)

### AC7: Skills Validation Failures

#### AC7.1: Invalid Skills
**Given** valid JWT token and achievement ownership
**And** request payload with invalid skills:
```json
{
  "title": "Updated Achievement",
  "description": "Updated description",
  "skills": ["magic", "invalid", "str"]
}
```
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Execute validateCustom() method
- Check each skill against ALLOWED_SKILLS set: ["str", "dex", "con", "wis", "int", "cha", "luc"]
- Return HTTP 400 Bad Request
- Return validation error:
```json
{
  "success": false,
  "data": "skills[0]: LOCALIZE: INVALID SKILL 'magic'. ALLOWED SKILLS ARE: str, dex, con, wis, int, cha, luc",
  "httpStatus": "BAD_REQUEST"
}
```
- Take no action (no achievement updated)

#### AC7.2: Too Many/Few Skills
**Given** valid JWT token and achievement ownership
**And** request payload with invalid skill count:
```json
{
  "title": "Updated Achievement",
  "description": "Updated description",
  "skills": []
}
```
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Validate @Size(min = 1, max = 7) annotation
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: AT LEAST ONE SKILL MUST BE PROVIDED"
- Take no action (no achievement updated)

### AC8: Database and System Errors
**Given** a valid update request that should succeed
**And** a database error occurs during the update process
**When** the PUT request is made to `/api/cmd/achievement/8NctRKY`
**Then** the system should:
- Catch exception in AchievementHandlerExceptionPolicy.handleException()
- Return HTTP 400 Bad Request (for domain-level errors)
- Return error response for traceability
- Ensure data integrity is maintained (no partial updates)
- Log error details

## Implementation Details

### Command Handler: UpdateAchievementCommandHandler
- **Base Class**: extends CommonCommandHandler<UpdateAchievementCommand>
- **Dependencies**: IAchievementRepository, IUserRepository, AchievementHandlerExceptionPolicy
- **Validation**: Inherits validateCommand() from CommonCommandHandler
- **Custom Validation**: UpdateAchievementCommand.validateCustom() for skills
- **Response**: HandlerResponse.success(achievement.toKto())

### Command Model: UpdateAchievementCommand
- **Base Class**: extends CommonCommand
- **JWT Extraction**: userKey automatically populated from JWT token
- **Path Parameter**: achievementKey from URL path parameter
- **Validation Annotations**: @NotBlank, @Size for fields
- **Custom Logic**: validateCustom() method for skill validation
- **Skill Normalization**: trim() and toLowerCase() applied

### Entity Update: achievement.update()
- **Update Method**: achievement.update(title, description, skillsList, isPublic)
- **Skills Conversion**: Arrays.asList(cmd.getSkills()) to convert array to List
- **Field Updates**: Updates title, description, skills, and visibility
- **Metadata Preservation**: Maintains creation date, user reference, entityKey

### Repository Operations
- **User Lookup**: IUserRepository.findByKey(userKey, UserKto.class)
- **Achievement Retrieval**: IAchievementRepository.getAchievementByKey(Achievement.class, achievementKey)
- **Achievement Update**: IAchievementRepository.update(achievement)
- **Authorization Check**: achievement.isCreator(User.fromKto(userKto))

### Error Handling: AchievementHandlerExceptionPolicy
- **Exception Mapping**: Maps domain exceptions to HTTP status codes
- **Correlation Tracking**: Includes correlation ID in all error responses
- **Structured Responses**: Consistent error format across all failures

## Data Validation Rules

### Required Fields (JSR-303)
- `userKey`: 7 characters exactly (from JWT token)
- `achievementKey`: 7 characters exactly (from URL path parameter)
- `title`: 1-200 characters, non-blank
- `description`: 1-1000 characters, non-blank
- `skills`: 1-7 skills array, non-empty

### Optional Fields
- `isPublic`: Boolean, defaults to true

### Custom Validation (validateCustom)
- **Skills**: Must be from ALLOWED_SKILLS set: ["str", "dex", "con", "wis", "int", "cha", "luc"]
- **Normalization**: Skills trimmed and converted to lowercase before validation
- **Error Format**: "skills[index]: LOCALIZE: INVALID SKILL 'value'. ALLOWED SKILLS ARE: ..."

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

### Error Response Format
```json
{
  "success": false,
  "data": "Error message text",
  "httpStatus": "NOT_FOUND"
}
```

## HTTP Status Codes

### Success Codes
- **200 OK**: Achievement updated successfully, returns entity key

### Client Error Codes (4xx)
- **400 Bad Request**: JSR-303 validation failures, custom validation failures
- **401 Unauthorized**: Missing/invalid JWT token, user not authorized to update achievement
- **404 Not Found**: User not found, achievement not found

### Server Error Codes (5xx)
- **500 Internal Server Error**: Database errors, unexpected system exceptions

## Sample Test Data

### Valid Update Request
```json
{
  "title": "Enhanced Multi File Upload System",
  "description": "Successfully implemented and optimized multi file upload for achievement media with advanced validation and error handling.",
  "skills": ["int", "wis", "con", "dex"],
  "isPublic": false
}
```

### Expected Success Response
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "httpStatus": "OK"
}
```
