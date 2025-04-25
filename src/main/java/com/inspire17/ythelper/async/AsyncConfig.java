package com.inspire17.ythelper.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "verificationEmailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);   // At least 2 threads always running
        executor.setMaxPoolSize(4);    // Can scale up to 4 threads when needed
        executor.setQueueCapacity(20); // Can queue up to 20 pending email requests
        executor.setThreadNamePrefix("VerificationEmailExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "videoConversionTaskExecutor")
    public Executor videoConversionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("videoConversionTaskExecutor-");
        executor.initialize();
        return executor;
    }
}
