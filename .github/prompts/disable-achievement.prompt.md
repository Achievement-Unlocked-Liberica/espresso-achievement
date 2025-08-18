---
mode: agent
description: Disable Achievement Endpoint: Implements an endpoint to disable an achievement by its key via PATCH operation.
---

## User Story

As a player, I want to disable an achievement, so that I can remove it from all filters, searches, and visibility.

## Description

Create an endpoint to disable an achievement using a PATCH operation.

- This action will not remove the achievement from the database; it will only set its 'enabled' property to false.
- Media disabling is not in scope of this operation.
- The implementation should follow the same pattern as the 'update achievement'.

## Types

Define a new type: `DisableAchievementCommand`.

## Implementation Steps
### Achievement Domain


- Add a new property `enabled` to the `Achievement` domain entity.
- Add a `disable` method to the `Achievement` class:
    - Sets the `enabled` property to `false`.

### AchievementCmdApi

- Add `disableAchievement` function.
    - Receives `achievementKey` from the URL path parameter.
    - Passes `achievementKey` to `AchievementCommandHandler`.

### AchievementCommandHandler

- Add a `handle` method for disabling achievement (parameter: `achievementKey`).
    - Validate the `achievementKey` (must not be blank, must be exactly 7 characters).
    - Retrieve user by `userKey`; throw not found error if missing.
    - Retrieve achievement by `achievementKey`; throw not found error if missing.
    - If found, check if achievement is already disabled:
        - If already disabled, return HTTP 204 No Content and take no action.
        - If enabled, call `disable` on achievement and save via `AchievementCmdRepository.disable`.

### AchievementCmdRepository

- Add `disable` method.
    - Receives `achievementKey` and `userKey`.
    - Forwards to PSQL provider to set `enabled` property to `false`.

### AchievementPSQLProvider

- Add `disable` method.
    - Receives `achievementKey` and `userKey`.
    - Updates the achievement record in the database, setting `enabled` to `false` where both keys match.

## DisableAchievementCommand

- Properties:
    - `achievementKey`: string, required, comes from the URL parameter.
    - `userKey`: string, required, extracted from the JWT token.
    - Updates achievement in database matching `id`, `achievementKey`, and `userKey`.
    - Sets `enabled` property to `false`.

## Service Specs & Documentation

- Annotate service and command for Swagger UI visibility.
- Add code comments to methods describing their functionality.
- Add comments to internal logic explaining each step.

## API Testing

- Update `api-tests/achievement-restClient.http` with a new HTTP request:
    - Add PATCH operation for disabling achievements.
    - Use existing JWT token variable from the file.
    - No JSON payload required.
    - Target endpoint: `PATCH http://localhost:8080/api/cmd/achievement/{key}/disable`.
    - Include proper headers (Content-Type, Authorization, X-API-Version).

## Acceptance Criteria

- Create a new file in 'service/acceptanceCriteria' called 'disable-achievement-ac.md'
- Write the acceptance criteria for the service endpoint.
- If the achievement is already disabled, the endpoint must return HTTP 204 No Content and perform no update.

## Testing

- Do not implement unit tests as part of this prompt 
