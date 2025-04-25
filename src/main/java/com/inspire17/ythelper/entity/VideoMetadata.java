package com.inspire17.ythelper.entity;

import com.inspire17.ythelper.dto.InstructionDto;
import com.inspire17.ythelper.helper.annotations.ToJsonString;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "yt_video_metadata")
@Getter
@Setter
@ToJsonString
public class VideoMetadata {

    public String storageType;
    @Id
    private String id;
    private String videoId;
    private String description;
    private String title;
    private String storageEnv;
    private String rawVideoUrl;
    private String thumbnailUrl;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String url;
}
