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
}
