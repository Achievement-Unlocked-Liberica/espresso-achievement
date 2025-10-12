# Get User Achievements - Acceptance Criteria

## Feature: Get User Achievements Endpoint

### User Story
As a player, I want to retrieve achievements for any specific user, so that I can view their accomplishments, compare progress, and discover interesting achievements created by other players in the community.

### Endpoint Details
- **Method**: GET
- **URL**: `/api/qry/achievement/user/{requestedUserKey}`
- **Authentication**: JWT token required
- **Path Parameters**: requestedUserKey (7-character user identifier)
- **Query Parameters**: size, limit, fromDate
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Retrieval of Another User's Public Achievements
**Given** a valid JWT token with userKey "ABC1234"
**And** another user exists with userKey "XYZ5678"
**And** the target user has 10 achievements (7 public, 3 private)
**And** path parameter requestedUserKey=XYZ5678
**And** no query parameters are provided (using defaults)
**When** the GET request is made to `/api/qry/achievement/user/XYZ5678`
**Then** the system should:
- Extract authenticated userKey "ABC1234" from JWT token
- Extract requestedUserKey "XYZ5678" from path parameter
- Validate the query using CommonQueryHandler validation
- Use default size type (MD) for DTO projection
- Use default limit (10) for maximum results
- Query IAchievementRepository.getAchievementsByUserKey() with requestedUserKey "XYZ5678"
- Filter only enabled AND public achievements (enabled=true AND isPublic=true)
- Order by registeredAt DESC (newest first)
- Return HTTP 200 OK
- Return response with 7 public achievements only:
```json
{
  "success": true,
  "data": [
    {
      "entityKey": "7XpQmNz",
      "title": "Mastered React Hooks",
      "description": "Successfully implemented custom React hooks for state management.",
      "userKey": "XYZ5678",
      "completedDate": "2025-01-20",
      "skills": ["int", "wis"],
      "isPublic": true,
      "registeredAt": "2025-01-20T14:30:00Z"
    }
  ],
  "responseType": "SUCCESS"
}
```

### AC2: Retrieve User's Own Achievements via User Endpoint
**Given** a valid JWT token with userKey "ABC1234"
**And** the user requests their own achievements
**And** path parameter requestedUserKey=ABC1234
**When** the GET request is made to `/api/qry/achievement/user/ABC1234`
**Then** the system should:
- Extract authenticated userKey "ABC1234" from JWT token
- Extract requestedUserKey "ABC1234" from path parameter
- Detect authenticated user is requesting their own achievements
- Query repository for userKey "ABC1234"
- Return ALL achievements (both public AND private) for the authenticated user
- Return HTTP 200 OK
- Return complete achievement portfolio including private achievements

### AC3: Retrieve User Achievements with Small (SM) Size Type
**Given** a valid JWT token
**And** a user exists with userKey "XYZ5678"
**And** path parameter requestedUserKey=XYZ5678
**And** query parameter size=SM
**When** the GET request is made to `/api/qry/achievement/user/XYZ5678?size=SM`
**Then** the system should:
- Map QuerySizeType.SM to AchievementDtoSm.class
- Query repository with small DTO projection
- Return HTTP 200 OK
- Return user's public achievements in SM format (abbreviated fields)

### AC4: Retrieve User Achievements with Large (LG) Size Type
**Given** a valid JWT token
**And** a user exists with userKey "XYZ5678"
**And** the user has public achievements with media, comments, and celebrations
**And** path parameter requestedUserKey=XYZ5678
**And** query parameter size=LG
**When** the GET request is made to `/api/qry/achievement/user/XYZ5678?size=LG`
**Then** the system should:
- Map QuerySizeType.LG to AchievementDtoLg.class
- Query repository with large DTO projection including counts
- Return HTTP 200 OK
- Return comprehensive user achievement data:
```json
{
  "success": true,
  "data": [
    {
      "entityKey": "7XpQmNz",
      "title": "Mastered React Hooks",
      "description": "Successfully implemented custom React hooks for state management.",
      "userKey": "XYZ5678",
      "userName": "Jane Smith",
      "completedDate": "2025-01-20",
      "skills": ["int", "wis"],
      "isPublic": true,
      "registeredAt": "2025-01-20T14:30:00Z",
      "updatedAt": "2025-01-20T14:30:00Z",
      "mediaCount": 2,
      "commentCount": 8,
      "celebrationCount": 15
    }
  ],
  "responseType": "SUCCESS"
}
```

