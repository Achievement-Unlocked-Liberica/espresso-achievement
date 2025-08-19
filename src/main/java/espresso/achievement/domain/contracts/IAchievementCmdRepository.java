package espresso.achievement.domain.contracts;

import espresso.achievement.domain.entities.Achievement;

public interface IAchievementCmdRepository {

    Achievement save(Achievement achievement);
    
    /**
     * Updates an existing achievement in the repository.
     * 
     * @param achievement The achievement entity to update
     * @return The updated Achievement entity
     */
    Achievement update(Achievement achievement);
    
    /**
     * Deletes an achievement and all its associated dependencies in the proper order.
     * The deletion order is: comments first, then media files, then the achievement record itself.
     * Uses database transactions to ensure atomicity of the entire deletion process.
     * 
     * @param achievement The achievement entity to delete along with its dependencies
     * @throws IllegalArgumentException if the achievement is null
     * @throws RuntimeException if there's an error during the deletion process
     */
    void deleteWithDependencies(Achievement achievement);
}
