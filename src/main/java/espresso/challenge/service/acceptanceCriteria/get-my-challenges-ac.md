# Acceptance Criteria: Get My Challenges

## Feature
Retrieve challenges for the authenticated user

## User Story
As an authenticated user, I want to retrieve my challenges so that I can view my personal challenge history.

## Acceptance Criteria

### AC1: Successfully retrieve my challenges
**Given** I am authenticated with a valid JWT token
**And** I have created challenges
**When** I request GET /api/qry/challenge/my
**Then** the system extracts my userKey from the JWT token
**And** returns HTTP 200 OK
**And** the response contains only my challenges ordered by registeredAt DESC

### AC2: Authentication required
**Given** I am not authenticated
**When** I request GET /api/qry/challenge/my
**Then** the system returns HTTP 401 UNAUTHORIZED

### AC3: Invalid JWT token
**Given** I provide an invalid or expired JWT token
**When** I request GET /api/qry/challenge/my
**Then** the system returns HTTP 401 UNAUTHORIZED

### AC4: User not found
**Given** I am authenticated but my user record doesn't exist
**When** I request GET /api/qry/challenge/my
**Then** the system returns HTTP 404 NOT_FOUND
**And** error message indicates "User not found"

### AC5: No challenges for user
**Given** I am authenticated
**And** I have not created any challenges
**When** I request GET /api/qry/challenge/my
**Then** the system returns HTTP 404 NOT_FOUND
**Or** returns empty array with HTTP 200 OK

### AC6: Limit parameter works
**Given** I am authenticated with 20 challenges
**When** I request with limit=5
**Then** the system returns exactly 5 challenges
**When** I omit limit
**Then** the system returns maximum 10 challenges (default)

### AC7: FromDate filter works
**Given** I am authenticated with challenges from various dates
**When** I request with fromDate=2025-01-01T00:00:00Z
**Then** only my challenges after that date are returned

### AC8: Size parameter controls DTO detail
**Given** I am authenticated with challenges
**When** I request with different size values (xs, sm, md, lg, xl)
**Then** each size returns appropriate detail level

## Technical Requirements

### API Endpoint
- **Method**: GET
- **Path**: `/api/qry/challenge/my`
- **Authentication**: **REQUIRED** - JWT Bearer token

### Query Parameters
| Parameter | Type | Required | Default | Validation |
|-----------|------|----------|---------|------------|
| size | QuerySizeType | Yes | md | xs, sm, md, lg, xl |
| limit | Integer | No | 10 | > 0 |
| fromDate | OffsetDateTime | No | null | Not in future, ISO 8601 |

### Request Headers
```
Authorization: Bearer <JWT_TOKEN>
```

### Response Format
```json
{
  "success": true,
  "message": "Challenges retrieved successfully",
  "data": [
    {
      "entityKey": "abc1234",
      "title": "My Challenge",
      "description": "Challenge description",
      "fulfillmentDate": "2025-12-31T00:00:00",
      "skills": ["JAVA"]
    }
  ]
}
```

### Response Codes
- **200 OK**: Challenges retrieved
- **400 BAD_REQUEST**: Invalid parameters
- **401 UNAUTHORIZED**: Missing/invalid JWT
- **404 NOT_FOUND**: User or challenges not found

### JWT Integration
- UserKey extracted from JWT token via `getAuthenticatedUserKey()`
- UserKey set on query object before handler execution
- No userKey in request body or query params

## Test Coverage
- [ ] Valid JWT with challenges
- [ ] Valid JWT without challenges
- [ ] Missing JWT token
- [ ] Invalid JWT token
- [ ] Expired JWT token
- [ ] Various limit values
- [ ] With fromDate filter
- [ ] Future fromDate (negative)
- [ ] All DTO sizes
- [ ] Pagination scenarios
