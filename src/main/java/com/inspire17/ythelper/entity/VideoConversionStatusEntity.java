package com.inspire17.ythelper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "yt_conversion_status")
@Getter
@Setter
public class VideoConversionStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "video_id", referencedColumnName = "id", nullable = false, unique = true)
    private VideoEntity video;

    @Column(name = "status", nullable = false)
    private boolean status;
}