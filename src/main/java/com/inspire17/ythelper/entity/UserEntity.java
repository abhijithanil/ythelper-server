package com.inspire17.ythelper.entity;

import com.inspire17.ythelper.dto.AccountStatus;
import com.inspire17.ythelper.dto.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
// Name of the table where user is stored
@Table(name = "yt_user")
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)
    private String username;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email_id", nullable = false, unique = true)
    private String emailId;

    private boolean isEmailVerified;

    private boolean isUserApproved;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}