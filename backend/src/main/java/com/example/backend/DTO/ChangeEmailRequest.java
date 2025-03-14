package com.example.backend.DTO;

import jakarta.validation.constraints.*;

public class ChangeEmailRequest {

    @NotBlank(message = "Email required")
    @Size(max = 320, message = "Email must be less than 320 characters")
    @Email(message = "Please provide a valid email address")
    private String newEmail;

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
