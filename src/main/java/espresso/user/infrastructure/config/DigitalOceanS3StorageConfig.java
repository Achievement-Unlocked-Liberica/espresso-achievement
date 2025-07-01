package espresso.user.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class DigitalOceanS3StorageConfig {
    @Value("${digitalocean.spaces.endpoint}")
    private String spacesEndpoint;

    @Value("${digitalocean.spaces.accessKey}")
    private String accessKey;

    @Value("${digitalocean.spaces.secretKey}")
    private String secretKey;

    @Bean
    public AmazonS3 s3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(spacesEndpoint, "sfo3"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}
