package com.inspire17.ythelper.entity;

import com.inspire17.ythelper.dto.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "yt_channel_permissions")
@Getter
@Setter
public class ChannelPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelEntity channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole; // EDITOR, USER
}
