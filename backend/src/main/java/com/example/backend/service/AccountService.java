package com.example.backend.service;
import com.example.backend.DTO.LoginRequest;
import com.example.backend.DTO.RegisterRequest;
import com.example.backend.model.Account;
import com.example.backend.repository.AccountRepository;
import java.util.Optional;

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

    public void loginAccount(LoginRequest loginRequest) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(loginRequest.getEmail().toLowerCase().trim());

        // if no account found with matching email
        if (optionalAccount.isEmpty()) { throw new RuntimeException("Incorrect email/password"); }
        
        Account account = optionalAccount.get(); // if it reaches here, that means there is an account associated

        // if password does not match DB hash
        if (!passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())) { throw new RuntimeException("Incorrect email/password"); }
    }
}
