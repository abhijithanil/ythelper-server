package com.inspire17.ythelper.repository;

import com.inspire17.ythelper.dto.AccountStatus;
import com.inspire17.ythelper.dto.UserRole;
import com.inspire17.ythelper.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmailId(String emailId);

    boolean existsByEmailId(String email);

    boolean existsByUsername(String userName);

    long countByUserRole(UserRole userRole);

    List<UserEntity> findByUserRoleAndAccountStatus(UserRole userRole, AccountStatus accountStatus);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.password = :newPassword WHERE u.emailId = :emailId")
    int updatePasswordByEmailId(String emailId, String newPassword);

    List<UserEntity> findByUserRole(UserRole userRole);
}