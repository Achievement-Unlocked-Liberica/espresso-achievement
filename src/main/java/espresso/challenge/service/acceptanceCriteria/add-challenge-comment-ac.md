# Add Challenge Comment - Acceptance Criteria

## Feature Overview
This feature allows authenticated users to add comments to existing challenges. Comments support sentiment analysis, language detection, and moderation workflows.

## User Story
**As an** authenticated user  
**I want to** add comments to challenges  
**So that** I can provide feedback, ask questions, or share thoughts about challenges

## Acceptance Criteria

### AC1: Successfully Add Comment
**Given** I am an authenticated user  
**And** a challenge with key "CH-12345" exists  
**When** I POST to `/api/cmd/challenge/CH-12345/comments`  
**With** request body:
```json
{
  "commentText": "This is a great challenge! Really helped me improve my skills."
}
```
**Then** the response status should be 201 CREATED  
**And** the response should contain the created comment with:
- Comment ID
- Comment text
- Challenge reference
- User reference  
- Created timestamp (UTC)
- Updated timestamp (UTC)
- Default sentiment (neutral: positive=0.0, neutral=1.0, negative=0.0)
- Default language ("en")
- Default status (PENDING)

### AC2: Comment Text Validation - Empty Text
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/comments`  
**With** empty comment text:
```json
{
  "commentText": ""
}
```
**Then** the response status should be 400 BAD_REQUEST  
**And** the response should contain error message "Comment text is required"

### AC3: Comment Text Validation - Text Too Long
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/comments`  
**With** comment text exceeding 200 characters  
**Then** the response status should be 400 BAD_REQUEST  
**And** the response should contain error message "Comment text cannot exceed 200 characters"

### AC4: Challenge Not Found
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/INVALID-KEY/comments`  
**With** valid comment text  
**Then** the response status should be 404 NOT_FOUND  
**And** the response should contain error message "Challenge not found"

### AC5: User Not Authenticated
**Given** I am not authenticated (no JWT token provided)  
**When** I POST to `/api/cmd/challenge/CH-12345/comments`  
**With** valid comment text  
**Then** the response status should be 401 UNAUTHORIZED  
**And** the response should contain error message about missing authentication

### AC6: Domain Event Published
**Given** I am an authenticated user  
**And** a challenge exists  
**When** I successfully add a comment  
**Then** a `ChallengeCommentEvent` should be published with:
- Event type: "Challenge.Comment.CREATED"
- Challenge key
- User key
- Comment text
- Event ID (7-character unique key)
- Timestamp
- Source: "challenge-module"

### AC7: Comment Added to Challenge
**Given** I am an authenticated user  
**When** I successfully add a comment to a challenge  
**Then** the comment should be added to the challenge's comments collection  
**And** the challenge entity should be updated with the new comment

### AC8: Multiple Comments Support
**Given** I am an authenticated user  
**And** a challenge already has existing comments  
**When** I add a new comment  
**Then** the new comment should be appended to the existing comments list  
**And** all previous comments should remain unchanged

## Technical Requirements

### Database Schema
- **Table**: `ChallengeComments`
- **Columns**:
  - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
  - `text` (VARCHAR(200), NOT NULL)
  - `challengeId` (BIGINT, FOREIGN KEY → Challenges.id)
  - `userId` (BIGINT, FOREIGN KEY → Users.id)
  - `createdAt` (TIMESTAMP, NOT NULL)
  - `updatedAt` (TIMESTAMP, NOT NULL)
  - `sentimentPositive` (DOUBLE, NOT NULL)
  - `sentimentNeutral` (DOUBLE, NOT NULL)
  - `sentimentNegative` (DOUBLE, NOT NULL)
  - `language` (VARCHAR(5), NOT NULL)
  - `status` (VARCHAR(20), NOT NULL) -- PENDING, APPROVED, FLAGGED, DELETED

### Indexes
- `idx_challenge_comment_id_pkey` (UNIQUE on id)
- `idx_challenge_comment_created_at_desc` (DESC on createdAt)
- `idx_challenge_comment_challenge_id_idx` (on challengeId)
- `idx_challenge_comment_user_id_idx` (on userId)

### API Endpoint
- **Method**: POST
- **Path**: `/api/cmd/challenge/{key}/comments`
- **Request Body**: `AddChallengeCommentCommand`
  ```json
  {
    "commentText": "string (max 200 chars)"
  }
  ```
- **Response**: `ChallengeComment` entity
- **Status Codes**:
  - 201: Created successfully
  - 400: Validation error
  - 401: Unauthorized
  - 404: Challenge or user not found
  - 500: Internal server error

### Security
- JWT authentication required
- User key extracted from JWT token (not from request body)
- Challenge key extracted from URL path parameter
- Users can comment on any challenge (no ownership validation)

### Data Validation
- Comment text: Required, non-blank, max 200 characters
- Challenge key: Required, must exist in database
- User key: Required, must exist in database

### CQRS Pattern
- **Command**: `AddChallengeCommentCommand`
- **Handler**: `AddChallengeCommentCommandHandler`
- **Repository**: `IChallengeCommentRepository`

### Domain Events
- Event raised after comment successfully added
- Event contains minimal data (keys + text)
- Event published through domain aggregate

## Future Enhancements (Out of Scope)
- Sentiment analysis integration (AI service)
- Language detection service
- Comment moderation workflow
- Comment editing
- Comment deletion
- Comment replies/threading
- Comment reactions
- Pagination for comments
- Filter comments by status

## Related Documentation
- Challenge CRUD Operations
- Domain Events Architecture
- JWT Authentication System
- Sentiment Analysis Integration (planned)

## Test Coverage
- Unit tests for command validation
- Unit tests for handler business logic
- Integration tests for API endpoint
- Domain event verification tests
- Database persistence tests
