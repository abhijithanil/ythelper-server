package com.inspire17.ythelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class YtUploadHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtUploadHelperApplication.class, args);
    }

}
