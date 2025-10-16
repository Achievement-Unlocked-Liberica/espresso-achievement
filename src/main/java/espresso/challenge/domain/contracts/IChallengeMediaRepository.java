package espresso.challenge.domain.contracts;

import java.io.IOException;

import espresso.challenge.domain.entities.Challenge;
import espresso.challenge.domain.entities.ChallengeMedia;

public interface IChallengeMediaRepository {
    ChallengeMedia save(Challenge challenge, ChallengeMedia challengeMedia) throws IOException;
}
