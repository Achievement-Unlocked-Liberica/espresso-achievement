# Create Achievement - Acceptance Criteria

## Feature: Create Achievement Endpoint

### User Story
As a player, I want to create a new achievement, so that I can share my accomplishments and track my skills and progress over time.

### Endpoint Details
- **Method**: POST
- **URL**: `/api/cmd/achievement`
- **Authentication**: JWT token required
- **Content-Type**: application/json
- **API Version**: X-API-Version header required

## Acceptance Criteria

### AC1: Successful Achievement Creation
**Given** a valid JWT token and complete achievement data
**And** all required fields are provided with valid values
**And** the user exists in the system
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Create a new achievement record in the database
- Generate a unique 7-character achievement key
- Set the achievement's enabled status to true by default
- Record the current timestamp as registeredAt
- Associate the achievement with the authenticated user
- Return HTTP 201 Created
- Return the complete achievement data including the generated key

### AC2: Invalid JWT Token
**Given** an invalid, expired, or missing JWT token
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 401 Unauthorized
- Return an error message about authentication failure
- Take no action (no achievement created)

### AC3: User Not Found
**Given** a valid JWT token with a userKey that doesn't exist in the system
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 404 Not Found
- Return an error message indicating user not found
- Take no action (no achievement created)

### AC4: Missing Required Fields
**Given** a valid JWT token and request payload
**And** one or more required fields are missing or empty
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 400 Bad Request
- Return validation error messages for each missing field
- Take no action (no achievement created)

#### Required Fields:
- `title`: Achievement title (1-200 characters)
- `description`: Achievement description (1-1000 characters)
- `completedDate`: Date the achievement was completed (cannot be in the future)
- `skills`: Array of skills (1-7 skills required)

### AC5: Invalid Field Formats and Lengths
**Given** a valid JWT token and request payload
**And** fields contain invalid formats or exceed length limits
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 400 Bad Request
- Return specific validation error messages for each invalid field
- Take no action (no achievement created)

#### Field Validation Rules:
- `title`: Maximum 200 characters, cannot be blank
- `description`: Maximum 1000 characters, cannot be blank
- `completedDate`: Must be in "yyyy-MM-dd" format, cannot be in the future
- `skills`: Minimum 1 skill, maximum 7 skills, each skill must be from allowed list
- `isPublic`: Optional boolean field, defaults to true

### AC6: Invalid Skills
**Given** a valid JWT token and request payload
**And** the skills array contains skills not in the allowed skills list
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 400 Bad Request
- Return validation error messages specifying which skills are invalid
- Take no action (no achievement created)

#### Allowed Skills:
Must be from the predefined list of valid skills (e.g., "str", "dex", "int", "wis", "con", "cha", "luc")

### AC7: Future Completed Date
**Given** a valid JWT token and request payload
**And** the completedDate is set to a future date
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 400 Bad Request
- Return validation error message indicating completed date cannot be in the future
- Take no action (no achievement created)

### AC8: Database Error During Creation
**Given** a valid request that should succeed
**And** a database error occurs during the creation process
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Return HTTP 500 Internal Server Error
- Return an appropriate error message
- Take no action (no achievement created)
- Ensure data integrity is maintained

### AC9: Achievement Visibility Settings
**Given** a valid JWT token and complete achievement data
**And** the `isPublic` field is explicitly set
**When** the POST request is made to `/api/cmd/achievement`
**Then** the system should:
- Set the achievement visibility based on the `isPublic` field
- Use "EVERYONE" visibility if `isPublic` is true
- Use "PRIVATE" visibility if `isPublic` is false
- Default to public visibility if `isPublic` is not specified

### AC10: Automatic Key Generation
**Given** a successful achievement creation request
**When** the achievement is created in the database
**Then** the system should:
- Generate a unique 7-character alphanumeric achievement key
- Ensure the generated key doesn't conflict with existing achievements
- Include the generated key in the response

## Data Validation Rules

### Required Fields
- `title`: Non-blank string, 1-200 characters
- `description`: Non-blank string, 1-1000 characters  
- `completedDate`: Valid date in "yyyy-MM-dd" format, must be today or in the past
- `skills`: Array with 1-7 valid skill strings

### Optional Fields
- `isPublic`: Boolean value, defaults to true if not provided

### Generated Fields
- `entityKey`: 7-character unique identifier (auto-generated)
- `registeredAt`: Current timestamp (auto-generated)
- `enabled`: Boolean, defaults to true (auto-generated)
- `active`: Boolean, defaults to true (auto-generated)

## Response Codes
- **201 Created**: Achievement created successfully
- **400 Bad Request**: Validation errors in request data
- **401 Unauthorized**: Invalid or missing authentication
- **404 Not Found**: User not found
- **500 Internal Server Error**: Server error during creation

## Security Considerations
- JWT token must be valid and not expired
- UserKey is automatically extracted from JWT token (not from request body)
- Users can only create achievements for themselves
- All creation operations must be logged for audit purposes

## Performance Considerations
- Achievement key generation should be optimized for uniqueness
- Database transactions should be used for atomic operations
- Proper indexing on achievement keys for fast lookups

## Sample Request Payload
```json
{
  "title": "Completed Multi File Upload",
  "description": "Successfully implemented multi file upload for achievement media.",
  "completedDate": "2025-07-18",
  "skills": ["int", "wis", "luc"],
  "isPublic": true
}
```

## Sample Success Response (201 Created)
```json
{
  "success": true,
  "data": {
    "id": 123,
    "entityKey": "ACHI001",
    "title": "Completed Multi File Upload",
    "description": "Successfully implemented multi file upload for achievement media.",
    "completedDate": "2025-07-18T00:00:00.000Z",
    "registeredAt": "2025-08-18T20:30:00.000Z",
    "skills": ["int", "wis", "luc"],
    "achievementVisibility": "EVERYONE",
    "enabled": true,
    "active": true,
    "user": {
      "entityKey": "USER123",
      "firstName": "John",
      "lastName": "Doe"
    }
  },
  "timestamp": "2025-08-18T20:30:00.000Z"
}
```
