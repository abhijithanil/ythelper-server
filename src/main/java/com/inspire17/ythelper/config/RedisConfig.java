package com.inspire17.ythelper.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.StringUtils;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;


    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        validateRedisConfig();

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        redisConfig.setPassword(redisPassword);

        return new LettuceConnectionFactory(redisConfig);
    }


    private void validateRedisConfig() {
        if (!StringUtils.hasText(redisHost)) {
            throw new IllegalArgumentException("Redis Host (spring.redis.host) is required and cannot be empty.");
        }
        if (redisPort <= 0 || redisPort > 65535) {
            throw new IllegalArgumentException("Redis Port (spring.redis.port) must be between 1 and 65535.");
        }
        if (!StringUtils.hasText(redisPassword)) {
            throw new IllegalArgumentException("Redis Password (spring.redis.password) cannot be empty.");
        }
    }
}
