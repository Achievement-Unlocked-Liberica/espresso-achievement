package espresso.user.infrastructure.repositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import espresso.user.domain.entities.UserProfileImage;
import espresso.user.domain.operational.exceptionPolicy.UserException;
import espresso.user.domain.operational.validationPolicy.UserValidator;

import com.amazonaws.services.s3.model.CannedAccessControlList;

@Repository
public class UserProfilePictureS3Provider {
    @Autowired
    private AmazonS3 s3Client;

    @Value("${digitalocean.spaces.bucketName}")
    private String bucketName;

    // public String uploadImage(MultipartFile file, String keyName) throws
    // IOException {
    public String uploadImage(String basePath, UserProfileImage userProfileImage) throws IOException {
        try {
            UserValidator.validateUserProfileImageForUpload(userProfileImage);
            UserValidator.validateBasePath(basePath);
            
            // Build the path for the profile image
            String imagePath = basePath + "/" + userProfileImage.getUser().getEntityKey() + "." + userProfileImage.getImageExtension();

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(userProfileImage.getImageData().length);
            meta.setContentType(userProfileImage.getContentType());

            ByteArrayInputStream inputStream = new ByteArrayInputStream(userProfileImage.getImageData());

            PutObjectRequest putRequest = new PutObjectRequest(bucketName, imagePath, inputStream, meta);

            putRequest.setCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putRequest);

            return s3Client.getUrl(bucketName, imagePath).toString();
            
        } catch (UserException e) {
            // Re-throw domain exceptions as-is
            throw e;
        } catch (AmazonServiceException e) {
            throw UserException.profileUpdateFailed(
                userProfileImage != null && userProfileImage.getUser() != null ? 
                    userProfileImage.getUser().getEntityKey() : "unknown", 
                "S3 service error: " + e.getMessage()
            );
        } catch (Exception e) {
            throw UserException.profileUpdateFailed(
                userProfileImage != null && userProfileImage.getUser() != null ? 
                    userProfileImage.getUser().getEntityKey() : "unknown", 
                "Unexpected error uploading image to S3: " + e.getMessage()
            );
        }
    }
}
