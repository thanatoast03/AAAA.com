package com.example.backend.repository;

import com.example.backend.model.UserActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
    int countByUserIdAndActionTypeAndCreatedAtAfter(Long userId, String actionType, LocalDateTime afterTime);
}