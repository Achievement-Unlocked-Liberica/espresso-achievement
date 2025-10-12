# Get My Achievements - Acceptance Criteria

## Feature: Get My Achievements Endpoint

### User Story
As a player, I want to retrieve all my own achievements, so that I can review my accomplishments, track my progress, and manage my achievement portfolio.

### Endpoint Details
- **Method**: GET
- **URL**: `/api/qry/achievement/my`
- **Authentication**: JWT token required (userKey extracted automatically)
- **Query Parameters**: size, limit, fromDate
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Retrieval of My Achievements with Default Parameters
**Given** a valid JWT token with userKey "ABC1234"
**And** the user has 15 achievements in the system
**And** no query parameters are provided (using defaults)
**When** the GET request is made to `/api/qry/achievement/my`
**Then** the system should:
- Extract userKey "ABC1234" from JWT token using getAuthenticatedUserKey()
- Validate the query using CommonQueryHandler validation
- Use default size type (MD) for DTO projection
- Use default limit (10) for maximum results
- Query IAchievementRepository.getAchievementsByUserKey() with userKey "ABC1234"
- Filter only enabled achievements (enabled=true) for this user
- Order by registeredAt DESC (newest first)
- Return HTTP 200 OK
- Return response with list of user's achievements in MD format:
```json
{
  "success": true,
  "data": [
    {
      "entityKey": "8NctRKY",
      "title": "Completed Multi File Upload",
      "description": "Successfully implemented multi file upload for achievement media.",
      "userKey": "ABC1234",
      "completedDate": "2025-01-15",
      "skills": ["int", "wis", "luc"],
      "isPublic": true,
      "registeredAt": "2025-01-15T10:30:00Z"
    }
  ],
  "responseType": "SUCCESS"
}
```

### AC2: Retrieve My Achievements with Small (SM) Size Type
**Given** a valid JWT token with userKey "ABC1234"
**And** the user has achievements in the system
**And** query parameter size=SM is provided
**When** the GET request is made to `/api/qry/achievement/my?size=SM`
**Then** the system should:
- Extract userKey from JWT token
- Map QuerySizeType.SM to AchievementDtoSm.class
- Query repository for user's achievements with small DTO projection
- Return HTTP 200 OK
- Return user's achievements in SM format (abbreviated fields)

### AC3: Retrieve My Achievements with Large (LG) Size Type
**Given** a valid JWT token with userKey "ABC1234"
**And** the user has achievements with media, comments, and celebrations
**And** query parameter size=LG is provided
**When** the GET request is made to `/api/qry/achievement/my?size=LG`
**Then** the system should:
- Extract userKey from JWT token
- Map QuerySizeType.LG to AchievementDtoLg.class
- Query repository with large DTO projection including counts
- Return HTTP 200 OK
- Return comprehensive user achievement data:
```json
{
  "success": true,
  "data": [
    {
      "entityKey": "8NctRKY",
      "title": "Completed Multi File Upload",
      "description": "Successfully implemented multi file upload for achievement media.",
      "userKey": "ABC1234",
      "userName": "John Doe",
      "completedDate": "2025-01-15",
      "skills": ["int", "wis", "luc"],
      "isPublic": true,
      "registeredAt": "2025-01-15T10:30:00Z",
      "updatedAt": "2025-01-15T10:30:00Z",
      "mediaCount": 3,
      "commentCount": 5,
      "celebrationCount": 12
    }
  ],
  "responseType": "SUCCESS"
}
```

### AC4: Retrieve My Achievements with Custom Limit
**Given** a valid JWT token with userKey "ABC1234"
**And** the user has 50 achievements in the system
**And** query parameter limit=5 is provided
**When** the GET request is made to `/api/qry/achievement/my?limit=5`
**Then** the system should:
- Extract userKey from JWT token
- Apply limit of 5 to the query
- Return HTTP 200 OK
- Return exactly 5 of user's achievements (or fewer if user has less than 5)
- Achievements ordered by registeredAt DESC

### AC5: Retrieve My Achievements with Date Filter
**Given** a valid JWT token with userKey "ABC1234"
**And** the user has achievements with various registered dates
**And** query parameter fromDate=2025-01-10T00:00:00Z is provided
**When** the GET request is made to `/api/qry/achievement/my?fromDate=2025-01-10T00:00:00Z`
**Then** the system should:
- Extract userKey from JWT token
- Parse fromDate parameter to OffsetDateTime
- Query repository with userKey and fromDate filter
- Filter achievements where registeredAt > fromDate
- Return HTTP 200 OK
- Return only user's achievements registered after 2025-01-10
- Achievements ordered by registeredAt DESC

### AC6: Retrieve My Achievements with Combined Parameters
**Given** a valid JWT token with userKey "ABC1234"
**And** the user has achievements in the system
**And** query parameters: size=LG, limit=20, fromDate=2025-01-01T00:00:00Z
**When** the GET request is made to `/api/qry/achievement/my?size=LG&limit=20&fromDate=2025-01-01T00:00:00Z`
**Then** the system should:
- Extract userKey from JWT token
- Apply all filters: LG size type, limit of 20, date filter
- Return HTTP 200 OK
- Return up to 20 user's achievements in LG format from specified date

