package espresso.challenge.domain.contracts;

import espresso.challenge.domain.events.ChallengeEncouragementEvent;
import espresso.challenge.domain.events.ChallengeCommentEvent;
import espresso.challenge.domain.events.ChallengeEvent;
import espresso.challenge.domain.events.ChallengeMediaEvent;

public interface IChallengeEventHandler {

    void handleEvent(ChallengeEvent event);

    void handleEvent(ChallengeEncouragementEvent event);

    void handleEvent(ChallengeCommentEvent event);

    void handleEvent(ChallengeMediaEvent event);

}
