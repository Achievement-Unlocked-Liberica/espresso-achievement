# Update Achievement Feature - Acceptance Criteria

## Feature Overview
The Update Achievement feature allows users to modify existing achievements they have created, including updating the title, description, skills, and visibility settings.

## User Stories

### Primary User Story
**As a user who has created achievements, I want to be able to update my existing achievements so that I can correct information, improve descriptions, or change visibility settings.**

## Acceptance Criteria

### AC1: Authentication and Authorization
- **Given** a user is authenticated with a valid JWT token
- **When** they attempt to update an achievement
- **Then** the system should extract the user key from the JWT token automatically
- **And** only allow the user to update achievements they own

### AC2: Achievement Identification
- **Given** a user wants to update a specific achievement
- **When** they make an update request
- **Then** the achievement must be identified by its 7-character alphanumeric key in the URL path
- **And** the URL format should be `PUT /api/cmd/achievement/{achievementKey}`

### AC3: Update Achievement Endpoint
- **Given** an authenticated user with a valid achievement key
- **When** they send a PUT request to `/api/cmd/achievement/{achievementKey}` with update data
- **Then** the system should:
  - Extract the user key from the JWT token
  - Set the achievement key from the URL path parameter
  - Validate the command data
  - Process the update request through the command handler

### AC4: Required Field Validation
- **Given** a user attempts to update an achievement
- **When** required fields are missing or invalid
- **Then** the system should return appropriate validation errors:
  - Achievement key must be exactly 7 characters
  - User key must be exactly 7 characters  
  - Title must be provided and not exceed 200 characters
  - Description must be provided and not exceed 1000 characters
  - At least one skill must be provided (maximum 7 skills)

### AC5: Skills Validation
- **Given** a user provides skills for an achievement update
- **When** the skills are validated
- **Then** the system should:
  - Accept only valid skill abbreviations from the allowed skills list
  - Normalize skill case (accept mixed case input)
  - Ignore null, empty, or whitespace-only skill entries
  - Return specific error messages for invalid skills with their array index

### AC6: Achievement Ownership Verification
- **Given** a user attempts to update an achievement
- **When** the achievement exists but is owned by a different user
- **Then** the system should return a 401 Unauthorized response
- **And** display an appropriate error message about authorization

### AC7: Non-existent Achievement Handling
- **Given** a user attempts to update an achievement
- **When** the provided achievement key does not exist
- **Then** the system should return a 404 Not Found response
- **And** display an appropriate error message

### AC8: Successful Update Response
- **Given** a valid update request for an owned achievement
- **When** the update is processed successfully
- **Then** the system should:
  - Return a 200 OK response
  - Update the achievement's title, description, skills, and visibility
  - Persist the changes to the database
  - Return the updated achievement data

### AC9: Update Field Processing
- **Given** a valid update request
- **When** the achievement is updated
- **Then** the system should:
  - Update the achievement title with the provided value
  - Update the achievement description with the provided value
  - Convert the skills array to a list for processing
  - Update the achievement visibility (isPublic) setting
  - Maintain existing achievement metadata (creation date, user reference, etc.)

### AC10: Error Handling
- **Given** any update operation
- **When** an unexpected error occurs during processing
- **Then** the system should:
  - Return a 500 Internal Server Error response
  - Log the error details for debugging
  - Return a generic error message to the user

## Technical Requirements

### Command Structure
- **UpdateAchievementCommand** must extend CommonCommand
- Must include validation for all required fields
- Must support skills validation using AchievementConstants
- Must inherit standard JSR-303 validation from parent class

### API Endpoint
- **Method**: PUT
- **Path**: `/api/cmd/achievement/{key}`
- **Parameters**: 
  - `key` (path variable): 7-character achievement key
  - Request body: UpdateAchievementCommand JSON
- **Authentication**: Required (JWT token)
- **Authorization**: User must own the achievement

### Handler Processing
- Validate command data
- Verify user existence
- Verify achievement existence and ownership
- Apply updates to achievement entity
- Persist changes via repository
- Return updated achievement

### Repository Operations
- Support achievement update operations
- Maintain data integrity
- Handle concurrent access appropriately

## Test Coverage Requirements

### Unit Tests
- UpdateAchievementCommand validation tests (all validation scenarios)
- AchievementCmdApi endpoint tests (success and error cases)
- Command handler tests (business logic validation)

### Integration Tests
- End-to-end update flow
- Authentication and authorization scenarios
- Database persistence validation

## Success Metrics
- Users can successfully update their achievements
- Validation errors are clear and actionable
- Unauthorized access attempts are properly blocked
- System maintains data integrity during updates
- Performance meets established benchmarks