### AC5: Retrieve User Achievements with Custom Limit
**Given** a valid JWT token
**And** a user has 50 public achievements
**And** path parameter requestedUserKey=XYZ5678
**And** query parameter limit=5
**When** the GET request is made to `/api/qry/achievement/user/XYZ5678?limit=5`
**Then** the system should:
- Apply limit of 5 to the query
- Return HTTP 200 OK
- Return exactly 5 of user's public achievements
- Achievements ordered by registeredAt DESC

### AC6: Retrieve User Achievements with Date Filter
**Given** a valid JWT token
**And** a user has public achievements with various registered dates
**And** path parameter requestedUserKey=XYZ5678
**And** query parameter fromDate=2025-01-10T00:00:00Z
**When** the GET request is made to `/api/qry/achievement/user/XYZ5678?fromDate=2025-01-10T00:00:00Z`
**Then** the system should:
- Parse fromDate parameter to OffsetDateTime
- Query repository with requestedUserKey and fromDate filter
- Filter public achievements where registeredAt > fromDate
- Return HTTP 200 OK
- Return only public achievements registered after 2025-01-10
- Achievements ordered by registeredAt DESC

### AC7: Retrieve User Achievements with Combined Parameters
**Given** a valid JWT token
**And** path parameter requestedUserKey=XYZ5678
**And** query parameters: size=LG, limit=20, fromDate=2025-01-01T00:00:00Z
**When** the GET request is made to `/api/qry/achievement/user/XYZ5678?size=LG&limit=20&fromDate=2025-01-01T00:00:00Z`
**Then** the system should:
- Apply all filters: LG size type, limit of 20, date filter
- Return HTTP 200 OK
- Return up to 20 public achievements in LG format from specified date

### AC8: User Exists But Has No Public Achievements
**Given** a valid JWT token
**And** a user exists with userKey "NEW9999"
**And** the user has 5 private achievements but 0 public achievements
**And** path parameter requestedUserKey=NEW9999
**When** the GET request is made to `/api/qry/achievement/user/NEW9999`
**Then** the system should:
- Query repository for user's public achievements
- Filter returns empty (no public achievements)
- Return HTTP 200 OK
- Return empty array:
```json
{
  "success": true,
  "data": [],
  "responseType": "SUCCESS"
}
```

### AC9: User Does Not Exist
**Given** a valid JWT token
**And** no user exists with userKey "INVALID"
**And** path parameter requestedUserKey=INVALID
**When** the GET request is made to `/api/qry/achievement/user/INVALID`
**Then** the system should:
- Query repository for userKey "INVALID"
- Repository returns empty result (no achievements for non-existent user)
- Return HTTP 200 OK
- Return empty array (not an error - user may not have created achievements yet)

### AC10: Authentication Failure - Missing JWT Token
**Given** no JWT token is provided in the request
**And** path parameter requestedUserKey=XYZ5678
**When** the GET request is made to `/api/qry/achievement/user/XYZ5678`
**Then** the system should:
- Detect missing authentication token
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no query executed)

### AC11: Authentication Failure - Invalid JWT Token
**Given** an invalid JWT token is provided
**And** path parameter requestedUserKey=XYZ5678
**When** the GET request is made to `/api/qry/achievement/user/XYZ5678?size=MD`
**Then** the system should:
- Validate JWT token and fail
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no query executed)

### AC12: Validation Failure - Invalid requestedUserKey Format (Too Short)
**Given** a valid JWT token
**And** path parameter requestedUserKey=ABC (only 3 characters)
**When** the GET request is made to `/api/qry/achievement/user/ABC`
**Then** the system should:
- JSR-303 validation detects invalid @Size constraint
- Return HTTP 400 Bad Request
- Return validation error:
```json
{
  "success": false,
  "error": ["requestedUserKey must be 7 characters"],
  "responseType": "VALIDATION_ERROR"
}
```

### AC13: Validation Failure - Invalid requestedUserKey Format (Too Long)
**Given** a valid JWT token
**And** path parameter requestedUserKey=ABCDEFGHIJ (10 characters)
**When** the GET request is made to `/api/qry/achievement/user/ABCDEFGHIJ`
**Then** the system should:
- JSR-303 validation detects invalid @Size constraint
- Return HTTP 400 Bad Request
- Return validation error indicating userKey must be exactly 7 characters

