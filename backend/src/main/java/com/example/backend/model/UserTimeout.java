package com.example.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_timeouts", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "actionType"}))
public class UserTimeout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ActionType actionType;

    private LocalDateTime timeoutUntil;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public LocalDateTime getTimeoutUntil() {
        return timeoutUntil;
    }

    public void setTimeoutUntil(LocalDateTime timeoutUntil) {
        this.timeoutUntil = timeoutUntil;
    }
}