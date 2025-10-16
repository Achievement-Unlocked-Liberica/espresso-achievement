# Acceptance Criteria: Get Latest Challenges

## Feature
Retrieve a list of the latest challenges ordered by registration date (newest first)

## User Story
As an API client, I want to retrieve the latest challenges so that I can display recent challenge activity to users.

## Acceptance Criteria

### AC1: Successfully retrieve latest challenges
**Given** there are challenges in the system
**When** I request GET /api/qry/challenge/latest with valid parameters
**Then** the system returns HTTP 200 OK
**And** the response contains a list of challenges ordered by registeredAt DESC
**And** each challenge conforms to the specified DTO size

### AC2: Limit parameter controls result count
**Given** there are more than 10 challenges in the system
**When** I request GET /api/qry/challenge/latest?limit=5
**Then** the system returns exactly 5 challenges
**When** I omit the limit parameter
**Then** the system returns a maximum of 10 challenges (default)

### AC3: Size parameter controls DTO detail level
**Given** there are challenges in the system
**When** I request with size=sm
**Then** each challenge includes only: entityKey, title, description, fulfillmentDate, skills
**When** I request with size=md
**Then** each challenge additionally includes: user (UserDtoSm), challengeVisibility, media
**When** I request with size=lg
**Then** each challenge includes full details with UserDtoLg

### AC4: FromDate filter works correctly
**Given** there are challenges from various dates
**When** I request with fromDate=2025-01-01T00:00:00Z
**Then** only challenges registered after that date are returned
**And** challenges are still ordered by registeredAt DESC

### AC5: Future fromDate validation fails
**Given** I am making a request
**When** I provide a fromDate in the future
**Then** the system returns HTTP 400 BAD_REQUEST
**And** the error message indicates "fromDate must not be in the future"

### AC6: Empty result handling
**Given** there are no challenges matching the criteria
**When** I request latest challenges
**Then** the system returns HTTP 404 NOT_FOUND
**Or** returns an empty list with HTTP 200 OK (depending on implementation)

### AC7: Authentication not required for public endpoint
**Given** I am not authenticated
**When** I request GET /api/qry/challenge/latest
**Then** the system returns challenges (if public visibility allows)
**Note**: This may change based on business rules for challenge visibility

### AC8: Only enabled challenges are returned
**Given** there are both enabled and disabled challenges
**When** I request latest challenges
**Then** only challenges where enabled=true are returned

## Technical Requirements

### API Endpoint
- **Method**: GET
- **Path**: `/api/qry/challenge/latest`
- **Authentication**: Optional (depends on visibility rules)

### Query Parameters
| Parameter | Type | Required | Default | Validation |
|-----------|------|----------|---------|------------|
| size | QuerySizeType | Yes | md | Must be: xs, sm, md, lg, or xl |
| limit | Integer | No | 10 | Must be > 0 |
| fromDate | OffsetDateTime | No | null | Must not be in future, ISO 8601 format |

### Response Format
```json
{
  "success": true,
  "message": "Challenges retrieved successfully",
  "data": [
    {
      "entityKey": "abc1234",
      "title": "Complete Java Certification",
      "description": "Obtain Oracle Java SE certification",
      "fulfillmentDate": "2025-12-31T00:00:00",
      "skills": ["JAVA", "CERT"]
    }
  ],
  "timestamp": "2025-10-12T10:30:00Z"
}
```

### Response Codes
- **200 OK**: Challenges retrieved successfully
- **400 BAD_REQUEST**: Invalid parameters (future fromDate, invalid size)
- **401 UNAUTHORIZED**: Authentication required but missing/invalid (if enforced)
- **404 NOT_FOUND**: No challenges found

### Database Query
- **Table**: Challenges
- **Filter**: `enabled = true`
- **Filter**: `registeredAt > :fromDate` (if fromDate provided)
- **Order**: `registeredAt DESC`
- **Limit**: Specified limit or 10

### Performance Considerations
- Query should use index on (enabled, registeredAt DESC)
- Pagination support via limit and fromDate
- DTO projection to minimize data transfer
- Consider caching for frequently accessed data

## Test Coverage

### Unit Tests
- [ ] Query validation (valid/invalid parameters)
- [ ] Handler logic (success/error cases)
- [ ] Repository method delegation
- [ ] DTO size mapping (xs→Sm, md→Md, lg→Lg)
- [ ] Limit normalization
- [ ] Exception handling

### Integration Tests
- [ ] End-to-end API call with various parameters
- [ ] Database query execution
- [ ] DTO projection correctness
- [ ] Empty result handling
- [ ] Date filtering accuracy

### Manual Tests
- [ ] Various DTO sizes (xs, sm, md, lg, xl)
- [ ] Different limit values (1, 5, 10, 20, 100)
- [ ] With and without fromDate
- [ ] Future fromDate (negative test)
- [ ] No challenges available
- [ ] Pagination scenarios

## Related Documentation
- GET_LATEST_CHALLENGES_IMPLEMENTATION_SUMMARY.md
- Challenge Query API Swagger Documentation
- Challenge DTO Specifications

## Dependencies
- Challenge entity must be persisted with registeredAt timestamp
- User entity for user details in MD/LG DTOs
- ChallengeMedia entity for media in MD/LG DTOs
- JWT authentication infrastructure (if required)

## Future Enhancements
- Add visibility filtering (public/private/friends)
- Add skill-based filtering
- Add full-text search capability
- Add cursor-based pagination
- Add response caching with TTL
- Add rate limiting per IP/user
