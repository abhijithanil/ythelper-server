package com.inspire17.ythelper.entity;

import com.inspire17.ythelper.dto.CommentDataDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "video_comments")
@Getter
@Setter
public class VideoComment {
    @Id
    private String id;

    private Long videoId; // Reference to SQL VideoEntity

    private Long userId; // Who commented

    private String text; // Updated to support text

    private String audioUrl; // Updated to audio urls

    private LocalDateTime createdAt = LocalDateTime.now();
}