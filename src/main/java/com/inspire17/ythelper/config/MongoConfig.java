package com.inspire17.ythelper.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        validateMongoConfig();
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .build());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

    private String getDatabaseName() {
        return new ConnectionString(mongoUri).getDatabase();
    }

    private void validateMongoConfig() {
        if (mongoUri == null || mongoUri.trim().isEmpty()) {
            throw new IllegalArgumentException("MongoDB URI (spring.data.mongodb.uri) is required and cannot be empty.");
        }
    }
}
