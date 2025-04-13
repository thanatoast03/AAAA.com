package com.example.backend.service;

import com.example.backend.model.ActionType;
import com.example.backend.model.UserActionLog;
import com.example.backend.repository.UserActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class RateLimiterService {
    @Autowired
    private UserActionLogRepository userActionLogRepository;

    @Autowired
    private UserTimeoutService userTimeoutService;

    public boolean isRateLimited(Long userId, ActionType actionType, int limit, Duration window) {
        LocalDateTime threshold = LocalDateTime.now().minus(window);
        int count = userActionLogRepository.countByUserIdAndActionTypeAndCreatedAtAfter(userId, actionType, threshold);

        LocalDateTime timeoutUntil = userTimeoutService.getTimeoutUntil(userId, actionType);
        boolean currentlyTimedOut = timeoutUntil != null && timeoutUntil.isAfter(LocalDateTime.now());

        if (count >= limit || currentlyTimedOut) {
            ActionType rateLimitType = switch (actionType) {
                case MESSAGE -> ActionType.MESSAGE_TIMEOUT;
                case EMAIL_CHANGE -> ActionType.EMAIL_CHANGE_TIMEOUT;
                case USERNAME_CHANGE -> ActionType.USERNAME_CHANGE_TIMEOUT;
                case PASSWORD_CHANGE -> ActionType.PASSWORD_CHANGE_TIMEOUT;
                default -> null;
            };
            userTimeoutService.setTimeoutUntil(userId, actionType, Duration.ofMinutes(1));
            logAction(userId, rateLimitType);

            return true;
        }

        return false;
    }

    public void logAction(Long userId, ActionType actionType){
        UserActionLog log = new UserActionLog();
        log.setUserId(userId);
        log.setActionType(actionType);
        userActionLogRepository.save(log);
    }
}
