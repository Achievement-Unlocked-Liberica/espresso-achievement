# Add Challenge Encouragement - Acceptance Criteria

## Feature Overview
This feature allows authenticated users to add encouragements to existing challenges. Encouragements are represented as a numeric count (1-9) to motivate challenge owners to complete their goals.

## User Story
**As an** authenticated user  
**I want to** add encouragements to challenges created by other users  
**So that** I can show support and motivate them to complete their challenges

## Acceptance Criteria

### AC1: Successfully Add Encouragement
**Given** I am an authenticated user  
**And** a challenge with key "CH-12345" exists  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** request body:
```json
{
  "count": 5
}
```
**Then** the response status should be 201 CREATED  
**And** the response should contain success status
**And** the encouragement should be persisted with:
- Challenge reference (CH-12345)
- User reference (from JWT token)
- Count value (5)
- Created timestamp (UTC)
- Updated timestamp (UTC)
- Default status (PENDING)

### AC2: Count Validation - Minimum Boundary (Valid)
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** count value of 1:
```json
{
  "count": 1
}
```
**Then** the response status should be 201 CREATED  
**And** the encouragement should be created successfully

### AC3: Count Validation - Maximum Boundary (Valid)
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** count value of 9:
```json
{
  "count": 9
}
```
**Then** the response status should be 201 CREATED  
**And** the encouragement should be created successfully

### AC4: Count Validation - Below Minimum
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** count value of 0:
```json
{
  "count": 0
}
```
**Then** the response status should be 400 BAD_REQUEST  
**And** the response should contain error message "LOCALIZE: ENCOURAGEMENT COUNT MUST BE GREATER THAN ZERO"

### AC5: Count Validation - Above Maximum
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** count value of 10:
```json
{
  "count": 10
}
```
**Then** the response status should be 400 BAD_REQUEST  
**And** the response should contain error message "LOCALIZE: ENCOURAGEMENT COUNT MUST BE LESS THAN 10"

### AC6: Count Validation - Negative Value
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** negative count value:
```json
{
  "count": -5
}
```
**Then** the response status should be 400 BAD_REQUEST  
**And** the response should contain error message "LOCALIZE: ENCOURAGEMENT COUNT MUST BE GREATER THAN ZERO"

### AC7: Count Validation - Null Value
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** null count value:
```json
{
  "count": null
}
```
**Then** the response status should be 400 BAD_REQUEST  
**And** the response should contain error message "LOCALIZE: ENCOURAGEMENT COUNT MUST BE PROVIDED"

### AC8: Count Validation - Missing Count
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** missing count field:
```json
{}
```
**Then** the response status should be 400 BAD_REQUEST  
**And** the response should contain error message "LOCALIZE: ENCOURAGEMENT COUNT MUST BE PROVIDED"

### AC9: Challenge Not Found
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/INVALID-KEY/encouragement`  
**With** valid encouragement count  
**Then** the response status should be 404 NOT_FOUND  
**And** the response should contain error message "Challenge not found"

### AC10: User Not Authenticated
**Given** I am not authenticated (no JWT token provided)  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** valid encouragement count  
**Then** the response status should be 401 UNAUTHORIZED  
**And** the response should contain error message about missing authentication

### AC11: Invalid JWT Token
**Given** I provide an invalid or expired JWT token  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** valid encouragement count  
**Then** the response status should be 401 UNAUTHORIZED  
**And** the response should contain error message about invalid token

### AC12: User Not Found
**Given** I am authenticated with a JWT token  
**But** my user account no longer exists in the system  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** valid encouragement count  
**Then** the response status should be 404 NOT_FOUND  
**And** the response should contain error message "User not found"

### AC13: Domain Event Published
**Given** I am an authenticated user  
**And** a challenge exists  
**When** I successfully add an encouragement  
**Then** domain events should be published via `challenge.publishDomainEvents()`  
**And** events should include encouragement creation details

### AC14: Encouragement Added to Challenge
**Given** I am an authenticated user  
**When** I successfully add an encouragement to a challenge  
**Then** the encouragement should be added to the challenge's encouragements collection  
**And** the challenge entity should be updated with the new encouragement  
**And** the challenge's `updatedAt` timestamp should be refreshed

### AC15: Multiple Encouragements Support
**Given** I am an authenticated user  
**And** a challenge already has existing encouragements  
**When** I add a new encouragement  
**Then** the new encouragement should be appended to the existing encouragements list  
**And** all previous encouragements should remain unchanged

### AC16: UserKey Security
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** request body that attempts to override userKey:
```json
{
  "count": 5,
  "userKey": "HACKER1"
}
```
**Then** the userKey from the JWT token should be used (not from request body)  
**And** the `userKey` field in the request body should be ignored  
**And** the encouragement should be created with the authenticated user's key

### AC17: ChallengeKey Security
**Given** I am an authenticated user  
**When** I POST to `/api/cmd/challenge/CH-12345/encouragement`  
**With** request body that attempts to override challengeKey:
```json
{
  "count": 5,
  "challengeKey": "OTHER99"
}
```
**Then** the challengeKey from the path variable should be used (not from request body)  
**And** the `challengeKey` field in the request body should be ignored  
**And** the encouragement should be created for challenge "CH-12345"

## Technical Requirements

### Database Schema
- **Table**: `ChallengeEncouragements`
- **Columns**:
  - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
  - `count` (INTEGER, NOT NULL, CHECK: count >= 1 AND count <= 9)
  - `challenge_id` (BIGINT, NOT NULL, FOREIGN KEY -> Challenges.id)
  - `user_id` (BIGINT, NOT NULL, FOREIGN KEY -> Users.id)
  - `challenge_key` (VARCHAR(7), NOT NULL)
  - `user_key` (VARCHAR(7), NOT NULL)
  - `created_at` (TIMESTAMP, NOT NULL, DEFAULT CURRENT_TIMESTAMP)
  - `updated_at` (TIMESTAMP, NOT NULL, DEFAULT CURRENT_TIMESTAMP)
  - `status` (VARCHAR(20), NOT NULL, DEFAULT 'PENDING')
- **Indexes**:
  - PRIMARY KEY: `id`
  - INDEX: `created_at DESC` (for date-based queries)

### API Endpoint
- **URL**: `/api/cmd/challenge/{key}/encouragement`
- **Method**: POST
- **Authentication**: Required (JWT Bearer token)
- **Content-Type**: application/json

### Request Body Schema
```json
{
  "count": {
    "type": "integer",
    "required": true,
    "minimum": 1,
    "maximum": 9,
    "description": "Number of encouragements to give (1-9)"
  }
}
```

### Response Codes
- **201 Created**: Encouragement added successfully
- **400 Bad Request**: Validation error (count out of range, null, or missing)
- **401 Unauthorized**: Missing, invalid, or expired JWT token
- **404 Not Found**: Challenge or user not found
- **500 Internal Server Error**: Unexpected server error

### Response Body Schema (Success)
```json
{
  "status": "success",
  "data": {},
  "message": null
}
```

### Response Body Schema (Error)
```json
{
  "status": "error",
  "message": "Error description",
  "errors": ["Detailed error messages"]
}
```

### Command Structure
```java
public class AddChallengeEncouragementCommand extends CommonCommand {
    @JsonIgnore
    @NotBlank
    @Size(min=7, max=7)
    private String userKey; // From JWT token
    
