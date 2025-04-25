package com.inspire17.ythelper.dto;

import com.inspire17.ythelper.validation.annotations.FullName;
import com.inspire17.ythelper.validation.annotations.Username;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 to 20 characters")
    @FullName
    private String fullName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 to 20 characters")
    @Username
    private String username;

    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email format is invalid"
    )
    @Email
    private String emailId;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{7,15}$", message = "Mobile number is invalid")
    private String mobileNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase, one lowercase, one number, and one special character"
    )
    private String password;

    private UserRole userRole;

    private boolean isUserApproved;

    private AccountStatus accountStatus;
}