### AC7: User Has No Achievements (Empty Result)
**Given** a valid JWT token with userKey "NEW9999"
**And** the user exists but has no achievements yet
**When** the GET request is made to `/api/qry/achievement/my`
**Then** the system should:
- Extract userKey from JWT token
- Query repository successfully
- Return HTTP 200 OK
- Return empty array:
```json
{
  "success": true,
  "data": [],
  "responseType": "SUCCESS"
}
```

### AC8: Include Both Public and Private Achievements
**Given** a valid JWT token with userKey "ABC1234"
**And** the user has 5 public achievements (isPublic=true)
**And** the user has 3 private achievements (isPublic=false)
**When** the GET request is made to `/api/qry/achievement/my`
**Then** the system should:
- Extract userKey from JWT token
- Query all achievements for this user regardless of isPublic status
- Return HTTP 200 OK
- Return all 8 achievements (both public and private)
- Private achievements visible because user is requesting their own data

### AC9: Authentication Failure - Missing JWT Token
**Given** no JWT token is provided in the request
**When** the GET request is made to `/api/qry/achievement/my`
**Then** the system should:
- Detect missing authentication token
- Cannot extract userKey
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no query executed)

### AC10: Authentication Failure - Invalid JWT Token
**Given** an invalid JWT token is provided
**When** the GET request is made to `/api/qry/achievement/my`
**Then** the system should:
- Validate JWT token and fail
- Cannot extract userKey
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no query executed)

### AC11: Authentication Failure - Expired JWT Token
**Given** an expired JWT token is provided
**When** the GET request is made to `/api/qry/achievement/my`
**Then** the system should:
- Validate JWT token expiration and fail
- Return HTTP 401 Unauthorized
- Return authentication error message indicating token expired
- Take no action (no query executed)

### AC12: Validation Failure - Future Date Filter
**Given** a valid JWT token with userKey "ABC1234"
**And** query parameter fromDate=2026-12-31T00:00:00Z (future date)
**When** the GET request is made to `/api/qry/achievement/my?fromDate=2026-12-31T00:00:00Z`
**Then** the system should:
- Execute custom validation in GetMyAchievementsQuery.validateCustom()
- Detect fromDate is in the future
- Return HTTP 400 Bad Request
- Return validation error:
```json
{
  "success": false,
  "error": ["fromDate must not be in the future"],
  "responseType": "VALIDATION_ERROR"
}
```

## Implementation Notes

### Handler Flow
1. AchievementQryApi.getMyAchievements() receives request
2. Extracts userKey from JWT token using getAuthenticatedUserKey()
3. Creates GetMyAchievementsQuery from query parameters
4. Sets userKey on query object
5. Calls AchievementQueryHandler.handle(GetMyAchievementsQuery)
6. Handler validates query using CommonQueryHandler.validateQuery()
7. Handler checks for authenticated user (userKey not null)
8. Handler maps size type to DTO class using getDtoSize()
9. Handler calls achievementRepository.getAchievementsByUserKey()
10. Repository validates parameters and queries database
11. Results projected to specified DTO type
12. Handler returns HandlerResponse with SUCCESS or error
13. API returns ResponseEntity with ServiceResponse wrapper

### Security Context
- UserKey automatically extracted from JWT token via SecurityContextHolder
- Uses JWTAuthenticationToken to get authenticated user's key
- No need for userKey in request body/parameters (security best practice)
- Ensures users can only see their own achievements via this endpoint

### DTO Size Mappings
- **XS** (Extra Small): Minimal fields - entityKey, title, userKey
- **SM** (Small): Essential fields - adds completedDate, isPublic
- **MD** (Medium): Standard fields - adds description, skills, registeredAt
- **LG** (Large): Comprehensive fields - adds userName, updatedAt, counts
- **XL** (Extra Large): Maps to LG

### Validation Rules
- **JWT Token**: Required, must be valid and not expired
- **size**: Must be valid QuerySizeType enum (XS, SM, MD, LG, XL), defaults to MD
- **limit**: Must be positive integer, typically 1-100, defaults to 10
- **fromDate**: Must be valid OffsetDateTime, cannot be in future

### Repository Behavior
- Filters by userKey extracted from JWT token
- Always filters enabled=true achievements
- Returns ALL achievements for the user (both public and private)
- Orders by registeredAt DESC for chronological listing
- Applies Limit for pagination support
- Uses Spring Data JPA projection for efficient DTO mapping

### Privacy Rules
- User requesting their own achievements sees ALL achievements (public + private)
- Different from public API which only shows isPublic=true
- Enables users to manage their complete achievement portfolio

### Error Handling
- Authentication errors: HTTP 401 via Spring Security
- Authorization errors: HTTP 401 if userKey cannot be extracted
- Validation errors: HTTP 400 via CommonQueryHandler
- Empty results: HTTP 200 with empty array (not an error)
- System errors: HTTP 500 via AchievementHandlerExceptionPolicy
