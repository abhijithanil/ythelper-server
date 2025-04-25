package com.inspire17.ythelper.service;

import com.inspire17.ythelper.dto.AuthenticationDto;
import com.inspire17.ythelper.dto.EditorDetailsDto;
import com.inspire17.ythelper.dto.UserRole;
import com.inspire17.ythelper.dto.UserWrapperDto;
import com.inspire17.ythelper.entity.UserEntity;
import com.inspire17.ythelper.exceptions.ServerException;
import com.inspire17.ythelper.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepo;


    @Override
    public UserWrapperDto loadUserByUsername(String username) throws ServerException {
        log.info("Loading user by username: {}", username);

        Optional<UserEntity> user = userRepo.findByUsername(username);

        if (user.isPresent()) {
            log.debug("User found: {}", username);
            return new UserWrapperDto(user.get());
        } else {
            log.warn("User not found: {}", username);
            return null;
        }
    }

    public void saveUser(UserEntity user) {
        log.info("Saving user: {}", user.getUsername());
        // Check if username already exists (if not null)
        if (user.getUsername() != null && userRepo.existsByUsername(user.getUsername())) {
            throw new ServerException("Username is already taken!", 400);
        }

        // Check if email already exists
        if (userRepo.existsByEmailId(user.getEmailId())) {
            throw new ServerException("Email is already registered!", 400);
        }

        try {
            userRepo.save(user);
            log.debug("User saved successfully: {}", user.getUsername());
        } catch (DataIntegrityViolationException e) {
            log.error("Validation failed: {}", user.getUsername(), e);
            throw new ServerException("Validation failed", 400);
        }
    }

    public void updateUser(UserEntity user) {
        log.info("Updating user: {}", user.getUsername());
        // Check if username already exists (if not null)
        if (user.getUsername() == null) {
            throw new ServerException("Bad request", 400);
        }
        try {
            userRepo.save(user);
            log.debug("User updated successfully: {}", user.getUsername());
        } catch (DataIntegrityViolationException e) {
            log.error("Validation failed: {}", user.getUsername(), e);
            throw new ServerException("Validation failed", 400);
        }
    }

    @Transactional
    public void deleteUserWithEmail(@Valid String emailId) {
        Optional<UserEntity> optionalUserEntity = userRepo.findByEmailId(emailId);
        optionalUserEntity.ifPresent(userEntity -> userRepo.delete(userEntity));
    }


    public AuthenticationDto authenticateUsingUserName(String userName, String password, PasswordEncoder passwordEncoder) {
        log.info("Authenticating user by username: {}", userName);

        Optional<UserEntity> optionalUser = userRepo.findByUsername(userName);
        if (optionalUser.isEmpty()) {
            log.warn("Authentication failed - Username not found: {}", userName);
            throw new ServerException("Invalid username or password", 401);
        }

        UserEntity user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed - Incorrect password for user: {}", userName);
            throw new ServerException("Invalid username or password", 401);
        }

        log.info("User authenticated successfully: {}", userName);
        return new AuthenticationDto(true, user);
    }

    public AuthenticationDto authenticateUsingEmail(String email, String password, PasswordEncoder passwordEncoder) {
        log.info("Authenticating user by email: {}", email);

        Optional<UserEntity> optionalUser = userRepo.findByEmailId(email);
        if (optionalUser.isEmpty()) {
            log.warn("Authentication failed - Email not found: {}", email);
            throw new ServerException("Invalid email or password", 401);
        }

        UserEntity user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed - Incorrect password for email: {}", email);
            throw new ServerException("Invalid email or password", 401);
        }

        log.info("User authenticated successfully: {}", email);
        return new AuthenticationDto(true, user);
    }

    public boolean matchUserName(String q) {
        log.info("Checking if username exists: {}", q);

        boolean exists = userRepo.existsByUsername(q);

        if (exists) {
            log.debug("Username exists: {}", q);
        } else {
            log.debug("Username is available: {}", q);
        }

        return exists;
    }

    public void userEmailVerified(String email) {
        log.info("Verifying email for user: {}", email);

        Optional<UserEntity> optionalUser = userRepo.findByEmailId(email);
        if (optionalUser.isEmpty()) {
            log.warn("Email verification failed - User not found: {}", email);
            throw new ServerException("User with this email not found", 404);
        }

        UserEntity user = optionalUser.get();
        if (user.isEmailVerified()) {
            log.info("Email is already verified: {}", email);
            return;
        }

        user.setEmailVerified(true);
        userRepo.save(user);

        log.info("User email verified successfully: {}", email);
    }

    public boolean isAdminExist() {
        long adminCount = userRepo.countByUserRole(UserRole.ADMIN);
        return adminCount > 0;
    }

    public List<EditorDetailsDto> getAllEditors() {
        List<EditorDetailsDto> editorDetailsDtos = new ArrayList<>();
        for (UserEntity userEntity : userRepo.findByUserRole(UserRole.EDITORS)) {
            EditorDetailsDto editorDetailsDto = new EditorDetailsDto();
            editorDetailsDto.setName(userEntity.getFullName());
            editorDetailsDto.setUserName(userEntity.getUsername());
            editorDetailsDto.setId(userEntity.getId());
            editorDetailsDto.setEmail(userEntity.getEmailId());
            editorDetailsDtos.add(editorDetailsDto);
        }
        return editorDetailsDtos;
    }

    public Optional<UserEntity> findUserByName(String userName) {
        return userRepo.findByUsername(userName);
    }
}
