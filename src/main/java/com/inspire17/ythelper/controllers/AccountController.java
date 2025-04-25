package com.inspire17.ythelper.controllers;

import com.inspire17.ythelper.dto.*;
import com.inspire17.ythelper.entity.UserEntity;
import com.inspire17.ythelper.exceptions.ServerException;
import com.inspire17.ythelper.helper.Helper;
import com.inspire17.ythelper.repository.UserRepository;
import com.inspire17.ythelper.service.EmailService;
import com.inspire17.ythelper.service.TokenService;
import com.inspire17.ythelper.service.UserDetailsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/updateAccount")
    public ResponseEntity<?> updateAccount(@Valid @RequestBody UserUpdateRequestDto updateRequest) {

        boolean isEmailUpdated = false;
        boolean isPasswordUpdated = false;
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());

        // Find the user to be updated
        UserWrapperDto userWrapper = userDetailsService.loadUserByUsername(updateRequest.getUsername());

        if (userWrapper == null) {
            throw new ServerException("User not found", 404);
        }

        UserEntity userToUpdate = userWrapper.getUser();

        if (accountInfo.getUserRole() == UserRole.USERS || accountInfo.getUserRole() == UserRole.EDITORS) {
            if (!accountInfo.getName().equals(userToUpdate.getUsername())) {
                throw new ServerException("You can only update your own profile", 403);
            }

            // Password change requires current password verification
            if (updateRequest.getNewPassword() != null && !updateRequest.getNewPassword().trim().isEmpty()) {
                if (updateRequest.getCurrentPassword() == null || !passwordEncoder.matches(updateRequest.getCurrentPassword(), userToUpdate.getPassword())) {
                    throw new ServerException("Current password is incorrect", 403);
                }

                userToUpdate.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
                isPasswordUpdated = true;
            }

            // Email change requires verification
            if (updateRequest.getEmailId() != null && !updateRequest.getEmailId().trim().isEmpty()
                    && !userToUpdate.getEmailId().equals(updateRequest.getEmailId())) {
                isEmailUpdated = true;
                String verificationToken = tokenService.generateVerificationToken(updateRequest.getEmailId());
                emailService.sendVerificationEmail(updateRequest.getEmailId(), verificationToken);
                userToUpdate.setEmailVerified(false);
            }

            // Update mobile number if present
            if (updateRequest.getMobileNumber() != null && !updateRequest.getMobileNumber().trim().isEmpty()) {
                userToUpdate.setMobileNumber(updateRequest.getMobileNumber());
            }

            // Update full name number if present
            if (updateRequest.getFullName() != null && !updateRequest.getFullName().trim().isEmpty()) {
                userToUpdate.setFullName(updateRequest.getFullName());
            }
        }

        // If ADMIN, they can update any user & any field
        else if (accountInfo.getUserRole() == UserRole.ADMIN) {
            // Admin can update email, which requires verification
            if (updateRequest.getEmailId() != null && !updateRequest.getEmailId().trim().isEmpty()
                    && !userToUpdate.getEmailId().equals(updateRequest.getEmailId())) {
                isEmailUpdated = true;
                String verificationToken = tokenService.generateVerificationToken(updateRequest.getEmailId());
                emailService.sendVerificationEmail(updateRequest.getEmailId(), verificationToken);
                userToUpdate.setEmailVerified(false);
            }

            // Admin can update password without current password verification
            if (updateRequest.getNewPassword() != null && !updateRequest.getNewPassword().trim().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
                isPasswordUpdated = true;
            }

            // Admin can update mobile number if provided
            if (updateRequest.getMobileNumber() != null && !updateRequest.getMobileNumber().trim().isEmpty()) {
                userToUpdate.setMobileNumber(updateRequest.getMobileNumber());
            }

            // Admin can update user role
            if (updateRequest.getUserRole() != null) {
                userToUpdate.setUserRole(updateRequest.getUserRole());
            }

            // Admin can update account status
            if (updateRequest.getAccountStatus() != null) {
                userToUpdate.setAccountStatus(updateRequest.getAccountStatus());
            }

            // Update full name number if present
            if (updateRequest.getFullName() != null && !updateRequest.getFullName().trim().isEmpty()) {
                userToUpdate.setFullName(updateRequest.getFullName());
            }

            // Admin can update approval status
            userToUpdate.setUserApproved(updateRequest.isUserApproved());
        }

        userDetailsService.updateUser(userToUpdate);

        return isPasswordUpdated
                ? ResponseEntity.ok("User updated successfully. Password updated.")
                : isEmailUpdated
                ? ResponseEntity.ok("User updated successfully. Please verify your new email.")
                : ResponseEntity.ok("User updated successfully.");
    }

    /**
     * Approves a user when an admin clicks the approval link.
     *
     * @param email The email ID of the user to be approved.
     * @return Response message.
     */
    @GetMapping("/approve-user")
    public ResponseEntity<?> approveUser(@RequestParam String email) {
        final AccountInfoDto accountInfo = Helper.accountInfo(SecurityContextHolder.getContext().getAuthentication());

        // Ensure user is logged in
        if (accountInfo.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized: Admin must log in to approve users.");
        }


        // Check if the authenticated user is an admin
        Optional<UserEntity> adminOptional = userRepository.findByUsername(accountInfo.getName());
        if (adminOptional.isEmpty() || adminOptional.get().getUserRole().getRole().endsWith(UserRole.ADMIN.getRole())) {
            return ResponseEntity.status(403).body("Forbidden: Only admins can approve users.");
        }

        // Find the user to approve
        Optional<UserEntity> userOptional = userRepository.findByEmailId(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        UserEntity user = userOptional.get();

        // Prevent approving already approved users
        if (user.isUserApproved()) {
            return ResponseEntity.ok("User is already approved.");
        }

        // Approve the user
        user.setUserApproved(true);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        // Send approval email asynchronously
        emailService.sendUserApprovalEmail(user);

        log.info("Admin {} approved user {}", accountInfo.getName(), user.getUsername());
        return ResponseEntity.ok("User approved successfully.");
    }


}

