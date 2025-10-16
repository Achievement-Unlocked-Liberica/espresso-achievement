# Update Challenge Acceptance Criteria

## Overview
This document defines the acceptance criteria for the Update Challenge endpoint (`PUT /api/cmd/challenge/{key}`). The endpoint enables authenticated users to update their own challenges with new title, description, skills, and visibility settings.

## API Endpoint
- **Method**: PUT
- **Path**: `/api/cmd/challenge/{key}`
- **Authentication**: Required (JWT Bearer token)
- **Authorization**: User must be the creator of the challenge
- **Request Body**: JSON payload with UpdateChallengeCommand
- **Response**: ServiceResponse with entity key or error details

## Acceptance Criteria

### AC1: Successful Challenge Update
**Given** a valid JWT token for user with key "ABCD123"
**And** user owns challenge with key "8NctRKY"
**And** valid update request payload:
```json
{
  "title": "Enhanced Multi File Upload System Challenge",
  "description": "Successfully implement and optimize multi file upload for challenge media with advanced validation and error handling.",
  "skills": ["int", "wis", "con", "dex"],
  "isPublic": false
}
```
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Extract `userKey` from JWT token and set it to command
- Extract `challengeKey` from URL path parameter and set it to command
- Validate command using CommonCommandHandler.validateCommand()
- Call UpdateChallengeCommand.validateCustom() for skills validation
- Retrieve user by userKey using IUserRepository.findByKey()
- Retrieve challenge by challengeKey using IChallengeRepository.getChallengeByKey()
- Verify user is creator via challenge.isCreator(user)
- Convert skills array to List using Arrays.asList()
- Update challenge via challenge.update(title, description, skillsList, isPublic)
- Save updated challenge via IChallengeRepository.update()
- Return HTTP 200 OK
- Return success response with entity key:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "responseType": "SUCCESS"
}
```

### AC2: Authentication Failures
**Given** requests to update a challenge
**When** authentication is missing or invalid
**Then** the system should handle different authentication scenarios

#### AC2.1: Missing JWT Token
**Given** no Authorization header provided
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Not process the command
- Block access at security filter level

#### AC2.2: Invalid JWT Token
**Given** malformed or expired JWT token
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Not process the command
- Block access at security filter level

### AC3: User Not Found
**Given** valid JWT token with userKey "INVALID"
**And** user does not exist in database
**And** valid request payload
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Extract userKey from JWT token
- Call IUserRepository.findByKey("INVALID", UserKto.class)
- Return null (user not found)
- Return HTTP 404 Not Found
- Return error response:
```json
{
  "success": false,
  "error": "User not found",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no challenge updated)

### AC4: Challenge Not Found
**Given** valid JWT token with userKey "ABCD123"
**And** challenge with key "INVALID" does not exist
**And** valid request payload
**When** the PUT request is made to `/api/cmd/challenge/INVALID`
**Then** the system should:
- Extract userKey and challengeKey from JWT and path
- Retrieve user successfully
- Call IChallengeRepository.getChallengeByKey(Challenge.class, "INVALID")
- Return null (challenge not found)
- Return HTTP 404 Not Found
- Return error response:
```json
{
  "success": false,
  "error": "LOCALIZE: CHALLENGE NOT FOUND",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no challenge updated)

### AC5: Unauthorized Access - Not Challenge Owner
**Given** valid JWT token with userKey "ABCD123"
**And** challenge with key "8NctRKY" is owned by different user "XYZU999"
**And** valid request payload
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Extract userKey and challengeKey from JWT and path
- Retrieve user successfully
- Retrieve challenge successfully
- Call challenge.isCreator(user)
- Return false (user is not the creator)
- Return HTTP 401 Unauthorized
- Return error response:
```json
{
  "success": false,
  "error": "LOCALIZE: USER IS NOT AUTHORIZED TO UPDATE THIS CHALLENGE",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no challenge updated)

### AC6: JSR-303 Validation Failures
**Given** valid JWT token and challenge ownership
**And** invalid request payload violating JSR-303 constraints
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should validate and reject the request

#### AC6.1: Missing Title
**Given** request payload without title field:
```json
{
  "description": "Updated description",
  "skills": ["str", "dex"]
}
```
**When** the PUT request is made
**Then** the system should:
- Validate @NotBlank annotation on title
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: A TITLE MUST BE PROVIDED"
- Take no action (no challenge updated)

#### AC6.2: Title Too Long
**Given** request payload with title exceeding 200 characters:
```json
{
  "title": "This is an extremely long title that exceeds the maximum allowed length of 200 characters and will be rejected by validation because it violates the @Size constraint annotation defined on the title field in UpdateChallengeCommand class which enforces maximum 200 character limit",
  "description": "Valid description",
  "skills": ["str", "dex"]
}
```
**When** the PUT request is made
**Then** the system should:
- Validate @Size(max = 200) annotation on title
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: TITLE MUST NOT BE GREATER THAN 200 CHARACTERS"
- Take no action (no challenge updated)

#### AC6.3: Missing Description
**Given** request payload without description field:
```json
{
  "title": "Updated Challenge",
  "skills": ["str", "dex"]
}
```
**When** the PUT request is made
**Then** the system should:
- Validate @NotBlank annotation on description
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: A DESCRIPTION MUST BE PROVIDED"
- Take no action (no challenge updated)

