package com.example.backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessageRequest {
    @NotBlank
    @Size(min = 1, max = 2000) // enforce character count constraint
    private String content; // can be null, if action is something like delete

    @NotBlank
    @Size(min = 4, max = 6) // these 3 are the only options:
    private String action; // send, delete, report

    @NotBlank
    private String token; // maintain JWT checking

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
