package com.inspire17.ythelper.service;

import com.inspire17.ythelper.dto.ResetPasswordDto;
import com.inspire17.ythelper.entity.UserEntity;
import com.inspire17.ythelper.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class PasswordService {

    @Autowired
    EmailService emailService;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    public boolean generateOTPIfEmailExist(@NotBlank(message = "Email is required") @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email format is invalid"
    ) @Email String emailId) {
        Optional<UserEntity> byEmailId = userRepository.findByEmailId(
                emailId
        );
        if (byEmailId.isPresent()) {
            String otp = tokenService.generateOTP(emailId);
            emailService.sendPasswordResetOtp(emailId, otp);
            log.info("Reset password otp send for email {}", emailId);
            return true;
        }
        log.warn("Reset password failed: email {} doesn't exists", emailId);
        return false;
    }

    public boolean validateAndUpdate(@Valid ResetPasswordDto resetPassword) {
        String otp = tokenService.getOTPFromEmail(resetPassword.getEmailId());
        if (resetPassword.getOtp().equals(otp)) {
            int updatedRows = userRepository.updatePasswordByEmailId(resetPassword.getEmailId(), resetPassword.getNewPassword());
            log.info("Password updated for email {}:{}", resetPassword.getEmailId(), (updatedRows > 0));
            tokenService.removeOTP(resetPassword.getEmailId());
            return updatedRows > 0;
        }

        log.error("Password updated for email {}", resetPassword.getEmailId());
        return false;
    }
}
