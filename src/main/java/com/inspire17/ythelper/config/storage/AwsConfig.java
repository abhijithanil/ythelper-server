//package com.inspire17.ythelper.config.storage;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.AwsCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//
//@Configuration
//public class AwsConfig {
//    @Value("${storage.env}")
//    private String storageEnv;
//
//    @Value("${AWS_ACCESS_KEY}")
//    private String accessKey;
//
//    @Value("${AWS_SECRET_KEY}")
//    private String secretKey;
//
//    @Bean
//    @ConditionalOnProperty(name = "storage.env", havingValue = "AWS_S3")
//    public S3Client s3Client() {
//        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
//        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Constants.REGION).build();
//        S3Client s3Client = AmazonS3Client
//                .builder()
//                .region(Region.of(regionName))
//                .credentialsProvider(StaticCredentialsProvider.create(credentials))
//                .build();
//        return s3Client;
//    }
//}
