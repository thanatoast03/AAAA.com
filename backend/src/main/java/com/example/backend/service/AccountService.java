package com.example.backend.service;
import com.example.backend.DTO.LoginRequest;
import com.example.backend.DTO.RegisterRequest;
import com.example.backend.DTO.ChangeUsernameRequest;
import com.example.backend.model.Account;
import com.example.backend.model.AuthenticatedUser;
import com.example.backend.repository.AccountRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

@Service
public class AccountService implements UserDetailsService {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtilService jwtUtils;

    public AccountService(AccountRepository accountRepository, @Lazy AuthenticationManager authenticationManager, JwtUtilService jwtUtils) {
        this.accountRepository = accountRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public Account saveAccount(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Password and confirm password do not match");
        }
        if (accountRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }
        if (accountRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username taken");
        }

        Account account = new Account();
        account.setUsername(registerRequest.getUsername().toLowerCase().trim());
        account.setEmail(registerRequest.getEmail().toLowerCase().trim());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        return accountRepository.save(account);
    }

    public String loginAccount(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().toLowerCase().trim();
        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        // try to authenticate
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
        );

        // create AuthenticatedUser and set authorities
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(() -> account.getRole());
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(account, authorities);

        // use AuthenticatedUser as principal
        Authentication customAuth = new UsernamePasswordAuthenticationToken(
            authenticatedUser, 
            null, 
            authorities
        );
        SecurityContextHolder.getContext().setAuthentication(customAuth);

        // generate token with extra claims
        Map<String, String> extraClaims = new HashMap<>();
        extraClaims.put("id", account.getId().toString());
        extraClaims.put("username", account.getUsername());
        extraClaims.put("role", account.getRole());

        return jwtUtils.generateToken(extraClaims, email, 86400000);
    }

    public void changeUsername(ChangeUsernameRequest usernameRequest) throws Exception{
        String oldUsername = getLoggedInUser().getUsername(); //get old username
        String newUsername = usernameRequest.getNewUsername(); //get new username from request

        Account currentUser = accountRepository.findByUsername(oldUsername).orElseThrow(() -> new Exception("User not found"));

        if (accountRepository.existsByUsername(newUsername) || newUsername.equals(oldUsername)) {
            throw new Exception("Username is already in use.");
        }

        currentUser.setUsername(newUsername);
        accountRepository.save(currentUser);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // get authenticated user object (account and authorities)
        Account account = accountRepository.findByEmail(email) // find by email
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Set<GrantedAuthority> authorities = new HashSet<>(); // set authorities
        authorities.add(() -> account.getRole());
        
        return new AuthenticatedUser(account, authorities);
    }

    public Account getLoggedInUser() { // get account associated with user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            return ((AuthenticatedUser) authentication.getPrincipal()).getAccount();
        }
        
        return null;
    }
}
