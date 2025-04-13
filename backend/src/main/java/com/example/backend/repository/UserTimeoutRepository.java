package com.example.backend.repository;

import com.example.backend.model.ActionType;
import com.example.backend.model.UserTimeout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTimeoutRepository extends JpaRepository<UserTimeout, Long> {
    Optional<UserTimeout> findByUserIdAndActionType(Long userId, ActionType actionType);
}