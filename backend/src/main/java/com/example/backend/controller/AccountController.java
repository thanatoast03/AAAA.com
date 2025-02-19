package com.example.backend.controller;

import com.example.backend.DTO.RegisterRequest;
import com.example.backend.service.AccountService;
import com.example.backend.service.ReCAPTCHAService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ReCAPTCHAService recaptchaService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@Validated @RequestBody RegisterRequest request, BindingResult bindingResult) {

        // any binding errors from field input errors/attack attempts
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                .body(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        // password match check
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        // reCAPTCHA verification
        if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
            return ResponseEntity.badRequest().body("reCAPTCHA verification failed");
        }

        try {
            accountService.saveAccount(request);
            return ResponseEntity.ok("Registration successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
