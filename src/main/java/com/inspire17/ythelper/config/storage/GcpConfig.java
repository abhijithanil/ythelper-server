package com.inspire17.ythelper.config.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;


@Configuration
public class GcpConfig {

    @Value("${storage.env}")
    private String storageEnv;

    @Bean
    @ConditionalOnProperty(name = "storage.env", havingValue = "GCP_BUCKET")
    public Storage storage() {
//        return StorageOptions.newBuilder()
//                .setCredentials(GoogleCredentials.fromStream(new FileInputStream("/Users/aj/gcp-service-account.json")))
//                .build()
//                .getService();
        return StorageOptions.getDefaultInstance().getService();
    }
}