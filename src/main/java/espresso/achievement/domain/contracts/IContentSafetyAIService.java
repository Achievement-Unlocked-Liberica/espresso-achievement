package espresso.achievement.domain.contracts;

public interface IContentSafetyAIService {

    void verifyTextContent(String text);

    void verifyImageContent(byte[] imageData);

}