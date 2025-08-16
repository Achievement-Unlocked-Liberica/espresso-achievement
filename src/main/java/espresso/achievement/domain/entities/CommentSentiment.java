package espresso.achievement.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object representing sentiment analysis for a comment.
 * Contains probability scores for positive, neutral, and negative sentiment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CommentSentiment {

    /**
     * Probability score for positive sentiment (0.0 to 1.0)
     */
    @Column(name = "sentimentPositive", nullable = false)
    private double positive;

    /**
     * Probability score for neutral sentiment (0.0 to 1.0)
     */
    @Column(name = "sentimentNeutral", nullable = false)
    private double neutral;

    /**
     * Probability score for negative sentiment (0.0 to 1.0)
     */
    @Column(name = "sentimentNegative", nullable = false)
    private double negative;

    /**
     * Creates a default sentiment with neutral values.
     * Used when sentiment analysis is not available or performed.
     * 
     * @return CommentSentiment with neutral sentiment
     */
    public static CommentSentiment createDefault() {
        return new CommentSentiment(0.0f, 1.0f, 0.0f);
    }

    /**
     * Creates a CommentSentiment with specified sentiment scores.
     * 
     * @param positive Positive sentiment score (0.0 to 1.0)
     * @param neutral Neutral sentiment score (0.0 to 1.0) 
     * @param negative Negative sentiment score (0.0 to 1.0)
     * @return CommentSentiment instance
     */
    public static CommentSentiment create(double positive, double neutral, double negative) {
        return new CommentSentiment(positive, neutral, negative);
    }
}
