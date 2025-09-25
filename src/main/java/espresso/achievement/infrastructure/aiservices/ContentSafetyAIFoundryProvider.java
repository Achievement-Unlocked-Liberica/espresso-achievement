package espresso.achievement.infrastructure.aiservices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.azure.ai.contentsafety.ContentSafetyClient;
import com.azure.ai.contentsafety.ContentSafetyClientBuilder;
import com.azure.ai.contentsafety.models.AnalyzeImageOptions;
import com.azure.ai.contentsafety.models.AnalyzeImageResult;
import com.azure.ai.contentsafety.models.AnalyzeTextOptions;
import com.azure.ai.contentsafety.models.AnalyzeTextResult;
import com.azure.ai.contentsafety.models.ContentSafetyImageData;
import com.azure.ai.contentsafety.models.ImageCategoriesAnalysis;
import com.azure.ai.contentsafety.models.TextCategoriesAnalysis;
import com.azure.core.credential.KeyCredential;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.util.BinaryData;

import espresso.achievement.domain.contracts.IContentSafetyAIProvider;
import espresso.achievement.domain.operational.exceptionPolicy.AchievementException;
import espresso.achievement.domain.operational.validationPolicy.AchievementValidator;

@Component
public class ContentSafetyAIFoundryProvider implements IContentSafetyAIProvider {

    private String endpoint;
    private String apiKey;

    ContentSafetyClient contentSafetyClient;

    public ContentSafetyAIFoundryProvider(
            @Value("${azure.contentSafety.endpoint}") String endpoint,
            @Value("${azure.contentSafety.apiKey}") String apiKey) {

        try {
            AchievementValidator.validateConfigurationValue(endpoint, "Azure Content Safety endpoint");
            AchievementValidator.validateConfigurationValue(apiKey, "Azure Content Safety API key");

            // Values are already injected by Spring before constructor runs
            this.endpoint = endpoint; // Not null
            this.apiKey = apiKey; // Not null

            // Safe to use the values
            this.contentSafetyClient = new ContentSafetyClientBuilder()
                    .credential(new KeyCredential(this.apiKey))
                    .endpoint(this.endpoint)
                    .buildClient();
                    
        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (Exception e) {
            throw AchievementException.creationFailed("Failed to initialize Content Safety AI client: " + e.getMessage());
        }
    }

    @Override
    public void verifyTextContent(String text) {
        try {
            AchievementValidator.validateTextContent(text);

            // AnalyzeTextResult response = contentSafetyClient.analyzeText(new
            // AnalyzeTextOptions(text));

            // for (TextCategoriesAnalysis result : response.getCategoriesAnalysis()) {
            // System.out.println(result.getCategory() + " severity: " +
            // result.getSeverity());
            // }

            System.out.println("Text content security verification is not implemented yet.");
            
        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (HttpResponseException e) {
            throw AchievementException.creationFailed("Azure Content Safety service error during text verification: " + e.getMessage());
        } catch (Exception e) {
            throw AchievementException.creationFailed("Unexpected error occurred during text content verification: " + e.getMessage());
        }
    }

    @Override
    public void verifyImageContent(byte[] imageData) {
        try {
            AchievementValidator.validateImageData(imageData);

            // ContentSafetyImageData image = new ContentSafetyImageData();
            // image.setContent(BinaryData.fromBytes(imageData));
            // AnalyzeImageResult response = contentSafetyClient.analyzeImage(new AnalyzeImageOptions(image));
            // for (ImageCategoriesAnalysis result : response.getCategoriesAnalysis()) {
            //     System.out.println(result.getCategory() + " severity: " + result.getSeverity());
            // }

            System.out.println("Image content security verification is not implemented yet.");
            
        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (HttpResponseException e) {
            throw AchievementException.creationFailed("Azure Content Safety service error during image verification: " + e.getMessage());
        } catch (Exception e) {
            throw AchievementException.creationFailed("Unexpected error occurred during image content verification: " + e.getMessage());
        }
    }

}
