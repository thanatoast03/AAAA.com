package com.example.backend.DTO;

import jakarta.validation.constraints.*;

public class ChangeUsernameRequest {

    @NotBlank(message = "New username is required") //ensures a new username has been entered
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,32}$", 
            message = "Username must be 3-32 characters and contain only letters, numbers, underscores, and hyphens") //ensures everything in the message (i aint typing allat)
    private String newUsername;

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
