package espresso.achievement.domain.contracts;

public interface IContentSafetyAIProvider {

    void verifyTextContent(String text);

    void verifyImageContent(byte[] imageData);

}