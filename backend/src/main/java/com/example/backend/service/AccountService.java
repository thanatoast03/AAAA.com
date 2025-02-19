package com.example.backend.service;
import com.example.backend.DTO.RegisterRequest;
import com.example.backend.model.Account;
import com.example.backend.repository.AccountRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private PasswordEncoder passwordEncoder;

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account saveAccount(RegisterRequest registerRequest) {
        // check if email/username already exists
        if (accountRepository.existsByEmail(registerRequest.getEmail())) { throw new RuntimeException("Email already registered!"); }
        if (accountRepository.existsByUsername(registerRequest.getUsername())) { throw new RuntimeException("Username taken"); }

        Account account = new Account();
        account.setUsername(registerRequest.getUsername().toLowerCase().trim());
        account.setEmail(registerRequest.getEmail().toLowerCase().trim());
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        return accountRepository.save(account); // save to DB
    }
}
