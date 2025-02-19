package com.example.backend.service;
import com.example.backend.DTO.RegisterRequest;
import com.example.backend.model.Account;
import com.example.backend.repository.AccountRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account saveAccount(RegisterRequest registerRequest) {
        // check if confirm password is same as password
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())){ throw new RuntimeException("Password and confirm password do not match"); }

        // check if email/username already exists
        if (accountRepository.existsByEmail(registerRequest.getEmail())) { throw new RuntimeException("Email already registered!"); }
        if (accountRepository.existsByUsername(registerRequest.getUsername())) { throw new RuntimeException("Username taken"); }

        Account account = new Account();
        account.setUsername(registerRequest.getUsername().toLowerCase().trim());
        account.setEmail(registerRequest.getEmail().toLowerCase().trim());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        return accountRepository.save(account); // save to DB
    }
}
