package espresso.achievement.infrastructure.aiservices;

import org.springframework.stereotype.Component;
import espresso.achievement.domain.contracts.IContentSafetyAIProvider;
import espresso.achievement.domain.contracts.IContentSafetyAIService;

@Component
public class ContentSafetyAIService implements IContentSafetyAIService {

    private final IContentSafetyAIProvider contentSafetyProvider;

    /**
     * Constructor for dependency injection.
     * 
     * @param contentSafetyProvider AI provider for content safety verification operations
     */
    public ContentSafetyAIService(IContentSafetyAIProvider contentSafetyProvider) {
        this.contentSafetyProvider = contentSafetyProvider;
    }


    @Override
    public void verifyTextContent(String text) {
        contentSafetyProvider.verifyTextContent(text);
    }

    @Override
    public void verifyImageContent(byte[] imageData) {
        contentSafetyProvider.verifyImageContent(imageData);
    }
}
