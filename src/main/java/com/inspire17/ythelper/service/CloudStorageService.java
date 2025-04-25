package com.inspire17.ythelper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class CloudStorageService {
    @Autowired
    private GCPStorageService gcpStorageService;


    public String uploadFile(File file, String uniqueId, String storageType) throws IOException {
        if ("AWS_S3".equalsIgnoreCase(storageType)) {
            return "NOT IMPLEMENTED YET";
//            return s3StorageService.upload(file, uniqueId);
        } else if ("GCP_BUCKET".equalsIgnoreCase(storageType)) {
            return gcpStorageService.upload(file, uniqueId);
        }
        throw new IllegalArgumentException("Unsupported storage type: " + storageType);
    }
}
