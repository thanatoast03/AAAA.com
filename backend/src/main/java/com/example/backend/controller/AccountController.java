package com.example.backend.controller;

import com.example.backend.DTO.ChangeUsernameRequest;
import com.example.backend.DTO.ChangeEmailRequest;
import com.example.backend.DTO.LoginRequest;
import com.example.backend.DTO.RegisterRequest;
import com.example.backend.service.AccountService;
import com.example.backend.service.ReCAPTCHAService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.profiles.active}") 
    private String activeProfile;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerAccount(@Validated @RequestBody RegisterRequest request, BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new Exception("Passwords do not match");
            }

            if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
                throw new Exception("reCAPTCHA verification failed");
            }

            accountService.saveAccount(request);
            response.put("success", "true");
            response.put("message", "Registration successful");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Validated @RequestBody LoginRequest request, BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        try {
            if (bindingResult.hasErrors()) { // if it does not fit DTO object
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }

            // reCAPTCHA verification in dev mode
            if (!"dev".equals(activeProfile) && !recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
                throw new Exception("reCAPTCHA verification failed");
            }

            // get JWT token when logging in
            String token = accountService.loginAccount(request);
            
            response.put("success", "true");
            response.put("message", "Login successful");
            response.put("token", token);  // return JWT token
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/changeUsername")
    public ResponseEntity<Map<String,String>> changeUsername(@Validated @RequestBody ChangeUsernameRequest request, BindingResult bindingResult) {
        Map<String,String> response = new HashMap<>();

        try{
            if (bindingResult.hasErrors()){ //check if username request is a valid username request
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }

            String token = accountService.changeUsername(request); //pass request on to AccountService, get token on successful change

            response.put("success","true"); //create response to be returned
            response.put("message","Username successfully changed");
            response.put("token",token);

            return ResponseEntity.ok(response); //return response object
        } catch (Exception e) {
            response.put("success","false"); //bad response
            response.put("message",e.getMessage()); //contain error
            return ResponseEntity.badRequest().body(response); //return bad request describing error
        }
    }

    @PostMapping("/changeEmail")
    public ResponseEntity<Map<String,String>> changeEmail(@Validated @RequestBody ChangeEmailRequest request, BindingResult bindingResult) {
        Map<String,String> response = new HashMap<>();

        try{
            if (bindingResult.hasErrors()){ //check if username request is a valid email request
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }

            String token = accountService.changeEmail(request); //pass request on to AccountService, get token on successful change

            response.put("success","true"); //create response to be returned
            response.put("message","Email successfully changed");
            response.put("token",token);

            return ResponseEntity.ok(response); //return response object
        } catch (Exception e) {
            response.put("success","false"); //bad response
            response.put("message",e.getMessage()); //contain error
            return ResponseEntity.badRequest().body(response); //return bad request describing error
        }
    }
}
