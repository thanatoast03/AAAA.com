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

    public boolean isRateLimited(Long userId, ActionType actionType, int limit, Duration window) {
        LocalDateTime threshold = LocalDateTime.now().minus(window);
        int count = userActionLogRepository.countByUserIdAndActionTypeAndCreatedAtAfter(userId, actionType, threshold);
        return count >= limit;
    }

    public void logAction(Long userId, ActionType actionType){
        UserActionLog log = new UserActionLog();
        log.setUserId(userId);
        log.setActionType(actionType);
        userActionLogRepository.save(log);
    }
}
