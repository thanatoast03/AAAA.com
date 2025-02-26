package com.example.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class JwtUtilService {
    private final String secretKey;
    private final SecretKey key;

    public JwtUtilService(@Value("${spring.jwt.secret_key}") String secretKey) {
        this.secretKey = secretKey;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Map<String, String> extraClaims, String email, long expireInterval) {
        return Jwts
                .builder()
                .claims()
                .add(extraClaims)
                .and()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireInterval))
                .signWith(key)
                .compact();
    }

    public String getEmail(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean isExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isValid(String token, UserDetails userDetails) { // check if token is valid
        try {
            String email = getEmail(token);
            Claims claims = extractAllClaims(token);
            
            return (email != null && // check if email is not null in token
                    email.equals(userDetails.getUsername()) && // check if user account aligns with email
                    !claims.getExpiration().before(new Date())); // check if claims are expired
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, String> extractClaims(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("extraClaims", Map.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
