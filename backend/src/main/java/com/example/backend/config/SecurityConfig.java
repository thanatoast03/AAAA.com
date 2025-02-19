package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    
    @Value("${spring.profiles.active}") // Get active profile
    private String activeProfile;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if ("dev".equals(activeProfile)) {
            // disable security in dev mode
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Allow all
                    .build();
        } else {
            // enable security in production
            return http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/", "/register", "/login").permitAll() // Allow landing, register, and login without auth
                            .anyRequest().authenticated() // Everything else requires authentication
                    )
                    .build();
        }
    }
}
