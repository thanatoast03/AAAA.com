package com.example.backend.service;

import com.example.backend.DTO.*;
import com.example.backend.model.Account;
import com.example.backend.model.AuthenticatedUser;
import com.example.backend.repository.AccountRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AccountService implements UserDetailsService {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtilService jwtUtils;
    private final RateLimiterService rateLimiterService;

    public AccountService(AccountRepository accountRepository, @Lazy AuthenticationManager authenticationManager, JwtUtilService jwtUtils, RateLimiterService rateLimiterService) {
        this.accountRepository = accountRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.rateLimiterService = rateLimiterService;
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

    public void deleteAccount(DeleteAccountRequest deleteRequest) throws Exception {
        String usernameToDelete = deleteRequest.getAccountToDelete();
        boolean requesterIsAdmin = getLoggedInUser().getRole().equals("admin");

        if(usernameToDelete.isBlank()){
            usernameToDelete = getLoggedInUser().getUsername(); //deletes logged in user if no account is specified
        } else if (!requesterIsAdmin) { // if blank and user requesting is not an admin
            throw new RuntimeException("You are not allowed to delete this account");
        }

        Account accountToDelete = accountRepository.findByUsername(usernameToDelete).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String previousToken = accountToDelete.getToken();

        boolean isDeleteAllowed =
                accountToDelete.getId().equals(getLoggedInUser().getId()) || // requester deleting own account
                !accountToDelete.getRole().equals("admin") && requesterIsAdmin; // admin deleting non-admin account
        if (!isDeleteAllowed) {
            throw new RuntimeException("You are not allowed to delete this account");
        }

        if(previousToken != null){
            jwtUtils.addToBlacklist(previousToken); //add token to blacklist
        }

        accountRepository.delete(accountToDelete); //goodbye account :NimiSobYT:
    }

    public void logoutAccount(String token) {
        if(token != null){
            jwtUtils.addToBlacklist(token);
        }

        // find account
        String email = jwtUtils.getEmail(token);
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // set token to null in DB
        account.setToken(null);
        accountRepository.save(account);
    }

    public String loginAccount(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().toLowerCase().trim();
        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // try to authenticate
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, loginRequest.getPassword())
        ); //! SUPER IMPORTANT; THROWS ERROR IF CREDENTIALS ARE INCORRECT

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
        Map<String, Map<String, String>> extraClaims = new HashMap<>();
        Map<String, String> claims = new HashMap<>();
        extraClaims.put("extraClaims", claims);
        claims.put("id", account.getId().toString());
        claims.put("username", account.getUsername());
        claims.put("role", account.getRole());

        String token = jwtUtils.generateToken(extraClaims, email, 86400000);
        replaceToken(account, token); // replaces token with new token

        return token;
    }

    public String changeUsername(ChangeUsernameRequest usernameRequest) throws Exception{
        String oldUsername = getLoggedInUser().getUsername(); //get old username
        String newUsername = usernameRequest.getNewUsername(); //get new username from request

        Account currentUser = accountRepository.findByUsername(oldUsername).orElseThrow(() -> new Exception("User not found"));

        if (accountRepository.existsByUsername(newUsername) || newUsername.equals(oldUsername)) {
            throw new Exception("Username is already in use.");
        }

        currentUser.setUsername(newUsername);
        accountRepository.save(currentUser);

        // generate token with extra claims
        Map<String, Map<String,String>> extraClaims = new HashMap<>();
        Map<String,String> claims = new HashMap<>();
        extraClaims.put("extraClaims",claims);
        claims.put("id", currentUser.getId().toString());
        claims.put("username", currentUser.getUsername());
        claims.put("role", currentUser.getRole());

        String token = jwtUtils.generateToken(extraClaims, currentUser.getEmail(), 86400000);
        replaceToken(currentUser, token); // replaces token with new token

        return token;
    }

    public String changeEmail(ChangeEmailRequest emailRequest) throws Exception{
        String oldEmail = getLoggedInUser().getEmail(); //get old email for email change
        String newEmail = emailRequest.getNewEmail(); //get new email from request

        Account currentUser = accountRepository.findByEmail(oldEmail).orElseThrow(() -> new Exception("User not found"));

        if (accountRepository.existsByEmail(newEmail) || newEmail.equals(oldEmail)) {
            throw new Exception("Email is already in use.");
        }

        currentUser.setEmail(newEmail);
        accountRepository.save(currentUser);

        // generate token with extra claims
        Map<String, Map<String,String>> extraClaims = new HashMap<>();
        Map<String,String> claims = new HashMap<>();
        extraClaims.put("extraClaims",claims);
        claims.put("id", currentUser.getId().toString());
        claims.put("username", currentUser.getUsername());
        claims.put("role", currentUser.getRole());

        String token = jwtUtils.generateToken(extraClaims, currentUser.getEmail(), 86400000);
        replaceToken(currentUser, token); // replaces token with new token

        return token;
    }

    public void changePassword(ChangePasswordRequest passwordRequest) throws Exception{
        String email = getLoggedInUser().getEmail(); //get email for password change
        String newPassword = passwordRequest.getNewPassword(); //get new password from request

        Account currentUser = accountRepository.findByEmail(email).orElseThrow(() -> new Exception("User not found"));

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(currentUser);
    }

    public boolean accountCheck(AccountCheckRequest accountCheckRequest) throws Exception {
        String username = accountCheckRequest.getUsername();
        accountRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found")); //throws error if account not found
        return true;
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

    private void replaceToken(Account account, String token) {
        String previousToken = account.getToken();
        if (previousToken != null && !jwtUtils.isExpired(previousToken)) {
            jwtUtils.addToBlacklist(previousToken); // invalidate previous token
        }
        account.setToken(token);
        accountRepository.save(account); // replaces with token in db
    }
}
