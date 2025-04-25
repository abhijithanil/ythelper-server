package com.inspire17.ythelper.service;

import com.inspire17.ythelper.dto.*;
import com.inspire17.ythelper.entity.*;
import com.inspire17.ythelper.exceptions.ServerException;
import com.inspire17.ythelper.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    @Autowired
    private final VideoConversionService videoConverterService;

    @Autowired
    private final VideoRepository videoRepository;
    @Autowired
    private final VideoConversionStatusRepository videoConversionRepository;
    @Autowired
    private final VideoMetadataRepository videoMetadataRepository;
    @Autowired
    private final CloudStorageService cloudStorageService; // Handles S3, GCP, etc.
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final InstructionRepository instructionRepository;

    @Autowired
    private final ChannelRepository channelRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Value("${storage.env}")
    private String storageEnv;

    @Value("${video.local.path}")
    private String localStoragePath;

    @Transactional(rollbackFor = {Exception.class})
    public String uploadVideo(MultipartFile file, String fileExtension, String title, String channelId, Integer revisionNumber, AccountInfoDto accountInfo) throws IOException {
        String uniqueId = UUID.randomUUID().toString();
        Map<String, String> filePath;
        final VideoEntity video = new VideoEntity();
        video.setId(uniqueId);

        try {
            if ("HOST_MACHINE".equals(storageEnv)) {
                filePath = saveToLocal(file, fileExtension, uniqueId);
            } else {
                filePath = saveToCloud(file, fileExtension, uniqueId);
            }

            // Step 3: Store Video Entry in PostgreSQL
            Optional<UserEntity> userEntity = userRepository.findByUsername(accountInfo.getName());
            if (userEntity.isEmpty()) {
                throw new ServerException("Failed to verify user", 403);
            }

            Optional<ChannelEntity> channelEntity = channelRepository.findById(Long.valueOf(channelId));

            if (channelEntity.isEmpty()) {
                throw new ServerException("Failed to verify channel", 403);
            }


            video.setTitle(title);
            video.setParentId(null);
            video.setUploadedBy(userEntity.get());
            video.setChannel(channelEntity.get());
            video.setStatus(VideoStatus.TODO);
            video.setRevisionId(revisionNumber);
            video.setUploadedAt(LocalDateTime.now());
            video.setOriginalFilePath(filePath.get("original"));
            video.setMp4filePath(filePath.get("mp4"));

            videoRepository.save(video);
            VideoConversionStatusEntity conversionStatusEntity = new VideoConversionStatusEntity();

            conversionStatusEntity.setVideo(video);

            conversionStatusEntity.setStatus(fileExtension.equals("mp4"));

            videoConversionRepository.save(conversionStatusEntity);

            log.info("Video uploaded successfully with ID: {}", uniqueId);
            return uniqueId;
        } catch (Exception e) {
            log.error("Error uploading video: {}", e.getMessage(), e);
            throw new IOException("Failed to upload video", e);
        }
    }


    private Map<String, String> saveToLocal(MultipartFile file, String fileExtension, String uniqueId) throws IOException {
        Map<String, String> filePaths = new HashMap<>();

        Path uploadDir = Paths.get(localStoragePath);
        if (Files.notExists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path originalDir = uploadDir.resolve("original");
        if (Files.notExists(originalDir)) {
            Files.createDirectories(originalDir);
        }

        Path originalFilePath = originalDir.resolve(uniqueId + "." + fileExtension);
        try {
            Files.copy(file.getInputStream(), originalFilePath, StandardCopyOption.REPLACE_EXISTING);
            filePaths.put("original", originalFilePath.toString());
            log.info("Video saved locally at: {}", originalFilePath);
        } catch (IOException e) {
            log.error("Failed to save original video locally: {}", e.getMessage());
            throw new IOException("Failed to save original video", e);
        }

        // Convert to MP4 only if it's not already an MP4 file
        if (!fileExtension.equalsIgnoreCase("mp4")) {
            // Ensure "mp4" directory exists
            Path mp4Dir = uploadDir.resolve("mp4");
            if (Files.notExists(mp4Dir)) {
                Files.createDirectories(mp4Dir);
            }

            Path mp4FilePath = mp4Dir.resolve(uniqueId + ".mp4");

            File inputFile = originalFilePath.toFile();
            File outputFile = mp4FilePath.toFile();

            videoConverterService.convertToMP4(inputFile, outputFile, uniqueId);
            filePaths.put("mp4", mp4FilePath.toString());


        } else {
            filePaths.put("mp4", originalFilePath.toString()); // Already in MP4 format
        }

        return filePaths;
    }


    private Map<String, String> saveToCloud(MultipartFile file, String fileExtension, String uniqueId) throws IOException {
        Map<String, String> filePaths = new HashMap<>();
        File tempFile = null;
        File tempMp4File = null;

        try {
            // ✅ Create a temp file for the original upload
            tempFile = Files.createTempFile(uniqueId, "." + fileExtension).toFile();
            file.transferTo(tempFile);

            // ✅ Upload original high-resolution video
            String originalFilePath = cloudStorageService.uploadFile(tempFile, uniqueId + "." + fileExtension, storageEnv);
            filePaths.put("original", originalFilePath);
            log.info("✅ Video saved to cloud at: {}", originalFilePath);

            // ✅ Convert and upload MP4 version if needed
            if (!fileExtension.equalsIgnoreCase("mp4")) {
                tempMp4File = Files.createTempFile(uniqueId, ".mp4").toFile();
                videoConverterService.convertToMP4AndUpload(tempFile, tempMp4File, cloudStorageService, uniqueId, storageEnv);

            } else {
                filePaths.put("mp4", originalFilePath); // Already MP4, no conversion needed
            }
        } catch (IOException e) {
            log.error("❌ Error while saving video to cloud: {}", e.getMessage());
            throw new IOException("Failed to upload video", e);
        } finally {
            // ✅ Cleanup temporary files
            if (tempFile != null && tempFile.exists()) tempFile.delete();
            if (tempMp4File != null && tempMp4File.exists()) tempMp4File.delete();
        }

        return filePaths;
    }


    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    public Resource getResource(String id, AccountInfoDto accountInfo) {
        Optional<VideoEntity> videoEntity = videoRepository.findById(id);
        if (videoEntity.isEmpty()) {
            throw new ServerException("Video not found", 404);
        }

        Path filePath = Paths.get(videoEntity.get().getMp4filePath());
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
            return resource;
        } catch (MalformedURLException e) {
            log.error("Malformed url error: {}", e.getMessage());
            throw new ServerException("Video not found", 404);
        }

    }

    public boolean postMetaData(VideoMetaDataDto metaDataDto, AccountInfoDto accountInfo) {
        if (metaDataDto.getId() != null) {
            throw new ServerException("Invalid request, metadata id is self assigned", 400);
        }
        VideoMetadata videoMetadata = modelMapper.map(metaDataDto, VideoMetadata.class);

        if (videoMetadata == null) {
            throw new ServerException("Invalid request", 400);
        }

        if (validateMetadata(metaDataDto, accountInfo)) {
            videoMetadataRepository.save(videoMetadata);
            return true;
        }
        return false;
    }

    private boolean validateMetadata(VideoMetaDataDto metaDataDto, AccountInfoDto accountInfo) {
        String videoId = metaDataDto.getVideoId();
        Optional<VideoEntity> videoEntity = videoRepository.findById(videoId);
        if (videoEntity.isEmpty()) {
            throw new ServerException("Video not found", 400);
        }

        if (!videoEntity.map(entity -> entity.getUploadedBy().getUsername().equals(accountInfo.getName())).orElse(false)) {
            throw new ServerException("Failed to validate uploaded by for the video", 400);
        }

        metaDataDto.getEditorInstructions().forEach(instructionDto -> {
            String instructionId = instructionDto.getInstructionId();
            Optional<InstructionEntity> instruction = instructionRepository.findById(instructionId);
            if (instruction.isEmpty()) {
                throw new ServerException("Video instruction are invalid", 400);
            }
            if (!instruction.get().getVideoId().equals(metaDataDto.getVideoId())) {
                throw new ServerException("Instruction doesn't belong to this video", 400);
            }
        });

        return false;
    }

    public List<VideoDto> getResources(String channelId, AccountInfoDto accountInfo) {
        Optional<ChannelEntity> channelEntity = channelRepository.findById(Long.valueOf(channelId));

        List<VideoDto> videoDtos = new ArrayList<>();
        if (channelEntity.isEmpty()) {
            return videoDtos;
        }

        List<VideoEntity> videos = videoRepository.find0thRevisionByChannel(channelEntity.get());
        videos.forEach(v -> {
            VideoDto videoDto = new VideoDto();
            Optional<VideoConversionStatusEntity> byVideoStatus = videoConversionRepository.findByVideoId(v.getId());
            if (byVideoStatus.isEmpty() || !byVideoStatus.get().isStatus()) {
                return;
            }

//            Optional<VideoMetadata> metadata = videoMetadataRepository.findById(v.getId());
            Optional<VideoMetadata> metadata = Optional.empty();
            if (metadata.isPresent()) {
                videoDto.setThumbnail(metadata.get().getThumbnailUrl());
            } else {
                videoDto.setThumbnail("https://media.licdn.com/dms/image/v2/D4E12AQEhA3mEpo1kvA/article-cover_image-shrink_720_1280/article-cover_image-shrink_720_1280/0/1663862064724?e=2147483647&v=beta&t=cGmdXnz2hxBciEnSVGaWEUpPdZfqp30Rczum6TJAkS8");
            }

            videoDto.setStatus(v.getStatus());
            videoDto.setId(v.getId());
            videoDto.setRevisionId(v.getRevisionId());
            videoDto.setChannelName(v.getChannel().getChannelName());
            videoDto.setTitle(v.getTitle());

            videoDtos.add(videoDto);

        });
        return videoDtos;
    }

    public Optional<VideoDto> getDetails(String id, AccountInfoDto accountInfo) {
        Optional<VideoEntity> videoEntity = videoRepository.findById(id);
        Optional<VideoMetadata> videoMetadata = videoMetadataRepository.findById(id);

        VideoDto dto = new VideoDto();
        if (videoEntity.isEmpty()) {
            return Optional.empty();
        }

        VideoEntity entity = videoEntity.get();
        dto.setTitle(entity.getTitle());
        dto.setId(entity.getId());
        dto.setChannelId(entity.getChannel().getId());
        dto.setChannelName(entity.getChannel().getChannelName());
        if (videoMetadata.isPresent()) {
            VideoMetadata metadata = videoMetadata.get();
            dto.setDescription(metadata.getDescription());
        }

        return Optional.of(dto);
    }

    public List<VideoDto> getRevisions(String videoId, AccountInfoDto accountInfo) {
        Optional<VideoEntity> videoEntity = videoRepository.findById(videoId);
        List<VideoDto> videoDtos = new ArrayList<>();
        if (videoEntity.isEmpty()) {
            return videoDtos;
        }

        if (videoEntity.get().getRevisionId() == 1) {
            List<VideoEntity> byParentId = videoRepository.findByParentId(videoEntity.get());
            if (byParentId == null || byParentId.isEmpty()) {
                return videoDtos;
            }

            byParentId.forEach(v -> {
                VideoDto dto = new VideoDto();
                dto.setId(v.getId());
                dto.setRevisionId(v.getRevisionId());
                dto.setUploadedAt(v.getUploadedAt());
                videoDtos.add(dto);
            });

            return videoDtos.stream().sorted(Comparator.comparing(VideoDto::getUploadedAt))
                    .collect(Collectors.toList());

        } else if (videoEntity.get().getParentId() != null) {
            return getRevisions(videoEntity.get().getParentId().getId(), accountInfo);
        } else {
            return new ArrayList<>();
        }

    }

    @Transactional(rollbackFor = {Exception.class})
    public String uploadEditedVideos(MultipartFile file, String fileExtension, String channelId, String parentId, AccountInfoDto accountInfo) throws IOException {
        String uniqueId = UUID.randomUUID().toString();
        Map<String, String> filePath;
        final VideoEntity video = new VideoEntity();
        video.setId(uniqueId);

        try {
            if ("HOST_MACHINE".equals(storageEnv)) {
                filePath = saveToLocal(file, fileExtension, uniqueId);
            } else {
                filePath = saveToCloud(file, fileExtension, uniqueId);
            }

            // Step 3: Store Video Entry in PostgreSQL
            Optional<UserEntity> userEntity = userRepository.findByUsername(accountInfo.getName());
            if (userEntity.isEmpty()) {
                throw new ServerException("Failed to verify user", 403);
            }

            Optional<ChannelEntity> channelEntity = channelRepository.findById(Long.valueOf(channelId));

            if (channelEntity.isEmpty()) {
                throw new ServerException("Failed to verify channel", 403);
            }
            Optional<VideoEntity> parentEntityObj = videoRepository.findById(parentId);

            if (parentEntityObj.isEmpty()) {
                throw new ServerException("Unable to upload revisions without base", 400);
            }

            if (channelId.equals(parentEntityObj.get().getChannel().getChannelName())) {
                throw new ServerException("Unable to upload revisions, unmatched to base revision channel", 400);
            }

            VideoEntity parentEntity = parentEntityObj.get();

            int latestRevisionNumber = videoRepository.findLatestRevisionNumber(parentEntity.getId());


            video.setTitle(parentEntity.getTitle());
            video.setParentId(parentEntity);
            video.setUploadedBy(userEntity.get());
            video.setChannel(channelEntity.get());
            video.setStatus(parentEntity.getStatus());
            video.setRevisionId(latestRevisionNumber);
            video.setUploadedAt(LocalDateTime.now());
            video.setOriginalFilePath("");
            video.setMp4filePath(filePath.get("mp4"));

            videoRepository.save(video);
            VideoConversionStatusEntity conversionStatusEntity = new VideoConversionStatusEntity();

            conversionStatusEntity.setVideo(video);

            conversionStatusEntity.setStatus(fileExtension.equals("mp4"));

            videoConversionRepository.save(conversionStatusEntity);

            log.info("Video uploaded successfully with ID: {}", uniqueId);
            return uniqueId;
        } catch (Exception e) {
            log.error("Error uploading video: {}", e.getMessage(), e);
            throw new IOException("Failed to upload video", e);
        }
    }

    public VideoStatusDto getVideoStatus(String videoId) {
        Optional<VideoEntity> videoEntityOpt = videoRepository.findById(videoId);
        VideoStatusDto videoStatusDto = new VideoStatusDto();
        if (videoEntityOpt.isPresent()) {
            VideoEntity videoEntity = videoEntityOpt.get();
            videoStatusDto.setVideoStatus(videoEntity.getStatus());
            videoStatusDto.setVideoId(videoEntity.getId());
        }
        return videoStatusDto;
    }
}