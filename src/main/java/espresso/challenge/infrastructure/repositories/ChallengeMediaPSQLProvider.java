package espresso.challenge.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import espresso.challenge.domain.entities.ChallengeMedia;

@Repository
public interface ChallengeMediaPSQLProvider extends JpaRepository<ChallengeMedia, Long> {
}
