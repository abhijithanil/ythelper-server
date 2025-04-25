package com.inspire17.ythelper.controllers;

import com.google.gson.JsonObject;
import com.inspire17.ythelper.dto.*;
import com.inspire17.ythelper.exceptions.ServerException;
import com.inspire17.ythelper.helper.Helper;
import com.inspire17.ythelper.service.InstructionService;
import com.inspire17.ythelper.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private InstructionService instructionService;


    @PostMapping("/video/raw_upload")
    public ResponseEntity<VideoUploadResponseDto> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("channelId") String channelId) {
        VideoUploadResponseDto videoUploadResponseDto;
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        if (accountInfo.getUserRole() != UserRole.ADMIN) {
            videoUploadResponseDto = new VideoUploadResponseDto("unauthorized to use this endpoint", 403);
            return ResponseEntity.status(403).body(videoUploadResponseDto);
        }
        String filename = file.getOriginalFilename();
        assert filename != null;
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        List<String> allowedFormats = List.of("mp4", "webm", "mov", "mkv");
        if (!allowedFormats.contains(fileExtension)) {
            videoUploadResponseDto = new VideoUploadResponseDto("Unsupported video format!", 403);
            return ResponseEntity.status(415).body(videoUploadResponseDto);
        }

        try {
            String videoId = videoService.uploadVideo(file, fileExtension, title, channelId, 1, accountInfo);
            videoUploadResponseDto = new VideoUploadResponseDto("Video uploaded successfully", 200, videoId);
            return ResponseEntity.ok().body(videoUploadResponseDto);
        } catch (IOException e) {
            log.error("Error uploading video: {}", e.getMessage());
            videoUploadResponseDto = new VideoUploadResponseDto("Error uploading video", 500);
            return ResponseEntity.internalServerError().body(videoUploadResponseDto);
        }
    }

    @PostMapping("/video/edited/upload")
    public ResponseEntity<VideoUploadResponseDto> uploadEditedVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("parentId") String parentId,
            @RequestParam("channelId") String channelId) {
        VideoUploadResponseDto videoUploadResponseDto;
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        if (accountInfo.getUserRole() == UserRole.USERS) {
            videoUploadResponseDto = new VideoUploadResponseDto("unauthorized to use this endpoint", 403);
            return ResponseEntity.status(403).body(videoUploadResponseDto);
        }

        String filename = file.getOriginalFilename();
        assert filename != null;
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        List<String> allowedFormats = List.of("mp4");
        if (!allowedFormats.contains(fileExtension)) {
            videoUploadResponseDto = new VideoUploadResponseDto("Unsupported video format!", 403);
            return ResponseEntity.status(415).body(videoUploadResponseDto);
        }

        try {
            String videoId = videoService.uploadEditedVideos(file, fileExtension, channelId, parentId, accountInfo);
            videoUploadResponseDto = new VideoUploadResponseDto("Video uploaded successfully", 200, videoId);
            return ResponseEntity.ok().body(videoUploadResponseDto);
        } catch (IOException e) {
            log.error("Error uploading video: {}", e.getMessage());
            videoUploadResponseDto = new VideoUploadResponseDto("Error uploading video", 500);
            return ResponseEntity.internalServerError().body(videoUploadResponseDto);
        }
    }



    @PostMapping("video/metadata")
    public ResponseEntity<String> addMetaData(@RequestBody VideoMetaDataDto metaDataDto) {
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        JsonObject responseObject = new JsonObject();
        if (accountInfo.getUserRole() != UserRole.ADMIN) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "unauthorized to use this endpoint");
            return ResponseEntity.status(403).body(responseObject.toString());
        }

        boolean status = videoService.postMetaData(metaDataDto, accountInfo);
        if (!status) {
            responseObject.addProperty("message", "Content validation failed");
            responseObject.addProperty("status", false);
            return ResponseEntity.badRequest().body(responseObject.toString());
        }

        responseObject.addProperty("status", true);
        return ResponseEntity.ok(responseObject.toString());
    }


    @GetMapping("/video/view")
    public ResponseEntity<?> getVideo(@RequestParam String id) {
        try {
            final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
//            final AccountInfoDto accountInfo = null;
            Resource resource = videoService.getResource(id, accountInfo);
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Video not found", "id", id));
            }
            log.info("Streaming");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + id + ".mp4\"")
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes") // Important for streaming
                    .body(resource);
        } catch (Exception e) {
            log.error("Exception streaming: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON) // Ensure JSON response for errors
                    .body(Map.of("error", "Internal Server Error", "message", e.getMessage()));
        }
    }

    @GetMapping("/video/details")
    public ResponseEntity<VideoDto> getVideoDetails(@RequestParam String id) {
        try {
            final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
            Optional<VideoDto> videoDto = videoService.getDetails(id, accountInfo);
            if (videoDto.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new VideoDto());
            }

            return ResponseEntity.ok()
                    .body(videoDto.get());
        } catch (Exception e) {
            log.error("Error fetching video details: {}", e.getMessage());
            throw new ServerException("Unable to fetch video details", 500);
        }
    }


    @GetMapping("/videos")
    public ResponseEntity<List<VideoDto>> getVideos(@RequestParam(required = false) String channelId) {
        log.info("Getting videos from channel {}", channelId);

        try {
            final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());

            List<VideoDto> resources = videoService.getResources(channelId, accountInfo);
            if (resources.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok().body(resources);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/download/raw/{filename}")
    public ResponseEntity<Resource> downloadRawVideo(@PathVariable String filename) throws IOException {
        File file = new File("videos/raw/" + filename);
        if (!file.exists()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        Resource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .body(resource);
    }

    @GetMapping("videos/revisions")
    public ResponseEntity<List<VideoDto>> getVideRevisions(@RequestParam String videoId) {
        try {
            final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
            List<VideoDto> videosDto = videoService.getRevisions(videoId, accountInfo);
            if (videosDto.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
            }

            return ResponseEntity.ok()
                    .body(videosDto);
        } catch (Exception e) {
            log.error("Error fetching video details: {}", e.getMessage());
            throw new ServerException("Unable to fetch video details", 500);
        }
    }
}
