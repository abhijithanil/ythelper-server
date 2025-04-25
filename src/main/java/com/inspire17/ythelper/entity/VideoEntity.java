package com.inspire17.ythelper.entity;

import com.inspire17.ythelper.dto.VideoStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "yt_video")
@Getter
@Setter
public class VideoEntity {
    @Id
    private String id;

    @Column(name = "video_title", nullable = false)
    private String title;

    @Column(name = "revision_id", nullable = false)
    private Integer revisionId;

    @ManyToOne //  parent-child relation mapping
    @JoinColumn(name = "parent_id")
    private VideoEntity parentId;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelEntity channel;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private UserEntity uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoStatus status;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(name = "original_file_path", nullable = false)
    private String originalFilePath;

    @Column(name = "mp4_file_path", nullable = false)
    private String mp4filePath;
}
