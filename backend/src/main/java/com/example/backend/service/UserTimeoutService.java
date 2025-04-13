package com.example.backend.service;

import com.example.backend.model.ActionType;
import com.example.backend.model.UserTimeout;
import com.example.backend.repository.UserTimeoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserTimeoutService {
    @Autowired
    private UserTimeoutRepository timeoutRepo;

    public LocalDateTime getTimeoutUntil(Long userId, ActionType actionType) {
        return timeoutRepo.findByUserIdAndActionType(userId, actionType)
                .map(UserTimeout::getTimeoutUntil)
                .orElse(null);
    }

    public void setTimeoutUntil(Long userId, ActionType actionType, Duration extendBy) {
        LocalDateTime now = LocalDateTime.now();

        UserTimeout timeout = timeoutRepo.findByUserIdAndActionType(userId, actionType)
                .orElseGet(() -> { // if user not timed out already, add timeout in
                    UserTimeout t = new UserTimeout();
                    t.setUserId(userId);
                    t.setActionType(actionType);
                    return t;
                });

        LocalDateTime currentTimeout = timeout.getTimeoutUntil();

        // if timeout already exists that hasnt expired
        LocalDateTime newTimeout = (currentTimeout != null && currentTimeout.isAfter(now))
                ? currentTimeout.plus(extendBy) // extend an extra duration
                : now.plus(extendBy); // if it doesnt exist, just make it now + current duration

        timeout.setTimeoutUntil(newTimeout);
        timeoutRepo.save(timeout);
    }
}
