# Acceptance Criteria: Get User Challenges

## Feature
Retrieve challenges for a specific user by their user key

## User Story
As an authenticated API client, I want to retrieve challenges for a specific user so that I can view another user's challenge history.

## Acceptance Criteria

### AC1: Successfully retrieve user challenges
**Given** I am authenticated with a valid JWT token
**And** another user with key "usr1234" has created challenges
**When** I request GET /api/qry/challenge/user/usr1234
**Then** the system extracts my userKey from JWT for authentication
**And** retrieves challenges for user "usr1234"
**And** returns HTTP 200 OK
**And** the response contains the requested user's challenges ordered by registeredAt DESC

### AC2: View my own challenges via user endpoint
**Given** I am authenticated as user "usr1234"
**When** I request GET /api/qry/challenge/user/usr1234
**Then** the system returns my challenges
**And** the result is equivalent to GET /api/qry/challenge/my

### AC3: Authentication required
**Given** I am not authenticated
**When** I request GET /api/qry/challenge/user/usr1234
**Then** the system returns HTTP 401 UNAUTHORIZED

### AC4: Requested user not found
**Given** I am authenticated
**When** I request challenges for non-existent user key "invalid"
**Then** the system returns HTTP 404 NOT_FOUND
**And** error message indicates "User not found"

### AC5: Requested user has no challenges
**Given** I am authenticated
**And** user "usr5678" exists but has no challenges
**When** I request GET /api/qry/challenge/user/usr5678
**Then** the system returns HTTP 404 NOT_FOUND
**Or** returns empty array with HTTP 200 OK

### AC6: Invalid user key format
**Given** I am authenticated
**When** I request with user key not exactly 7 characters
**Then** the system returns HTTP 400 BAD_REQUEST
**And** error message indicates "USER KEY MUST BE 7 CHARACTERS"

### AC7: Limit and fromDate filters work
**Given** I am authenticated
**And** requesting another user's challenges
**When** I use limit and fromDate parameters
**Then** the system applies filters correctly
**And** returns filtered results

### AC8: Size parameter controls DTO detail
**Given** I am authenticated
**When** I request with different size values (xs, sm, md, lg, xl)
**Then** each size returns appropriate detail level for the requested user

## Technical Requirements

### API Endpoint
- **Method**: GET
- **Path**: `/api/qry/challenge/user/{requestedUserKey}`
- **Authentication**: **REQUIRED** - JWT Bearer token

### Path Parameters
| Parameter | Type | Required | Validation |
|-----------|------|----------|------------|
| requestedUserKey | String | Yes | Exactly 7 characters |

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
      "title": "User Challenge",
      "description": "Challenge description",
      "fulfillmentDate": "2025-12-31T00:00:00",
      "user": {
        "entityKey": "usr1234",
        "username": "targetuser"
      },
      "skills": ["JAVA"]
    }
  ]
}
```

### Response Codes
- **200 OK**: Challenges retrieved
- **400 BAD_REQUEST**: Invalid user key format or parameters
- **401 UNAUTHORIZED**: Missing/invalid JWT
- **404 NOT_FOUND**: User or challenges not found

### Dual User Context
- **Authenticated User**: Extracted from JWT (for auth/audit)
- **Requested User**: From path variable (target of query)
- Both userKeys validated separately

### Privacy Considerations (Future)
- May need to filter by challenge visibility
- May need to check friendship/connection status
- Currently returns all enabled challenges for requested user

## Test Coverage
- [ ] View another user's challenges
- [ ] View own challenges via user endpoint
- [ ] Non-existent user
- [ ] Invalid user key format (short, long)
- [ ] Missing authentication
- [ ] Invalid JWT
- [ ] Various limit values
- [ ] With fromDate filter
- [ ] All DTO sizes
- [ ] Pagination scenarios
- [ ] User with no challenges

## Future Enhancements
- Privacy filtering based on challenge visibility
- Friendship/connection verification
- Different DTO detail based on relationship
- Block list filtering
- Rate limiting per requesting user
