package espresso.achievement.infrastructure.repositories;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import espresso.achievement.domain.entities.AchievementCelebration;

/**
 * PostgreSQL data provider for achievement celebration operations.
 * Currently configured to not persist to database as per requirements.
 */
@Repository
public interface AchievementCelebrationPSQLProvider extends JpaRepository<AchievementCelebration, Long>{

}
