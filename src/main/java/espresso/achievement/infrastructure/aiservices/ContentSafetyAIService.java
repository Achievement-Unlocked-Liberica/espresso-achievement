package espresso.achievement.infrastructure.aiservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import espresso.achievement.domain.contracts.IContentSafetyAIProvider;
import espresso.achievement.domain.contracts.IContentSafetyAIService;

@Component
public class ContentSafetyAIService implements IContentSafetyAIService {

    @Autowired
    private IContentSafetyAIProvider contentSafetyProvider;


    @Override
    public void verifyTextContent(String text) {
        contentSafetyProvider.verifyTextContent(text);
    }

    @Override
    public void verifyImageContent(byte[] imageData) {
        contentSafetyProvider.verifyImageContent(imageData);
    }
}
