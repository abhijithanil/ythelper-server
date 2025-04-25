package com.inspire17.ythelper.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "yt_video_editors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoEditorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String videoId;

    @OneToOne
    @JoinColumn(name = "editor_id", nullable = false)
    private UserEntity editorId;
}
