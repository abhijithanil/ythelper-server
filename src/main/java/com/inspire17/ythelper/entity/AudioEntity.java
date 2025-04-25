package com.inspire17.ythelper.entity;

import com.inspire17.ythelper.dto.AudioType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "yt_audio")
@Getter
@Setter
public class AudioEntity {
    @Id
    private String id;

    private AudioType audioType;

    private String instructionId;

    private String commentId;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private UserEntity uploadedBy;


    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(name = "file_path", nullable = false)
    private String filePath;
}
