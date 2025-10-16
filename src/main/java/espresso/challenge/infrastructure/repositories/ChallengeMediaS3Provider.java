package espresso.challenge.infrastructure.repositories;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import espresso.challenge.domain.entities.ChallengeMedia;
import espresso.challenge.domain.operational.exceptionPolicy.ChallengeException;
import espresso.challenge.domain.operational.validationPolicy.ChallengeValidator;

import com.amazonaws.services.s3.model.CannedAccessControlList;

@Repository
public class ChallengeMediaS3Provider {
    
    private final AmazonS3 s3Client;
    private final String bucketName;

    /**
     * Constructor for dependency injection.
     * 
     * @param s3Client Amazon S3 client for storage operations
     * @param bucketName S3 bucket name for storing challenge media
     */
    public ChallengeMediaS3Provider(
            AmazonS3 s3Client,
            @Value("${digitalocean.spaces.bucketName}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public String uploadImage(String basePath, ChallengeMedia challengeMedia) {
        try {
            ChallengeValidator.validateChallengeMediaForUpload(challengeMedia);

            // Build the path for the challenge media
            String imagePath = basePath + "/" + challengeMedia.getImageName();

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(challengeMedia.getImageData().length);
            meta.setContentType(challengeMedia.getContentType());

            ByteArrayInputStream inputStream = new ByteArrayInputStream(challengeMedia.getImageData());

            PutObjectRequest putRequest = new PutObjectRequest(bucketName, imagePath, inputStream, meta);
            putRequest.setCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putRequest);

            return s3Client.getUrl(bucketName, imagePath).toString();

        } catch (ChallengeException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (AmazonServiceException e) {
            throw ChallengeException.mediaProcessingFailed("image", "AWS S3 service error: " + e.getErrorMessage());
        } catch (Exception e) {
            throw ChallengeException.mediaProcessingFailed("image", "Unexpected error during S3 upload: " + e.getMessage());
        }
    }
}
