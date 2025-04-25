package com.inspire17.ythelper.controllers;


import com.google.gson.JsonObject;
import com.inspire17.ythelper.dto.*;
import com.inspire17.ythelper.entity.UserEntity;
import com.inspire17.ythelper.helper.Helper;
import com.inspire17.ythelper.sec.JwtUtil;
import com.inspire17.ythelper.service.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller responsible for authentication and user management endpoints.
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@Validated
public class AuthController {

    @Autowired
    private AuthenticationService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordService passwordService;

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshToken The refresh token used to generate a new access token.
     * @return A new access token if the refresh token is valid, otherwise returns an error response.
     */
    @PostMapping("/refresh_token")
    public ResponseEntity<?> refresh_token(@RequestBody String refreshToken) {
        log.info("Received request to refresh token");

        String newAccessToken = jwtUtil.refreshToken(refreshToken);

        if (newAccessToken == null) {
            log.warn("Invalid refresh token received");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        log.info("Successfully refreshed token");
        return ResponseEntity.ok(newAccessToken);
    }

    /**
     * Handles Initial signup and registration.
     *
     * @param signupRequest The user details for registration.
     * @return A success message upon successful registration.
     */
    @PostMapping("/isignup")
    public ResponseEntity<?> initialSignup(@Valid @RequestBody SignupRequestDto signupRequest) throws MessagingException {
        log.info("Received signup request for username: {}", signupRequest.getUsername());

        signupRequest.setUserRole(UserRole.ADMIN);
        signupRequest.setAccountStatus(AccountStatus.ACTIVE);
        signupRequest.setUserApproved(true);
        jwtService.isignup(signupRequest);
        String token = tokenService.generateVerificationToken(signupRequest.getEmailId());
        emailService.sendVerificationEmail(signupRequest.getEmailId(), token);
        log.info("User registered successfully: {}", signupRequest.getUsername());

        return ResponseEntity.status(201).body("User registered successfully");
    }

    /**
     * Handles user signup and registration.
     *
     * @param signupRequest The user details for registration.
     * @return A success message upon successful registration.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        log.info("Received signup request for username: {}", signupRequest.getUsername());
        if (signupRequest.getUserRole() != null && signupRequest.getUserRole().getRole().equals("ADMIN")) {
            return ResponseEntity.status(400).body("Sign up failed, Cannot signup as admin, Contact Admin");
        }

        if (signupRequest.getUserRole() == null) {
            signupRequest.setUserRole(UserRole.USERS);
        }

        signupRequest.setAccountStatus(AccountStatus.PENDING);
        signupRequest.setUserApproved(false);
        jwtService.signup(signupRequest);
        String token = tokenService.generateVerificationToken(signupRequest.getEmailId());
        emailService.sendVerificationEmail(signupRequest.getEmailId(), token);
        emailService.notifyAdminsForApproval(signupRequest);
        log.info("User registered successfully: {}", signupRequest.getUsername());

        return ResponseEntity.status(201).body("User registered successfully");
    }

    /**
     * Checks whether a given username is available.
     *
     * @param q The text to be checked for available username.
     * @return A response indicating whether the username exists or is available.
     */
    @GetMapping("/username_check")
    public ResponseEntity<?> usernameCheck(@RequestParam(required = true) String q) {
        JsonObject jsonObject = new JsonObject();
        if (jwtService.userNameExits(q)) {
            log.debug("Username exists: {}", q);

            jsonObject.addProperty("exists", true);
            return ResponseEntity.ok(jsonObject.toString());
        } else {
            log.debug("Username available: {}", q);

            jsonObject.addProperty("exists", false);
            return ResponseEntity.ok(jsonObject.toString());
        }
    }

    /**
     * Handles user login and returns a JWT token upon successful authentication.
     *
     * @param loginRequest The user credentials.
     * @return A JWT token if authentication is successful, otherwise an unauthorized response.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        JsonObject responseObject = new JsonObject();

        Optional<UserEntity> userEntity = jwtService.authenticate(loginRequest);

        if (userEntity.isPresent()) {
            if (!userEntity.get().isUserApproved()) {
                responseObject.addProperty("message", "User need to wait for approval. Contact admin.");
                return ResponseEntity.ok(responseObject.toString());
            }

            if (userEntity.get().isEmailVerified()) {
                UserWrapperDto userDetails = new UserWrapperDto(userEntity.get());
                String token = jwtUtil.generateToken(userDetails);
                log.info("User logged in successfully, token generated");
                responseObject.addProperty("token", token);
                return ResponseEntity.ok(responseObject.toString());
            } else {
                String token = tokenService.generateVerificationToken(userEntity.get().getEmailId());
                emailService.sendVerificationEmail(userEntity.get().getEmailId(), token);
                responseObject.addProperty("message", "Email is not verified.");
                return ResponseEntity.ok().body(responseObject.toString());
            }
        }
        responseObject.addProperty("message", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseObject.toString());
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam String token) {
        String email = tokenService.getEmailFromToken(token);
        if (email != null) {
            userDetailsService.userEmailVerified(email);
            tokenService.removeToken(token);
            return "Email " + email + " successfully verified!";
        } else {
            return "Invalid or expired token.";
        }
    }

    @PostMapping("/reset_pass")
    public ResponseEntity<?> reset_pass(@Valid @RequestBody ResetPasswordDto resetPassword) {
        JsonObject responseObject = new JsonObject();
        log.info("Received request for resetting password for email: {}", resetPassword.getEmailId());

        if (resetPassword.getEmailId() == null || resetPassword.getEmailId().isEmpty()) {
            responseObject.addProperty("message", "Invalid request.");
            return ResponseEntity.badRequest().body(responseObject.toString());
        }

        if (resetPassword.getOtp() == null || resetPassword.getOtp().isEmpty()) {
            boolean emailExist = passwordService.generateOTPIfEmailExist(resetPassword.getEmailId());
            if (emailExist) {
                responseObject.addProperty("message", "OTP generated and emailed.");
                responseObject.addProperty("status", true);
                return ResponseEntity.ok(responseObject.toString());
            } else {
                responseObject.addProperty("message", resetPassword.getEmailId() + " is not registered with us.");
                responseObject.addProperty("status", true);
                return ResponseEntity.badRequest().body(responseObject.toString());
            }

        }

        if (resetPassword.getNewPassword() != null && !resetPassword.getNewPassword().isEmpty()) {
            boolean validated = passwordService.validateAndUpdate(resetPassword);
            if (validated) {
                responseObject.addProperty("message", "Password updated.");
                responseObject.addProperty("status", true);
                return ResponseEntity.ok(responseObject.toString());
            }
        }
        responseObject.addProperty("message", "Invalid request.");
        return ResponseEntity.badRequest().body(responseObject.toString());

    }

    @GetMapping("/is_admin")
    public ResponseEntity<Map<String, Boolean>> isAdmin() {
        AccountInfoDto accountInfoDto = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        boolean isAdmin = false;
        Map<String, Boolean> response = new HashMap<>();
        if(accountInfoDto.getUserRole() !=null) {
            if (accountInfoDto.getUserRole()==UserRole.ADMIN){
                isAdmin = true;
            }
        }
        response.put("isAdmin", isAdmin);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/is_editor")
    public ResponseEntity<Map<String, Boolean>> isEditor() {
        AccountInfoDto accountInfoDto = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());
        boolean isEditor = false;
        Map<String, Boolean> response = new HashMap<>();
        if(accountInfoDto.getUserRole() !=null) {
            if (accountInfoDto.getUserRole()==UserRole.ADMIN){
                isEditor = true;
            }
        }
        response.put("isEditor", isEditor);
        return ResponseEntity.ok(response);
    }
}