#### AC6.4: Description Too Long
**Given** request payload with description exceeding 1000 characters
**When** the PUT request is made
**Then** the system should:
- Validate @Size(max = 1000) annotation on description
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: DESCRIPTION MUST NOT BE GREATER THAN 1000 CHARACTERS"
- Take no action (no challenge updated)

#### AC6.5: Empty Skills Array
**Given** request payload with empty skills array:
```json
{
  "title": "Updated Challenge",
  "description": "Updated description",
  "skills": []
}
```
**When** the PUT request is made
**Then** the system should:
- Validate @Size(min = 1, max = 7) annotation on skills
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: AT LEAST ONE SKILL MUST BE PROVIDED"
- Take no action (no challenge updated)

#### AC6.6: Too Many Skills
**Given** request payload with more than 7 skills:
```json
{
  "title": "Updated Challenge",
  "description": "Updated description",
  "skills": ["str", "dex", "con", "wis", "int", "cha", "luc", "extra"]
}
```
**When** the PUT request is made
**Then** the system should:
- Validate @Size(min = 1, max = 7) annotation on skills
- Return HTTP 400 Bad Request
- Return error indicating skills count violation
- Take no action (no challenge updated)

### AC7: Custom Skills Validation (validateCustom)
**Given** valid JWT token and challenge ownership
**And** request payload with skills that fail custom validation
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should validate skills against allowed values

#### AC7.1: Invalid Skill Value
**Given** valid JWT token and challenge ownership
**And** request payload with invalid skill:
```json
{
  "title": "Updated Challenge",
  "description": "Updated description",
  "skills": ["magic", "str"]
}
```
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Call UpdateChallengeCommand.validateCustom()
- Check each skill against ChallengeConstants.ALLOWED_SKILLS: ["str", "dex", "con", "wis", "int", "cha", "luc"]
- Normalize skill via trim() and toLowerCase()
- Find "magic" is not in ALLOWED_SKILLS
- Return HTTP 400 Bad Request
- Return validation error:
```json
{
  "success": false,
  "error": "skills[0]: LOCALIZE: INVALID SKILL 'magic'. ALLOWED SKILLS ARE: str, dex, con, wis, int, cha, luc",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-09-26T10:30:00Z"
}
```
- Take no action (no challenge updated)

#### AC7.2: Too Many/Few Skills
**Given** valid JWT token and challenge ownership
**And** request payload with invalid skill count:
```json
{
  "title": "Updated Challenge",
  "description": "Updated description",
  "skills": []
}
```
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Validate @Size(min = 1, max = 7) annotation
- Return HTTP 400 Bad Request
- Return error: "LOCALIZE: AT LEAST ONE SKILL MUST BE PROVIDED"
- Take no action (no challenge updated)

### AC8: Database and System Errors
**Given** a valid update request that should succeed
**And** a database error occurs during the update process
**When** the PUT request is made to `/api/cmd/challenge/8NctRKY`
**Then** the system should:
- Catch exception in ChallengeHandlerExceptionPolicy.handleException()
- Return HTTP 500 Internal Server Error
- Return error response with correlation ID for traceability
- Ensure data integrity is maintained (no partial updates)
- Log error details with correlation ID

## Implementation Details

### Command Handler: UpdateChallengeCommandHandler
- **Base Class**: extends CommonCommandHandler
- **Dependencies**: IChallengeRepository, IUserRepository, ChallengeHandlerExceptionPolicy
- **Validation**: Inherits validateCommand() from CommonCommandHandler
- **Custom Validation**: UpdateChallengeCommand.validateCustom() for skills
- **Response**: HandlerResponse.success(challenge.toKto())

### Command Model: UpdateChallengeCommand
- **Base Class**: extends CommonCommand
- **JWT Extraction**: userKey automatically populated from JWT token
- **Path Parameter**: challengeKey from URL path parameter
- **Validation Annotations**: @NotBlank, @Size for fields
- **Custom Logic**: validateCustom() method for skill validation
- **Skill Normalization**: trim() and toLowerCase() applied

### Entity Update: challenge.update()
- **Update Method**: challenge.update(title, description, skillsList, isPublic)
- **Skills Conversion**: Arrays.asList(cmd.getSkills()) to convert array to List
- **Field Updates**: Updates title, description, skills, and visibility
- **Metadata Preservation**: Maintains creation date, user reference, entityKey

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
  "responseType": "SUCCESS"
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
- **200 OK**: Challenge updated successfully, returns entity key

### Client Error Codes (4xx)
- **400 Bad Request**: JSR-303 validation failures, custom validation failures
- **401 Unauthorized**: Missing/invalid JWT token, user not authorized to update challenge
- **404 Not Found**: User not found, challenge not found

### Server Error Codes (5xx)
- **500 Internal Server Error**: Database errors, unexpected system exceptions

## Sample Test Data

### Valid Update Request
```json
{
  "title": "Enhanced Multi File Upload System Challenge",
  "description": "Successfully implement and optimize multi file upload for challenge media with advanced validation and error handling.",
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
  "responseType": "SUCCESS"
}
```
