package com.example.backend.controller;

import com.example.backend.DTO.LoginRequest;
import com.example.backend.DTO.RegisterRequest;
import com.example.backend.service.AccountService;
import com.example.backend.service.ReCAPTCHAService;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
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
    public HashMap<String, String> registerAccount(@Validated @RequestBody RegisterRequest request, BindingResult bindingResult) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("success", "false");

        try {
            // any binding errors from field input errors/attack attempts
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }

            // password match check
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new Exception("Passwords do not match");
            }

            // reCAPTCHA verification
            if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
                throw new Exception("reCAPTCHA verification failed");
            }

            try {
                accountService.saveAccount(request);
                map.put("success", "true");
                System.out.println("Successfully registered account");
            } catch (RuntimeException e) {
                map.put("reason", e.getMessage());   
            }

        } catch (Exception e) {
            map.put("reason", e.getMessage());
        }

        return map;
    }

    @PostMapping("/login")
    public HashMap<String, String> login(@Validated @RequestBody LoginRequest request, BindingResult bindingResult) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("success", "false");

        try {
            // any binding errors from field input errors/attack attempts
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }

            // reCAPTCHA verification
            if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
                throw new Exception("reCAPTCHA verification failed");
            }

            try {
                accountService.loginAccount(request);
                map.put("success", "true");
                System.out.println("Successfully logged into account");
            } catch (RuntimeException e) {
                map.put("reason", e.getMessage());   
            }

        } catch (Exception e) { 
            map.put("reason", e.getMessage());
        }

        return map;
    }
}
