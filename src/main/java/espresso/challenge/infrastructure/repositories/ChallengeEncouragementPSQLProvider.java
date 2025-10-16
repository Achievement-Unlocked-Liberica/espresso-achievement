package espresso.challenge.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import espresso.challenge.domain.entities.ChallengeEncouragement;

/**
 * PostgreSQL data provider for challenge encouragement operations.
 */
@Repository
public interface ChallengeEncouragementPSQLProvider extends JpaRepository<ChallengeEncouragement, Long> {

}
