# Get Achievement Detail - Acceptance Criteria

## Feature: Get Achievement Detail Endpoint

### User Story
As a player, I want to retrieve detailed information about a specific achievement, so that I can view all the information, media, comments, and celebrations associated with that achievement.

### Endpoint Details
- **Method**: GET
- **URL**: `/api/qry/achievement/detail`
- **Authentication**: JWT token required
- **Query Parameters**: entityKey, size
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Achievement Detail Retrieval with Medium (MD) Size
**Given** a valid JWT token
**And** an achievement exists with entityKey "8NctRKY"
**And** the achievement is enabled (enabled=true)
**And** query parameters: entityKey=8NctRKY, size=MD
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=8NctRKY&size=MD`
**Then** the system should:
- Validate the query using CommonQueryHandler validation
- Validate entityKey format (7 alphanumeric characters)
- Map QuerySizeType.MD to AchievementDtoMd.class
- Query IAchievementRepository.getAchievementByKey()
- Return HTTP 200 OK
- Return achievement details in MD format:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY",
    "title": "Completed Multi File Upload",
    "description": "Successfully implemented multi file upload for achievement media.",
    "userKey": "ABC1234",
    "completedDate": "2025-01-15",
    "skills": ["int", "wis", "luc"],
    "isPublic": true,
    "enabled": true,
    "registeredAt": "2025-01-15T10:30:00Z"
  },
  "responseType": "SUCCESS"
}
```

### AC2: Retrieve Achievement Detail with Small (SM) Size
**Given** a valid JWT token
**And** an achievement exists with entityKey "8NctRKY"
**And** query parameters: entityKey=8NctRKY, size=SM
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=8NctRKY&size=SM`
**Then** the system should:
- Map QuerySizeType.SM to AchievementDtoSm.class
- Query repository with small DTO projection
- Return HTTP 200 OK
- Return abbreviated achievement details:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY",
    "title": "Completed Multi File Upload",
    "userKey": "ABC1234",
    "completedDate": "2025-01-15",
    "isPublic": true
  },
  "responseType": "SUCCESS"
}
```

### AC3: Retrieve Achievement Detail with Large (LG) Size
**Given** a valid JWT token
**And** an achievement exists with entityKey "8NctRKY"
**And** the achievement has 3 media files, 5 comments, 12 celebrations
**And** query parameters: entityKey=8NctRKY, size=LG
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=8NctRKY&size=LG`
**Then** the system should:
- Map QuerySizeType.LG to AchievementDtoLg.class
- Query repository with large DTO projection including related entity counts
- Return HTTP 200 OK
- Return comprehensive achievement details:
```json
{
  "success": true,
  "data": {
    "entityKey": "8NctRKY",
    "title": "Completed Multi File Upload",
    "description": "Successfully implemented multi file upload for achievement media.",
    "userKey": "ABC1234",
    "userName": "John Doe",
    "userRace": "Human",
    "completedDate": "2025-01-15",
    "skills": ["int", "wis", "luc"],
    "isPublic": true,
    "enabled": true,
    "registeredAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-15T10:30:00Z",
    "mediaCount": 3,
    "commentCount": 5,
    "celebrationCount": 12
  },
  "responseType": "SUCCESS"
}
```

### AC4: Retrieve Achievement Detail with Extra Large (XL) Size
**Given** a valid JWT token
**And** an achievement exists with entityKey "8NctRKY"
**And** query parameters: entityKey=8NctRKY, size=XL
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=8NctRKY&size=XL`
**Then** the system should:
- Map QuerySizeType.XL to AchievementDtoLg.class (XL maps to LG)
- Query repository with large DTO projection
- Return HTTP 200 OK
- Return comprehensive achievement details (same as LG)

### AC5: Achievement Not Found
**Given** a valid JWT token
**And** no achievement exists with entityKey "INVALID"
**And** query parameters: entityKey=INVALID, size=MD
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=INVALID&size=MD`
**Then** the system should:
- Query IAchievementRepository.getAchievementByKey()
- Repository returns null (no match found)
- AchievementValidator.validateQueryResult() detects null result
- Throw AchievementException.notFound("INVALID")
- Return HTTP 404 Not Found
- Return error response with correlation ID:
```json
{
  "success": false,
  "error": "Achievement not found with key: INVALID",
  "correlationId": "correlation-uuid-123",
  "timestamp": "2025-01-15T10:30:00Z",
  "responseType": "NOT_FOUND"
}
```

### AC6: Achievement Exists But Disabled
**Given** a valid JWT token
**And** an achievement exists with entityKey "8NctRKY"
**And** the achievement is disabled (enabled=false)
**And** query parameters: entityKey=8NctRKY, size=MD
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=8NctRKY&size=MD`
**Then** the system should:
- Query repository with filter enabled=true
- Repository returns null (disabled achievements filtered out)
- Return HTTP 404 Not Found
- Return error response indicating achievement not found

