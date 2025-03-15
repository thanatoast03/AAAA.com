package com.example.backend.DTO;

import jakarta.validation.constraints.NotBlank;

public class OnlineNotification {
    @NotBlank
    private String action;

    @NotBlank
    private String token;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
