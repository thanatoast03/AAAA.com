package com.example.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// get username, return list of all messages sent by user
public class UserMessagesRequest {
    @NotBlank
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
