package com.inspire17.ythelper.config.storage;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofGigabytes(50)); // 50GB max file size
        factory.setMaxRequestSize(DataSize.ofGigabytes(50)); // 50GB max request size
        return factory.createMultipartConfig();
    }
}
