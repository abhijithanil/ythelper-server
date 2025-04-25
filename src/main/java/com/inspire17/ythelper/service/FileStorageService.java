package com.inspire17.ythelper.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path audioStorageLocation = Paths.get("audio_instructions/");

    @SneakyThrows
    public String storeAudio(MultipartFile audioFile, String directory) {
        if (Files.notExists(audioStorageLocation)) {
            Files.createDirectories(audioStorageLocation);
        }
        String fileName = UUID.randomUUID() + "_" + audioFile.getOriginalFilename();
        Path targetLocation = audioStorageLocation.resolve(fileName);
        Files.copy(audioFile.getInputStream(), targetLocation);
        return "/files/audio_instructions/" + fileName;  // Return URL or path based on your static mapping
    }
}
