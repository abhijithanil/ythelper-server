package com.inspire17.ythelper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "yt_channel")
@Getter
@Setter
public class ChannelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_name", nullable = false, unique = true)
    private String channelName;

    @Column(name = "youtube_channel_id", nullable = false, unique = true)
    private String youtubeChannelId;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private UserEntity admin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