### AC7: Authentication Failure - Missing JWT Token
**Given** no JWT token is provided in the request
**And** query parameters: entityKey=8NctRKY, size=MD
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=8NctRKY&size=MD`
**Then** the system should:
- Spring Security detects missing authentication
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no query executed)

### AC8: Authentication Failure - Invalid JWT Token
**Given** an invalid JWT token is provided
**And** query parameters: entityKey=8NctRKY, size=MD
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=8NctRKY&size=MD`
**Then** the system should:
- Spring Security validates JWT token and fails
- Return HTTP 401 Unauthorized
- Return authentication error message
- Take no action (no query executed)

### AC9: Validation Failure - Missing Required entityKey Parameter
**Given** a valid JWT token
**And** no entityKey parameter is provided
**When** the GET request is made to `/api/qry/achievement/detail?size=MD`
**Then** the system should:
- JSR-303 validation detects missing @NotNull entityKey
- Return HTTP 400 Bad Request
- Return validation error:
```json
{
  "success": false,
  "error": ["entityKey must not be null"],
  "responseType": "VALIDATION_ERROR"
}
```

### AC10: Validation Failure - Invalid entityKey Format
**Given** a valid JWT token
**And** query parameters: entityKey=ABC (too short), size=MD
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=ABC&size=MD`
**Then** the system should:
- AchievementValidator.validateEntityKey() detects invalid format
- Return HTTP 400 Bad Request
- Return validation error indicating entityKey must be 7 characters

### AC11: Default Size Type When Not Specified
**Given** a valid JWT token
**And** an achievement exists with entityKey "8NctRKY"
**And** query parameters: entityKey=8NctRKY (no size specified)
**When** the GET request is made to `/api/qry/achievement/detail?entityKey=8NctRKY`
**Then** the system should:
- Use default size type MD
- Map to AchievementDtoMd.class
- Return HTTP 200 OK
- Return achievement details in MD format

## Implementation Notes

### Handler Flow
1. AchievementQryApi.getAchievementDetail() receives request
2. Extracts GetAchievementDetailQuery from query parameters
3. Calls AchievementQueryHandler.handle(GetAchievementDetailQuery)
4. Handler validates query using CommonQueryHandler.validateQuery()
5. Handler validates entityKey format using AchievementValidator
6. Handler maps size type to DTO class using getDtoSize()
7. Handler calls achievementRepository.getAchievementByKey()
8. Repository validates parameters and queries database
9. Repository validates query result is not null
10. Result projected to specified DTO type
11. Handler returns HandlerResponse with SUCCESS or error
12. API returns ResponseEntity with ServiceResponse wrapper

### DTO Size Mappings
- **XS** (Extra Small): Minimal fields - entityKey, title, userKey
- **SM** (Small): Essential fields - adds completedDate, isPublic
- **MD** (Medium): Standard fields - adds description, skills, enabled, registeredAt
- **LG** (Large): Comprehensive fields - adds userName, userRace, updatedAt, counts
- **XL** (Extra Large): Maps to LG (same comprehensive fields)

### Validation Rules
- **entityKey**: Must not be null, must be exactly 7 alphanumeric characters
- **size**: Must be valid QuerySizeType enum (XS, SM, MD, LG, XL), defaults to MD

### Repository Behavior
- Queries with filter enabled=true to exclude disabled achievements
- Uses Spring Data JPA projection for efficient DTO mapping
- Returns null if achievement not found or disabled
- Validates result and throws AchievementException.notFound() if null

### Error Handling
- Authentication errors: HTTP 401 via Spring Security
- Validation errors: HTTP 400 via CommonQueryHandler and AchievementValidator
- Not found: HTTP 404 via AchievementException.notFound()
- System errors: HTTP 500 via AchievementHandlerExceptionPolicy
- All errors include correlation ID for tracking

### Query Optimization
- Single database query using JPA projection
- No N+1 query problems with counts (aggregated in single query)
- Efficient column selection based on DTO size type
