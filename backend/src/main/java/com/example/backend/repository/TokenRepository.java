package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.BlacklistedToken;

@Repository
public interface TokenRepository extends JpaRepository<BlacklistedToken,Long> {
    boolean existsByToken(String token);
}