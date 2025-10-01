# Add Achievement Comment - Acceptance Criteria

## Feature: Add Achievement Comment Endpoint

### User Story
As a player, I want to add comments to achievements, so that I can share feedback, congratulations, or ask questions about the achievement.

### Endpoint Details
- **Method**: POST
- **URL**: `/api/cmd/achievement/{achievementKey}/comment`
- **Authentication**: JWT token required (userKey extracted automatically)
- **Content-Type**: application/json
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Comment Addition
**Given** a valid JWT token with userKey "ABC1234"
**And** a user exists in the system with key "ABC1234"
**And** an achievement exists with key "8NctRKY"
**And** the request payload contains valid comment data:
```json
{
  "commentText": "Amazing achievement! Your dedication really shows in this accomplishment. Keep up the great work!"
}
```
**When** the POST request is made to `/api/cmd/achievement/8NctRKY/comment`
**Then** the system should:
- Extract userKey from JWT token (not from request body)
- Extract achievementKey "8NctRKY" from URL path parameter
- Validate command via CommonCommand.validateCommand()
- Look up user by key "ABC1234" via IUserRepository.findByKey()
- Retrieve achievement by key "8NctRKY" via IAchievementRepository.getAchievementByKey()
- Create new comment via AchievementComment.create(commentText, achievement, user)
- Add comment to achievement via achievement.addComment(comment) (raises domain events)
- Save comment via IAchievementCommentRepository.save(comment)
- Publish domain events via publishDomainEvents(achievement)
- Return HTTP 200 OK
- Return response with achievement entity key:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY"
  },
  "responseType": "SUCCESS"
}
```

### Scenario: Fail to add comment with invalid achievement key
**Given** a user is authenticated with a valid JWT token
**And** the user has a valid user key "USR123A"
**When** the user submits a comment request with:
  - achievementKey: "INVALID"
  - commentText: "Great achievement!"
**Then** the system should return HTTP 400 Bad Request
**And** the response should contain validation error for achievement key
**And** the error message should indicate "ACHIEVEMENT KEY MUST BE EXACTLY 7 CHARACTERS"

### Scenario: Fail to add comment to non-existent achievement
**Given** a user is authenticated with a valid JWT token
**And** the user has a valid user key "USR123A"
**And** there is no achievement with key "ACH999Z"
**When** the user submits a comment request with:
  - achievementKey: "ACH999Z"
  - commentText: "Great achievement!"
**Then** the system should return HTTP 404 Not Found
**And** the response should contain error message "Achievement not found"

### Scenario: Fail to add comment without authentication
**Given** a user is not authenticated
**When** the user submits a comment request with:
  - achievementKey: "ACH456B"
  - commentText: "Great achievement!"
**Then** the system should return HTTP 401 Unauthorized
**And** the response should indicate authentication is required

### Scenario: Fail to add comment with empty text
**Given** a user is authenticated with a valid JWT token
**And** the user has a valid user key "USR123A"
**And** there exists an achievement with key "ACH456B"
**When** the user submits a comment request with:
  - achievementKey: "ACH456B"
  - commentText: ""
**Then** the system should return HTTP 400 Bad Request
**And** the response should contain validation error for comment text
**And** the error message should indicate "COMMENT TEXT MUST BE PROVIDED"

### Scenario: Fail to add comment with text exceeding maximum length
**Given** a user is authenticated with a valid JWT token
**And** the user has a valid user key "USR123A"
**And** there exists an achievement with key "ACH456B"
**When** the user submits a comment request with:
  - achievementKey: "ACH456B"
  - commentText: "This comment text is way too long and exceeds the maximum allowed character limit of 200 characters. It should be rejected by the validation logic as it contains more than the allowed limit and will cause validation errors when processed by the system."
**Then** the system should return HTTP 400 Bad Request
**And** the response should contain validation error for comment text
**And** the error message should indicate "COMMENT TEXT MUST NOT EXCEED 200 CHARACTERS"

### Scenario: Fail to add comment for non-existent user
**Given** a user is authenticated with JWT token containing invalid user key
**And** the JWT contains user key "INVALID"
**And** there exists an achievement with key "ACH456B"
**When** the user submits a comment request with:
  - achievementKey: "ACH456B"
  - commentText: "Great achievement!"
**Then** the system should return HTTP 404 Not Found
**And** the response should contain error message "User not found"

### Scenario: Successfully add multiple comments to the same achievement
**Given** a user is authenticated with a valid JWT token
**And** the user has a valid user key "USR123A"
**And** there exists an achievement with key "ACH456B"
**When** the user submits a first comment request with:
  - achievementKey: "ACH456B"
  - commentText: "First comment"
**And** the user submits a second comment request with:
  - achievementKey: "ACH456B"
  - commentText: "Second comment"
**Then** both comments should be created successfully
**And** both responses should return HTTP 201 Created
**And** each comment should have unique timestamps
**And** each comment should maintain separate database records

### Scenario: Comment entity contains all required system fields
**Given** a user is authenticated with a valid JWT token
**And** the user has a valid user key "USR123A"
**And** there exists an achievement with key "ACH456B"
**When** the user submits a comment request with:
  - achievementKey: "ACH456B"
  - commentText: "Test comment"
**Then** the created comment should have:
  - Auto-generated ID (not null)
  - createdAt timestamp in UTC
  - updatedAt timestamp in UTC (same as createdAt initially)
  - sentiment with default neutral values (positive: 0.0, neutral: 1.0, negative: 0.0)
  - language set to "en"
  - status set to "PENDING"
  - Valid references to achievement and user entities

### Scenario: Handle database errors gracefully
**Given** a user is authenticated with a valid JWT token
**And** the user has a valid user key "USR123A"
**And** there exists an achievement with key "ACH456B"
**And** the database is experiencing connection issues
**When** the user submits a comment request with:
  - achievementKey: "ACH456B"
  - commentText: "Test comment"
**Then** the system should return HTTP 500 Internal Server Error
**And** the response should contain appropriate error message
**And** no partial data should be persisted
**And** the error should be logged for debugging purposes
