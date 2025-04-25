//package com.inspire17.ythelper.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.*;
//
//import java.io.IOException;
//import java.nio.file.Path;
//
//@Service
//@RequiredArgsConstructor
//public class S3StorageService {
//
//    private final S3Client s3Client;
//
//    public String upload(MultipartFile file, String videoId) throws IOException {
//        String key = "videos/" + videoId + "_" + file.getOriginalFilename();
//        s3Client.putObject(PutObjectRequest.builder()
//                        .bucket("your-s3-bucket-name")
//                        .key(key)
//                        .build(),
//                (Path) file.getInputStream());
//
//        return "https://your-s3-bucket-name.s3.amazonaws.com/" + key;
//    }
//
//    public void deleteFile(String videoId) {
//        s3Client.deleteObject(DeleteObjectRequest.builder()
//                .bucket("your-s3-bucket-name")
//                .key("videos/" + videoId)
//                .build());
//    }
//}
