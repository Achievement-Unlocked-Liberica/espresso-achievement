---
mode: agent
---

## Story 3: Achievement Comment Feature

### Overview
As a user, I want to add comments to existing achievements so that I can communicate with achievement owners.

### Goal
Implement a complete vertical feature that allows users to post comments on achievements with proper validation, persistence, and API support.

### Technical Requirements

#### API Endpoints
- Create a new service class `AchievementCommentsCmdApi` with a POST endpoint at `/comments`
- Endpoint should follow the same coding patterns as existing Achievement service endpoints
- The endpoint should receive and process an `AddAchievementCommentCommand` object

#### Command Structure
- Create `AddAchievementCommentCommand` with:
    - achievementKey (required, 7 alphanumeric characters)
    - userKey (required, 7 alphanumeric characters)
    - commentText (required, maximum 200 characters)
- Implement appropriate validation for all fields with a `validate()` method

#### Command Handler
- Create a command handler for processing `AddAchievementCommentCommand`
- Add the 'handle' method to a new file called `AchievementCommentCommandHandler`
- Handler responsibilities:
  - Call the command's validate method to perform validation
  - Verify dependent entities exist (achievement and user)
  - Create a new instance of the AchievementComment domain model using the domain's 'create' operation
  - Call the repository to save the new domain model instance
- Follow existing patterns for command handlers in the project

#### Domain Model
- Create `AchievementComment` entity with:
    - Required fields:
        - text (the comment content)
        - achievement (reference to parent Achievement)
        - user (reference to the commenting User)
    - Auto-generated/system fields:
        - id (auto-incrementing long, primary key)
        - createdAt (UTC timestamp of creation)
        - updatedAt (UTC timestamp of last update)
        - sentiment (composite value object with positive, neutral, negative float values)
        - language (ISO 639 language code string)
        - status (enumeration: PENDING, APPROVED, FLAGGED, DELETED)
    - Implement a 'create' operation for instantiating new comments
- Implement proper relationship mapping (one Achievement to many AchievementComments)

#### Repository Layer
- Create repository interface for AchievementComment
- Implement PostgreSQL data provider following project standards
- Configure JPA annotations to prevent eager loading of Achievement and User entities

#### Error Handling
- Implement proper validation error responses
- Handle cases where achievement or user doesn't exist
- Provide meaningful error messages

#### Documentation
- Add Javadoc comments to all methods
- Include brief inline comments explaining key functionality
- Document API endpoint for consumers

### Deliverables
- Complete implementation of the feature across all layers in the Achievement module folder
- A file named `add-achievement-comment-ac.tx` containing acceptance criteria in BDD format for all scenarios
- Create the acceptance criteria file in a folder called 'acceptanceCriteria' under the 'services' folder

### Non-Requirements
- Test implementation (will be handled in a separate request)

### Coding Standards
- Follow existing project conventions and patterns
- All methods must have Javadoc comments
- Add brief inline comments explaining complex logic
- Maintain consistent naming conventions
