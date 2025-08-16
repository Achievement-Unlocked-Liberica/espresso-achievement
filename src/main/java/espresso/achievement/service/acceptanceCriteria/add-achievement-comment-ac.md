# Achievement Comment Feature - Acceptance Criteria

## Feature: Add Achievement Comment
As a user, I want to add comments to existing achievements so that I can communicate with achievement owners.

### Scenario: Successfully add a comment to an existing achievement
**Given** a user is authenticated with a valid JWT token
**And** the user has a valid user key "USR123A"  
**And** there exists an achievement with key "ACH456B"
**When** the user submits a comment request with:
  - achievementKey: "ACH456B"
  - commentText: "Great achievement! Well done!"
**Then** the system should create a new achievement comment
**And** the comment should be saved with status "PENDING"
**And** the comment should have default sentiment values (neutral)
**And** the comment should have default language "en"
**And** the response should return HTTP 201 Created
**And** the response should contain the created comment data

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
