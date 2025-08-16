package espresso.achievement.infrastructure.helpers;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;

import espresso.achievement.domain.entities.CommentSentiment;

public class TextAnalyticsHelper {

    private static final String ENDPOINT = "https://<your-text-analytics-endpoint>.cognitiveservices.azure.com/";
    private static final String API_KEY = "";

    public static TextAnalyticsClient createTextAnalyticsClient() {
        return new TextAnalyticsClientBuilder()
                .endpoint(ENDPOINT)
                .credential(new AzureKeyCredential(API_KEY))
                .buildClient();
    }

    public static CommentSentiment analyzeSentiment(String text) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        TextAnalyticsClient client = createTextAnalyticsClient();

        var scores = client.analyzeSentiment(text).getConfidenceScores();

        CommentSentiment sentiment = CommentSentiment.create(
                scores.getPositive(),
                scores.getNeutral(),
                scores.getPositive());

        return sentiment;
    }

    public static String getLanguage(String text) {

        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        TextAnalyticsClient client = createTextAnalyticsClient();

        var iso6391Name = client.detectLanguage(text).getIso6391Name();

        return iso6391Name;
    }

}