### AC14: Validation Failure - Future Date Filter
**Given** a valid JWT token
**And** path parameter requestedUserKey=XYZ5678
**And** query parameter fromDate=2026-12-31T00:00:00Z (future date)
**When** the GET request is made to `/api/qry/achievement/user/XYZ5678?fromDate=2026-12-31T00:00:00Z`
**Then** the system should:
- Execute custom validation in GetUserAchievementsQuery.validateCustom()
- Detect fromDate is in the future
- Return HTTP 400 Bad Request
- Return validation error message

## Implementation Notes

### Handler Flow
1. AchievementQryApi.getUserAchievements() receives request
2. Extracts authenticated userKey from JWT token using getAuthenticatedUserKey()
3. Extracts requestedUserKey from path parameter
4. Creates GetUserAchievementsQuery from query parameters
5. Sets both userKey (authenticated) and requestedUserKey (target) on query
6. Calls AchievementQueryHandler.handle(GetUserAchievementsQuery)
7. Handler validates query using CommonQueryHandler.validateQuery()
8. Handler checks if userKey == requestedUserKey (user requesting own achievements)
9. Handler maps size type to DTO class using getDtoSize()
10. Handler calls achievementRepository.getAchievementsByUserKey()
11. Repository applies privacy filters based on userKey comparison
12. Results projected to specified DTO type
13. Handler returns HandlerResponse with SUCCESS or error
14. API returns ResponseEntity with ServiceResponse wrapper

### Privacy and Visibility Rules
- **Requesting Another User's Achievements**: Only public achievements returned (isPublic=true)
- **Requesting Own Achievements**: All achievements returned (public + private)
- **Comparison Logic**: Compare authenticated userKey with requestedUserKey
- **Filter Application**: Repository applies `enabled=true AND (isPublic=true OR userKey=authenticatedUserKey)`

### Security Context
- Authenticated userKey automatically extracted from JWT token
- RequestedUserKey explicitly provided in path parameter
- Two-level filtering: authentication (who is asking) + authorization (what they can see)
- Prevents unauthorized access to private achievements

### DTO Size Mappings
- **XS** (Extra Small): Minimal fields - entityKey, title, userKey
- **SM** (Small): Essential fields - adds completedDate, isPublic
- **MD** (Medium): Standard fields - adds description, skills, registeredAt
- **LG** (Large): Comprehensive fields - adds userName, updatedAt, counts
- **XL** (Extra Large): Maps to LG

### Validation Rules
- **JWT Token**: Required, must be valid and not expired
- **requestedUserKey**: Must not be null, must be exactly 7 alphanumeric characters
- **size**: Must be valid QuerySizeType enum (XS, SM, MD, LG, XL), defaults to MD
- **limit**: Must be positive integer, typically 1-100, defaults to 10
- **fromDate**: Must be valid OffsetDateTime, cannot be in future

### Repository Behavior
- Queries with requestedUserKey from path parameter
- Always filters enabled=true achievements
- Applies privacy filter: returns only public achievements UNLESS authenticated user is requesting their own
- Orders by registeredAt DESC for chronological listing
- Applies Limit for pagination support
- Uses Spring Data JPA projection for efficient DTO mapping

### Comparison with Get My Achievements Endpoint
| Feature | Get My Achievements | Get User Achievements |
|---------|---------------------|----------------------|
| Path | `/api/qry/achievement/my` | `/api/qry/achievement/user/{userKey}` |
| User Identification | From JWT only | Path parameter + JWT |
| Privacy | All (public + private) | Public only (unless requesting own) |
| Use Case | Personal dashboard | Social/browsing features |

### Error Handling
- Authentication errors: HTTP 401 via Spring Security
- Validation errors: HTTP 400 via CommonQueryHandler
- Invalid userKey format: HTTP 400 via JSR-303 validation
- User not found: HTTP 200 with empty array (not an error)
- Empty results: HTTP 200 with empty array
- System errors: HTTP 500 via AchievementHandlerExceptionPolicy

### Performance Considerations
- Single database query using JPA projection
- Efficient privacy filtering in database query (not post-processing)
- Indexed queries on userKey and registeredAt fields
- No N+1 query problems with counts (aggregated in single query)
