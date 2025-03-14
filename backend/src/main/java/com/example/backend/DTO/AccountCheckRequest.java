package com.example.backend.DTO;

import jakarta.validation.constraints.*;

public class AccountCheckRequest {
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,32}$", 
            message = "Username must be 3-32 characters and contain only letters, numbers, underscores, and hyphens")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
