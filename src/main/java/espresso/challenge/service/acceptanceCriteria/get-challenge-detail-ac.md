# Acceptance Criteria: Get Challenge Detail

## Feature
Retrieve detailed information about a specific challenge by its entity key

## User Story
As an API client, I want to retrieve detailed information about a specific challenge so that I can display complete challenge data to users.

## Acceptance Criteria

### AC1: Successfully retrieve challenge detail
**Given** a challenge exists with entityKey "abc1234"
**When** I request GET /api/qry/challenge/detail?entityKey=abc1234&size=lg
**Then** the system returns HTTP 200 OK
**And** the response contains the challenge with full details matching the specified DTO size

### AC2: Challenge not found
**Given** no challenge exists with entityKey "invalid"
**When** I request GET /api/qry/challenge/detail?entityKey=invalid&size=md
**Then** the system returns HTTP 404 NOT_FOUND

### AC3: Invalid entity key format
**Given** I provide an invalid entity key
**When** I request with entityKey that is not exactly 7 characters
**Then** the system returns HTTP 400 BAD_REQUEST
**And** error message indicates "KEY MUST BE 7 CHARACTERS"

### AC4: Size parameter controls detail level
**Given** a challenge exists
**When** I request with size=sm
**Then** response includes minimal fields
**When** I request with size=md
**Then** response includes user and media
**When** I request with size=lg
**Then** response includes full user details and all media

### AC5: Only enabled challenges returned
**Given** a challenge exists but is disabled
**When** I request the challenge by key
**Then** the system returns HTTP 404 NOT_FOUND

### AC6: Missing required parameters
**Given** I make a request
**When** I omit the entityKey parameter
**Then** the system returns HTTP 400 BAD_REQUEST
**When** I omit the size parameter
**Then** the system returns HTTP 400 BAD_REQUEST

## Technical Requirements

### API Endpoint
- **Method**: GET
- **Path**: `/api/qry/challenge/detail`

### Query Parameters
| Parameter | Type | Required | Validation |
|-----------|------|----------|------------|
| entityKey | String | Yes | Exactly 7 characters |
| size | QuerySizeType | Yes | xs, sm, md, lg, xl |

### Response Format
```json
{
  "success": true,
  "data": {
    "entityKey": "abc1234",
    "title": "Complete Certification",
    "description": "Detailed description...",
    "fulfillmentDate": "2025-12-31",
    "user": {
      "entityKey": "usr5678",
      "username": "johndoe"
    },
    "skills": ["JAVA", "CERT"],
    "challengeVisibility": "EVERYONE",
    "media": []
  }
}
```

### Response Codes
- **200 OK**: Challenge retrieved
- **400 BAD_REQUEST**: Invalid parameters
- **404 NOT_FOUND**: Challenge not found or disabled

## Test Coverage
- [ ] Valid key with all DTO sizes
- [ ] Invalid key formats (too short, too long, special chars)
- [ ] Non-existent key
- [ ] Disabled challenge
- [ ] Missing parameters
- [ ] Different DTO projections
