package espresso.achievement.infrastructure.repositories;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import espresso.achievement.domain.entities.AchievementMedia;
import espresso.achievement.domain.operational.exceptionPolicy.AchievementException;
import espresso.achievement.domain.operational.validationPolicy.AchievementValidator;

import com.amazonaws.services.s3.model.CannedAccessControlList;

@Repository
public class AchievementMediaS3Provider {
    
    private final AmazonS3 s3Client;
    private final String bucketName;

    /**
     * Constructor for dependency injection.
     * 
     * @param s3Client Amazon S3 client for storage operations
     * @param bucketName S3 bucket name for storing achievement media
     */
    public AchievementMediaS3Provider(
            AmazonS3 s3Client,
            @Value("${digitalocean.spaces.bucketName}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String uploadImage(String basePath, AchievementMedia achievementMedia) {
        try {
            AchievementValidator.validateAchievementMediaForUpload(achievementMedia);

            // Build the path for the achievement media
            String imagePath = basePath + "/" + achievementMedia.getImageName();

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(achievementMedia.getImageData().length);
            meta.setContentType(achievementMedia.getContentType());

            ByteArrayInputStream inputStream = new ByteArrayInputStream(achievementMedia.getImageData());

            PutObjectRequest putRequest = new PutObjectRequest(bucketName, imagePath, inputStream, meta);
            putRequest.setCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putRequest);

            return s3Client.getUrl(bucketName, imagePath).toString();

        } catch (AchievementException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (AmazonServiceException e) {
            throw AchievementException.mediaProcessingFailed("image", "AWS S3 service error: " + e.getErrorMessage());
        } catch (Exception e) {
            throw AchievementException.mediaProcessingFailed("image", "Unexpected error during S3 upload: " + e.getMessage());
        }
    }
}
