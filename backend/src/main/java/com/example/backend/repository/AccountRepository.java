package com.example.backend.repository;

import com.example.backend.model.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    Optional<Account> findByEmail(String email);
    @NonNull Optional<Account> findById(@NonNull Long id);
    @NonNull Optional<Account> findByUsername(@NonNull String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsById(@NonNull Long id);
    // todo: probably more like updates
}
