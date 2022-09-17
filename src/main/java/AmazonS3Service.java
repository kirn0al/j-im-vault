import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

class AmazonS3Service {

    private static final String ACCESS_KEY_ENV = "accessKey";
    private static final String SECRET_KEY_ENV = "secretKey";
    private static final String BUCKET_ENV = "bucket";
    private static final String BUCKET_NAME = System.getenv().getOrDefault(BUCKET_ENV, BUCKET_ENV);
    private static final Logger logger = Logger.getLogger(AmazonS3Service.class);
    private final AmazonS3 amazonS3;

    AmazonS3Service() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
            System.getenv().getOrDefault(ACCESS_KEY_ENV, ACCESS_KEY_ENV),
            System.getenv().getOrDefault(SECRET_KEY_ENV, SECRET_KEY_ENV));
        this.amazonS3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(Regions.EU_WEST_2)
            .build();
    }

    String uploadToS3(InputStream inputStream) {
        String key = UUID.randomUUID().toString();
        amazonS3.putObject(BUCKET_NAME, key, inputStream, new ObjectMetadata());
        return key;
    }

    String getImageAsBase64(String key) {
        try (final S3ObjectInputStream s3ObjectInputStream = amazonS3.getObject(BUCKET_NAME, key).getObjectContent()) {
            return Base64.getEncoder().encodeToString(s3ObjectInputStream.getDelegateStream().readAllBytes());
        } catch (IOException e) {
            logger.error("An error occurred while reading a byte array");
            return "";
        }
    }
}
