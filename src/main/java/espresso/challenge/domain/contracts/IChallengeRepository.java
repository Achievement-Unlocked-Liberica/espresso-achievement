package espresso.challenge.domain.contracts;

import espresso.challenge.domain.entities.Challenge;

/**
 * Repository interface for Challenge entity operations.
 * Combines both command and query operations for challenges,
 * providing CRUD operations and specialized query methods for challenge data.
 */
public interface IChallengeRepository {

    // Command operations
    /**
     * Saves a new challenge to the repository.
     * 
     * @param challenge The challenge entity to save
     * @return The saved Challenge entity with generated ID and timestamps
     */
    Challenge save(Challenge challenge);
    
    /**
     * Updates an existing challenge in the repository.
     * 
     * @param challenge The challenge entity to update
     * @return The updated Challenge entity
     */
    Challenge update(Challenge challenge);
    
    /**
     * Deletes a challenge and all its associated dependencies in the proper order.
     * Uses database transactions to ensure atomicity of the entire deletion process.
     * 
     * @param challenge The challenge entity to delete along with its dependencies
     * @throws IllegalArgumentException if the challenge is null
     * @throws RuntimeException if there's an error during the deletion process
     */
    void deleteWithDependencies(Challenge challenge);

    // Query operations
    /**
     * Retrieves a challenge by its entity key and projects it to the specified DTO type.
     * 
     * @param <T> The type of the DTO to project to
     * @param dtoType The class type to project the challenge data to
     * @param entityKey The unique 7-character key identifying the challenge
     * @return The challenge data projected to the specified type, or null if not found
     */
    <T> T getChallengeByKey(Class<T> dtoType, String entityKey);

    // Additional query operations can be added here as needed
}
