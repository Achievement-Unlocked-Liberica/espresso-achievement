package espresso.challenge.application.eventHandlers;

 
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import espresso.challenge.domain.contracts.IChallengeEventHandler;
import espresso.challenge.domain.events.ChallengeEncouragementEvent;
import espresso.challenge.domain.events.ChallengeCommentEvent;
import espresso.challenge.domain.events.ChallengeEvent;
import espresso.challenge.domain.events.ChallengeMediaEvent;
import espresso.challenge.infrastructure.integrations.ChallengeEventPublisher;

@Component
public class ChallengeEventHandler implements IChallengeEventHandler {

    private final ChallengeEventPublisher challengeEventPublisher;

    /**
     * Constructor for dependency injection.
     * 
     * @param challengeEventPublisher Event publisher for challenge events
     */
    public ChallengeEventHandler(ChallengeEventPublisher challengeEventPublisher) {
        this.challengeEventPublisher = challengeEventPublisher;
    }

    @Override
    @EventListener
    public void handleEvent(ChallengeEvent event) {
        this.challengeEventPublisher.publishEvent(event);
    }

    @Override
    @EventListener
    public void handleEvent(ChallengeEncouragementEvent event) {
        this.challengeEventPublisher.publishEvent(event);
    }

    @Override
    @EventListener
    public void handleEvent(ChallengeCommentEvent event) {
        this.challengeEventPublisher.publishEvent(event);
    }

    @Override
    @EventListener
    public void handleEvent(ChallengeMediaEvent event) {
        this.challengeEventPublisher.publishEvent(event);
    }

}
