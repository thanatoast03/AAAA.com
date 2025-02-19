package com.example.backend.service;

import java.util.HashMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReCAPTCHAService {
    @Value("${CAPTCHA_SECRET_KEY}")
    private String recaptchaSecret;

    private static class RecaptchaResponse {
        private boolean success;
        private double score;
        private String action;
        private String hostname;
        private String[] errorCodes;
        private String challenge_ts;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public String[] getErrorCodes() {
            return errorCodes;
        }

        public void setErrorCodes(String[] errorCodes) {
            this.errorCodes = errorCodes;
        }

        public String getChallenge_ts() {
            return challenge_ts;
        }

        public void setChallenge_ts(String challenge_ts) {
            this.challenge_ts = challenge_ts;
        }
    }

    public boolean verifyRecaptcha(String token) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HashMap<String, String> body = new HashMap<>(); // setup hash map
        body.put("secret", recaptchaSecret);
        body.put("response", token);
        
        String requestJson = new JSONObject(body).toString(); // turn into JSON
                    
        HttpEntity<String> entity = new HttpEntity<String>(requestJson, headers);
        RecaptchaResponse response = restTemplate.postForObject(url, entity, RecaptchaResponse.class); // send to verify

        return response != null && response.isSuccess() && response.getScore() >= 0.5;
    }



}
