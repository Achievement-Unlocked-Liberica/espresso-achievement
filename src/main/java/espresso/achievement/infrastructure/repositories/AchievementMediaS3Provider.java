package espresso.achievement.infrastructure.repositories;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import espresso.achievement.domain.entities.AchievementMedia;

import com.amazonaws.services.s3.model.CannedAccessControlList;

@Repository
public class AchievementMediaS3Provider {
    @Autowired
    private AmazonS3 s3Client;

    @Value("${digitalocean.spaces.bucketName}")
    private String bucketName;

    public String uploadImage(String basePath, AchievementMedia achievementMedia) throws IOException {

        try {
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
        } catch (Exception e) {
            throw new IOException("Error uploading achievement media to S3: " + e.getMessage(), e);
        }
    }
}
