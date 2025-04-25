package com.inspire17.ythelper.service;

import com.inspire17.ythelper.repository.VideoConversionStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Service
public class VideoConversionService {

    @Autowired
    private VideoConversionStatusRepository videoConversionStatusRepository;

    @Async("videoConversionTaskExecutor")
    public void convertToMP4(File inputFile, File outputFile, String uniqueId) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", inputFile.getAbsolutePath(),
                    "-c:v", "libx264", "-crf", "23", "-preset", "fast",
                    "-c:a", "aac", "-b:a", "128k",
                    outputFile.getAbsolutePath()
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            boolean finished = process.waitFor(180, java.util.concurrent.TimeUnit.MINUTES);

            if (!finished) {
                process.destroy(); // ✅ Kill the process if it hangs
                log.error("FFmpeg process timed out!");
            } else if (process.exitValue() == 0) {
                log.info("Video successfully converted and saved as MP4.");
                videoConversionStatusRepository.updateStatus(uniqueId, true);
            } else {
                log.error("FFmpeg conversion failed with exit code: " + process.exitValue());
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error converting to MP4: " + e.getMessage());
        }
    }


    @Async("videoConversionTaskExecutor")
    public void convertToMP4AndUpload(File inputFile, File outputFile, CloudStorageService cloudStorageService, String uniqueId, String storageEnv) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", inputFile.getAbsolutePath(),
                    "-c:v", "libx264", "-crf", "23", "-preset", "fast",
                    "-c:a", "aac", "-b:a", "128k",
                    outputFile.getAbsolutePath()
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();
            if (process.exitValue() == 0) {
                log.info("Video converted and saved locally in MP4 format");
            } else {
                log.error("Video conversion failed");
            }
            String mp4FilePath = cloudStorageService.uploadFile(outputFile, uniqueId + ".mp4", storageEnv);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            log.error("Error converting/uploading  mp4 of {} :{}", uniqueId, e.getMessage());
        }
    }
}
