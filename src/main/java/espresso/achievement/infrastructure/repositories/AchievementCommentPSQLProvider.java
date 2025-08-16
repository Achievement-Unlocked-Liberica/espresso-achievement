package espresso.achievement.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import espresso.achievement.domain.entities.AchievementComment;

/**
 * JPA repository interface for AchievementComment entity.
 * Provides standard CRUD operations through Spring Data JPA.
 */
@Repository
public interface AchievementCommentPSQLProvider extends JpaRepository<AchievementComment, Long> {
    
    // Standard JPA methods are inherited from JpaRepository
    // Additional custom query methods can be added here if needed
}
