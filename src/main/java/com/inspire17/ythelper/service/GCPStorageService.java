package com.inspire17.ythelper.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class GCPStorageService {

    private static final String BUCKET_NAME = "yt-helper";
    private static final String STORAGE_BASE_URL = "https://storage.googleapis.com/";
    private static final Logger logger = Logger.getLogger(GCPStorageService.class.getName());

    private final Storage storage;

    /**
     * Uploads a file to Google Cloud Storage.
     *
     * @param file    The file to upload.
     * @param videoId The unique video ID for storage reference.
     * @return The publicly accessible URL of the uploaded file.
     * @throws IOException If an error occurs during file upload.
     */
    public String upload(File file, String videoId) throws IOException {
        String objectName = "videos/" + videoId;

        // ✅ Determine content type based on file extension
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream"; // Default content type
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            BlobId blobId = BlobId.of(BUCKET_NAME, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType) // ✅ Ensures correct content type
                    .build();

            storage.create(blobInfo, fileInputStream);

            String fileUrl = STORAGE_BASE_URL + BUCKET_NAME + "/" + objectName;
            logger.info("✅ File uploaded successfully to: " + fileUrl);
            return fileUrl;
        } catch (StorageException e) {
            logger.severe("❌ Error uploading file to GCP Storage: " + e.getMessage());
            throw new IOException("Failed to upload file to Google Cloud Storage", e);
        }
    }

    /**
     * Deletes a file from Google Cloud Storage.
     *
     * @param videoId The unique video ID used for storage reference.
     * @return true if the file was successfully deleted, false if it did not exist.
     */
    public boolean deleteFile(String videoId) {
        String objectName = "videos/" + videoId;
        BlobId blobId = BlobId.of(BUCKET_NAME, objectName);

        try {
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                logger.info("✅ File deleted successfully from GCP Storage: " + objectName);
            } else {
                logger.warning("⚠️ File not found in GCP Storage: " + objectName);
            }
            return deleted;
        } catch (StorageException e) {
            logger.severe("❌ Error deleting file from GCP Storage: " + e.getMessage());
            return false;
        }
    }
}
