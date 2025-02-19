package com.example.backend.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email required")
    @Size(max = 320, message = "Email must be less than 320 characters")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,32}$", 
            message = "Username must be 3-32 characters and contain only letters, numbers, underscores, and hyphens")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
            message = "Password must contain at least one digit, lowercase, uppercase, and special character")
    private String password;

    @NotBlank(message = "Confirm your password")
    private String confirmPassword;

    @NotBlank(message = "reCAPTCHA token is required")
    private String recaptchaToken;
}
