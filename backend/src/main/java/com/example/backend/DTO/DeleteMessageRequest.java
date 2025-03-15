package com.example.backend.DTO;

import org.springframework.lang.NonNull;

public class DeleteMessageRequest {
    @NonNull
    private Long id;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }
}
