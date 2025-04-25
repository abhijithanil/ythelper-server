package com.inspire17.ythelper.dto;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class VideoMetaDataDto extends BaseDto {
    public String storageType;
    @Id
    private String id;
    private String videoId;
    private String description;
    private String title;
    private String storageEnv;
    private String rawVideoUrl;
    private List<InstructionDto> editorInstructions;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String url;
}
