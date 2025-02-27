package com.example.backend.controller;

import com.example.backend.model.Account;
import com.example.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/verify")
public class JWTCheckController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> verifyToken() {
        // ? due to the JWT auth filter, should pass if JWT sent along with request
        //* send useful fields
        Account account = accountService.getLoggedInUser();
        Map<String, String> response = new HashMap<>();
        response.put("id", account.getId().toString());
        response.put("username", account.getUsername());
        return ResponseEntity.ok(response);
    }
}