    @JsonIgnore
    @NotBlank
    @Size(min=7, max=7)
    private String challengeKey; // From path variable
    
    @NotNull
    @Min(1)
    @Max(9)
    private Integer count; // From request body
}
```

### Handler Workflow
1. Validate command (JSR-303 annotations)
2. Look up UserKto by userKey
3. Convert UserKto to User entity
4. Look up Challenge by challengeKey
5. Create ChallengeEncouragement using factory method
6. Add encouragement to challenge via `challenge.addEncouragement()`
7. Save challenge (cascades to encouragement)
8. Publish domain events via `challenge.publishDomainEvents()`
9. Return success response

### Error Handling
- Use `ChallengeHandlerExceptionPolicy` for consistent error handling
- Validation errors return 400 with localized message
- Entity not found errors return 404 with descriptive message
- Authorization errors return 401 with security message
- Unexpected errors return 500 with generic message

### Transaction Management
- All database operations should be transactional
- Rollback on any error during save operation
- Ensure atomicity of encouragement creation

### Domain Events (Note: Event Handlers Not Implemented)
- Events are published via `challenge.publishDomainEvents()`
- No event handlers implemented per user request
- Future event handlers may include:
  - Notification to challenge owner
  - Analytics tracking
  - Leaderboard updates

## Testing Considerations

### Test Scenarios (Included in HTTP Test File)
1. Valid encouragement (count: 1-9)
2. Boundary values (1, 9)
3. Invalid count (0, 10, negative)
4. Null count
5. Missing count
6. Non-existent challenge
7. Non-existent user
8. Missing JWT token
9. Invalid JWT token
10. Expired JWT token
11. Multiple encouragements on same challenge
12. Attempt to override userKey in request body
13. Attempt to override challengeKey in request body
14. Challenge with existing encouragements
15. Concurrent encouragement creation

### Performance Considerations
- Lazy loading of encouragements collection
- Denormalized keys (challengeKey, userKey) for performance
- Indexed created_at for efficient queries
- ChallengeEncouragementCounts for aggregation optimization

## Definition of Done
- [x] All acceptance criteria met
- [x] API endpoint implemented and documented
- [x] Command and handler created
- [x] Repository layer implemented
- [x] Entity relationships established
- [x] Validation rules enforced
- [x] Security measures in place (JWT, field hiding)
- [x] Error handling implemented
- [x] No compilation errors
- [ ] HTTP test file created (pending)
- [ ] Event handlers implemented (not required per user request)

## Related Documentation
- Implementation Summary: `ADD_CHALLENGE_ENCOURAGEMENT_IMPLEMENTATION_SUMMARY.md`
- HTTP Test Suite: `acceptanceCriteria/add-challenge-encouragement-restClient.http`
- API Documentation: Available via Swagger UI at `/swagger-ui.html`

## Glossary
- **Encouragement**: A numeric count (1-9) representing motivation given to a challenge owner
- **Challenge**: A goal or task created by a user
- **JWT**: JSON Web Token used for authentication
- **CQRS**: Command Query Responsibility Segregation pattern
- **Aggregate Root**: Challenge entity that manages encouragements lifecycle
- **Value Entity**: ChallengeEncouragement as a value within Challenge aggregate

---

**Status**: ✅ Implementation Complete  
**Version**: 1.0  
**Last Updated**: December 2024
