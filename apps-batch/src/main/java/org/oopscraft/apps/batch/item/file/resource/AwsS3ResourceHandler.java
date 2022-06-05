package org.oopscraft.apps.batch.item.file.resource;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class AwsS3ResourceHandler extends ResourceHandler {

    private static final String S3_PROTOCOL_PREFIX = "s3://";

    public static final int AWS_DEFAULT_TIMEOUT = 1000 * 60 * 60;

    @Override
    public boolean supports(String filePath) {
        Assert.notNull(filePath, "filePath must not be null");
        return filePath.toLowerCase().startsWith(S3_PROTOCOL_PREFIX);
    }

    @Override
    public Resource createReadableResource(String filePath) {
        log.info("AwsS3ResourceHandler.createReadableResource[{}]", filePath);
        AmazonS3 s3Client = createAmazonS3Client();
        AmazonS3URI amazonS3URI = new AmazonS3URI(filePath);
        String bucket = amazonS3URI.getBucket();
        String key = amazonS3URI.getKey();
        GetObjectRequest request = new GetObjectRequest(bucket, key);
        log.info("AmazonS3.getObject({},{})", bucket, key);
        InputStream is = s3Client.getObject(request).getObjectContent();
        return new InputStreamResource(is);
    }

    @Override
    public Resource createWritableResource(String filePath) {
        log.info("AwsS3ResourceHandler.createWritableResource[{}]", filePath);
        try {
            AmazonS3URI amazonS3URI = new AmazonS3URI(filePath);
            String bucket = amazonS3URI.getBucket();
            String key = amazonS3URI.getKey();
            File tempFile = File.createTempFile(String.format("%s.", key), ".tmp");
            tempFile.deleteOnExit();

            // if exists object
            AmazonS3 s3Client = createAmazonS3Client();
            if (s3Client.doesObjectExist(bucket, key)) {
                InputStream is = s3Client.getObject(bucket, key).getObjectContent();
                try {
                    FileUtils.copyInputStreamToFile(is, tempFile);
                } finally {
                    if (is != null) {
                        try { is.close(); } catch (Exception ignore) {}
                    }
                }
            }
            return new FileSystemResource(tempFile);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flushWritableResource(Resource resource, String filePath) {
        log.info("AwsS3ResourceHandler.putWritableResource[{},{}]", resource, filePath);
        try {
            AmazonS3 s3Client = createAmazonS3Client();
            AmazonS3URI amazonS3URI = new AmazonS3URI(filePath);
            String bucket = amazonS3URI.getBucket();
            String key = amazonS3URI.getKey();
            log.info("uploads completed tempFile[{},{}] to S3[{},{}]", resource.getFile().getAbsolutePath(), resource.getFile().length(), bucket, key);
            if (s3Client.doesObjectExist(bucket, key)) {
                s3Client.deleteObject(bucket, key);
            }
            s3Client.putObject(bucket, key, resource.getFile());
        } catch (Exception e) {
            log.warn("upload S3 error", e);
            throw new RuntimeException(e);
        }
    }

    /*
     * createAmazonS3Client
     */
    private AmazonS3 createAmazonS3Client() {

        // create builder
        AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard();

        // credentials
        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        s3ClientBuilder.withCredentials(new AWSStaticCredentialsProvider(credentials));

        // endpoint
        Map<String,String> dataHomeProperties = BatchConfig.getDataHomeProperties();
        String endpoint = dataHomeProperties.get("endpoint");
        String region = dataHomeProperties.get("region");
        if(endpoint != null && endpoint.trim().length() > 0) {
            Assert.notNull(region, "region is not defined");
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
            s3ClientBuilder.withEndpointConfiguration(endpointConfiguration);
            s3ClientBuilder.enablePathStyleAccess();
        }

        // config
        ClientConfiguration config = new ClientConfiguration();
        config.setClientExecutionTimeout(AWS_DEFAULT_TIMEOUT);
        s3ClientBuilder.withClientConfiguration(config);

        // return
        return s3ClientBuilder.build();
    }

}
