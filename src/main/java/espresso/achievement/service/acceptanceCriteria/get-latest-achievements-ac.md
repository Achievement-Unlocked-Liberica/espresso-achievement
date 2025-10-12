# Get Latest Achievements - Acceptance Criteria

## Feature: Get Latest Achievements Endpoint

### User Story
As a player, I want to retrieve the latest achievements from the system, so that I can see what others have accomplished recently and get inspiration for my own goals.

### Endpoint Details
- **Method**: GET
- **URL**: `/api/qry/achievement/latest`
- **Authentication**: JWT token required
- **Query Parameters**: size, limit, fromDate
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Latest Achievements Retrieval with Default Parameters
**Given** a valid JWT token
**And** achievements exist in the system
**And** no query parameters are provided (using defaults)
**When** the GET request is made to `/api/qry/achievement/latest`
**Then** the system should:
- Validate the query using CommonQueryHandler validation
- Use default size type (MD) for DTO projection
- Use default limit (10) for maximum results
- Query IAchievementRepository.getLatestAchievements() with no date filter
- Filter only enabled achievements (enabled=true)
- Order by registeredAt DESC (newest first)
- Return HTTP 200 OK
- Return response with list of achievements in MD format:
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

### AC2: Retrieve Latest Achievements with Small (SM) Size Type
**Given** a valid JWT token
**And** achievements exist in the system
**And** query parameter size=SM is provided
**When** the GET request is made to `/api/qry/achievement/latest?size=SM`
**Then** the system should:
- Map QuerySizeType.SM to AchievementDtoSm.class
- Query repository with small DTO projection
- Return HTTP 200 OK
- Return abbreviated achievement data (fewer fields than MD):
```json
{
  "success": true,
  "data": [
    {
      "entityKey": "8NctRKY",
      "title": "Completed Multi File Upload",
      "userKey": "ABC1234",
      "completedDate": "2025-01-15",
      "isPublic": true
    }
  ],
  "responseType": "SUCCESS"
}
```

### AC3: Retrieve Latest Achievements with Large (LG) Size Type
**Given** a valid JWT token
**And** achievements exist in the system
**And** query parameter size=LG is provided
**When** the GET request is made to `/api/qry/achievement/latest?size=LG`
**Then** the system should:
- Map QuerySizeType.LG to AchievementDtoLg.class
- Query repository with large DTO projection
- Return HTTP 200 OK
- Return comprehensive achievement data with additional fields:
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

### AC4: Retrieve Latest Achievements with Custom Limit
**Given** a valid JWT token
**And** 50 achievements exist in the system
**And** query parameter limit=5 is provided
**When** the GET request is made to `/api/qry/achievement/latest?limit=5`
**Then** the system should:
- Validate limit parameter is within acceptable range
- Apply limit of 5 to the query
- Return HTTP 200 OK
- Return exactly 5 achievements (or fewer if less than 5 exist)
- Achievements ordered by registeredAt DESC

### AC5: Retrieve Latest Achievements with Date Filter
**Given** a valid JWT token
**And** achievements exist with various registered dates
**And** query parameter fromDate=2025-01-10T00:00:00Z is provided
**When** the GET request is made to `/api/qry/achievement/latest?fromDate=2025-01-10T00:00:00Z`
**Then** the system should:
- Parse fromDate parameter to OffsetDateTime
- Query IAchievementRepository with fromDate filter
- Filter achievements where registeredAt > fromDate
- Return HTTP 200 OK
- Return only achievements registered after 2025-01-10
- Achievements ordered by registeredAt DESC

### AC6: Retrieve Latest Achievements with Combined Parameters
**Given** a valid JWT token
**And** achievements exist in the system
**And** query parameters: size=LG, limit=20, fromDate=2025-01-01T00:00:00Z
**When** the GET request is made to `/api/qry/achievement/latest?size=LG&limit=20&fromDate=2025-01-01T00:00:00Z`
**Then** the system should:
- Apply all filters: LG size type, limit of 20, date filter
- Return HTTP 200 OK
- Return up to 20 achievements in LG format from specified date
- Achievements ordered by registeredAt DESC

### AC7: No Achievements Found (Empty Result)
**Given** a valid JWT token
**And** no achievements exist in the system (or none match filters)
**When** the GET request is made to `/api/qry/achievement/latest`
**Then** the system should:
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

### AC8: Authentication Failure - Missing JWT Token
**Given** no JWT token is provided in the request
**When** the GET request is made to `/api/qry/achievement/latest`
**Then** the system should:
- Detect missing authentication token
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no query executed)

### AC9: Authentication Failure - Invalid JWT Token
**Given** an invalid JWT token is provided
**When** the GET request is made to `/api/qry/achievement/latest`
**Then** the system should:
- Validate JWT token and fail
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no query executed)

### AC10: Validation Failure - Future Date Filter
**Given** a valid JWT token
**And** query parameter fromDate=2026-12-31T00:00:00Z (future date)
**When** the GET request is made to `/api/qry/achievement/latest?fromDate=2026-12-31T00:00:00Z`
**Then** the system should:
- Execute custom validation in GetLatestAchievementsQuery.validateCustom()
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

### AC11: Validation Failure - Invalid Limit Values
**Given** a valid JWT token
**And** query parameter limit=-5 (negative value)
**When** the GET request is made to `/api/qry/achievement/latest?limit=-5`
**Then** the system should:
- Validate limit parameter in AchievementValidator.validateAndNormalizeLimit()
- Detect invalid limit value
- Return HTTP 400 Bad Request
- Return validation error message

## Implementation Notes

### Handler Flow
1. AchievementQryApi.getLatestAchievements() receives request
2. Extracts GetLatestAchievementsQuery from query parameters
3. Calls AchievementQueryHandler.handle(GetLatestAchievementsQuery)
4. Handler validates query using CommonQueryHandler.validateQuery()
5. Handler maps size type to DTO class using getDtoSize()
6. Handler calls achievementRepository.getLatestAchievements()
7. Repository validates parameters and queries database
8. Results projected to specified DTO type
9. Handler returns HandlerResponse with SUCCESS or error
10. API returns ResponseEntity with ServiceResponse wrapper

### DTO Size Mappings
- **SM** (Small): Essential fields only - entityKey, title, userKey, completedDate, isPublic
- **MD** (Medium): Standard fields - adds description, skills, registeredAt
- **LG** (Large): Comprehensive fields - adds userName, updatedAt, counts (media, comments, celebrations)

### Validation Rules
- **size**: Must be valid QuerySizeType enum (XS, SM, MD, LG, XL), defaults to MD
- **limit**: Must be positive integer, typically 1-100, defaults to 10
- **fromDate**: Must be valid OffsetDateTime, cannot be in future

### Repository Behavior
- Always filters enabled=true achievements
- Orders by registeredAt DESC for chronological listing
- Applies Limit for pagination support
- Uses Spring Data JPA projection for efficient DTO mapping

### Error Handling
- Authentication errors: HTTP 401 via Spring Security
- Validation errors: HTTP 400 via CommonQueryHandler
- Not found (empty results): HTTP 200 with empty array
- System errors: HTTP 500 via AchievementHandlerExceptionPolicy
