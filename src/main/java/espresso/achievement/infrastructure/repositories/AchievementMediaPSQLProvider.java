package espresso.achievement.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import espresso.achievement.domain.entities.AchievementMedia;

@Repository
public interface AchievementMediaPSQLProvider extends JpaRepository<AchievementMedia, Long> {
}
