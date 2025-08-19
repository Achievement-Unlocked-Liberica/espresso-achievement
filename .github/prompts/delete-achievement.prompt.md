---
mode: agent
description: Delete Achievement Endpoint: Implements an endpoint to delete an achievement by its key via DELETE operation.
---

## User Story

As a player, I want to delete an achievement, so that I can permanently remove it and all associated data from the database.

## Description

Create an endpoint to delete an achievement using a DELETE operation.

- This action will permanently remove the achievement from the database
- All achievement media records associated to the achievement will also be deleted
- All achievement comments associated to the achievement will also be deleted
- Dependencies must be removed in the correct order: comments first, then media, then the achievement record itself
- The implementation should follow the same pattern as the 'disable achievement'
- This is a destructive operation that cannot be undone

## Types

Define a new type: `DeleteAchievementCommand`.

## Implementation Steps

### Achievement Domain

Achievement entity must provide delete validation and cascading deletion support:
- Validates that achievement can be deleted (no active dependencies outside of comments/media)
- Provides methods to identify all dependent entities that must be deleted
- Ensures proper deletion order to maintain referential integrity:
    1. Comments associated with the achievement
    2. Media files associated with the achievement
    3. Achievement record itself
- Returns confirmation of successful deletion or detailed error information

### AchievementCmdApi

### AchievementCommandHandler

- Add `deleteAchievement` function.
    - Accepts `DeleteAchievementCommand` parameter.
    - Checks if the achievement exists; if not, returns a noContent response (HTTP 204).
    - Validates user authorization to delete the achievement.
    - Orchestrates cascading deletion in proper dependency order:
        1. Delete all comments associated with the achievement
        2. Delete all media files associated with the achievement
        3. Delete the achievement record itself
    - Uses transactional operations to ensure data consistency
    - Returns confirmation of successful deletion or detailed error information
    - Handles rollback scenarios if any deletion step fails

### AchievementCmdApi

- Add `deleteAchievement` function.
    - Uses DELETE HTTP method
    - Receives `achievementKey` from the URL path parameter.
    - Passes `DeleteAchievementCommand` to `AchievementCommandHandler`.
    - Returns HTTP 200 OK on successful deletion
    - Returns appropriate error codes for failure scenarios:
        - 204 No Content if the achievement didn't exist and was not deleted
        - 404 Not Found if achievement doesn't exist
        - 403 Forbidden if user lacks authorization
        - 500 Internal Server Error for deletion failures

### AchievementCommandHandler

- Add a `handle` method for deleting achievement (parameter: `DeleteAchievementCommand`).
    - Validate the `DeleteAchievementCommand` (must not be blank, must be exactly 7 characters).
    - Retrieve user by `userKey`; throw not found error if missing.
    - Retrieve achievement by `achievementKey`; throw not found error if missing.
    - Verify user is authorized to delete this achievement (user must own the achievement).
    - If authorized, call `AchievementCmdRepository.deleteWithDependencies` to remove the achievement and all dependencies in proper order.

### AchievementCmdRepository

- Add `deleteWithDependencies` method.
    - Receives the full `Achievement` entity.
    - Forwards to PSQL provider to delete the achievement and all associated data in proper dependency order.
    - Ensures proper transaction handling for cascading deletes.
    - Returns confirmation of successful deletion.

### AchievementPSQLProvider

- Add `deleteAchievementWithDependencies` method.
    - Receives the `Achievement` entity.
    - Deletes achievement dependencies in the correct order to maintain referential integrity:
        1. Delete all comments associated with the achievement (DELETE FROM comments WHERE achievement_id = ?)
        2. Delete all media files associated with the achievement (DELETE FROM achievement_media WHERE achievement_id = ?)
        3. Delete the achievement record itself (DELETE FROM achievements WHERE id = ?)
    - Uses database transactions to ensure atomicity of the entire deletion process
    - Handles foreign key constraints properly to avoid constraint violations
    - Returns confirmation of successful deletion or throws specific exceptions for failures
        1. Delete all achievement comments associated to the achievement
        2. Delete all achievement media records associated to the achievement  
        3. Delete the achievement record itself
    - Uses proper transaction management to ensure data integrity.
    - If any step fails, roll back all changes.

## DeleteAchievementCommand

- Properties:
    - `achievementKey`: string, required, comes from the URL parameter.
    - `userKey`: string, required, extracted from the JWT token.
    - Identifies the achievement to be permanently deleted from the database.
    - Validates user authorization to delete the specified achievement.

## Service Specs & Documentation

- Annotate service and command for Swagger UI visibility.
- Add code comments to methods describing their functionality.
- Add comments to internal logic explaining each step.

## API Testing

- Update `api-tests/achievement-restClient.http` with a new HTTP request:
    - Add DELETE operation for deleting achievements.
    - Use existing JWT token variable from the file.
    - No JSON payload required.
    - Target endpoint: `DELETE http://localhost:8080/api/cmd/achievement/{key}`.
    - Include proper headers (Content-Type, Authorization, X-API-Version).

## Acceptance Criteria

- Create a new file in 'achievement/service/acceptanceCriteria' called 'delete-achievement-ac.md'
- Write the acceptance criteria for the service endpoint.
- If the achievement is already deleted, the endpoint must return HTTP 204 No Content and perform no update.

## Testing

- Do not implement unit tests as part of this prompt 
