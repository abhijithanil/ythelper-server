package com.inspire17.ythelper.service;

import com.inspire17.ythelper.dto.AuthenticationDto;
import com.inspire17.ythelper.dto.LoginRequestDto;
import com.inspire17.ythelper.dto.SignupRequestDto;
import com.inspire17.ythelper.dto.UserRole;
import com.inspire17.ythelper.entity.UserEntity;
import com.inspire17.ythelper.exceptions.ServerException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthenticationService {

    private final UserDetailsService userDetailsService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Handles user signup by encrypting password and saving user data.
     */
    @Transactional
    public void signup(SignupRequestDto signupRequest) {
        log.info("Starting user signup process for username: {}", signupRequest.getUsername());

        UserEntity user = new UserEntity();
        user.setEmailId(signupRequest.getEmailId());
        user.setEmailVerified(false);
        user.setUserApproved(signupRequest.isUserApproved());
        user.setUsername(signupRequest.getUsername());
        user.setMobileNumber(signupRequest.getMobileNumber());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUserRole(Optional.ofNullable(signupRequest.getUserRole()).orElse(UserRole.USERS));
        user.setAccountStatus(signupRequest.getAccountStatus());
        user.setFullName(signupRequest.getFullName());
        try {
            userDetailsService.saveUser(user);
            log.info("User registered successfully: {}", user.getUsername());
        } catch (ServerException e) {
            log.error("Error during user signup for username: {}", user.getUsername(), e);
            throw new ServerException(e.getMessage(), 400);
        } catch (Exception e) {
            log.error("Error during user signup for username: {}", user.getUsername(), e);
            throw new ServerException("Failed to register user", 400);
        }
    }


    /**
     * Authenticates a user by either username or email.
     */
    public Optional<UserEntity> authenticate(LoginRequestDto loginRequest) {
        if (loginRequest.getUsername() != null) {
            return authenticateUser(loginRequest.getUsername(), loginRequest.getPassword(), true);
        } else if (loginRequest.getEmailId() != null) {
            return authenticateUser(loginRequest.getEmailId(), loginRequest.getPassword(), false);
        }
        log.warn("Authentication failed: No username or email provided.");
        return Optional.empty();
    }

    /**
     * Helper method to authenticate a user using username or email.
     */
    private Optional<UserEntity> authenticateUser(String identifier, String password, boolean isUsername) {
        log.info("Authenticating user: {}", identifier);

        try {
            AuthenticationDto authentication = isUsername ? userDetailsService.authenticateUsingUserName(identifier, password, passwordEncoder) : userDetailsService.authenticateUsingEmail(identifier, password, passwordEncoder);

            if (authentication.isAuthenticated()) {
                log.info("User authenticated successfully: {}", identifier);
                return Optional.of(authentication.getUserEntity());
            }

            log.warn("Authentication unsuccessful for user: {}", identifier);
        } catch (Exception e) {
            log.warn("Authentication failed for user: {}", identifier, e);
        }
        return Optional.empty();
    }

    /**
     * Deletes a user based on their email.
     */
    public void deleteUser(@Valid String emailId) {
        userDetailsService.deleteUserWithEmail(emailId);
    }

    public boolean userNameExits(String q) {
        return userDetailsService.matchUserName(q);
    }

    public void isignup(@Valid SignupRequestDto signupRequest) {
        if (userDetailsService.isAdminExist()) {
            throw new ServerException("isignup API can be used if there are no Admins", 403);
        }
        signup(signupRequest);
    }
}
