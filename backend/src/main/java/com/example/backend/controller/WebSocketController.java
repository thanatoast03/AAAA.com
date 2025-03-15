package com.example.backend.controller;

import com.example.backend.DTO.MessageRequest;
import com.example.backend.DTO.OnlineNotification;
import com.example.backend.service.JwtUtilService;
import com.example.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;
import java.util.*;

@Controller
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtilService jwtUtil;

    @MessageMapping("/message")
    @SendTo("/topic/chat")
    public Map<String, Object> handleMessage(@Validated @Payload MessageRequest message, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        try {
            // check jwt token
            if (jwtUtil.isExpired(message.getToken())){
                throw new Exception("expired json token");
            }

            Map<String, String> savedMessage = messageService.handleMessage(message, principal);

            if (savedMessage.isEmpty()) {
                throw new Exception("failed to execute message request");
            }

            response.put("action", message.getAction()); // if it got here, that means that it was a valid action
            response.put("success", "true");
            response.put("message", savedMessage);

            return response;
        } catch (Exception exception) {
            //! don't want to send an error back because it will send to EVERYONE connected
            throw new RuntimeException("failed to execute message request");
        }
    }

    @MessageMapping("/online")
    @SendTo("/topic/online")
    public Map<String, String> handleOnline(@Validated @Payload OnlineNotification onlineNotification, Principal principal) {
        Map<String, String> response = new HashMap<>();

        // notifies users of every existing user connected to the websocket
        try {
            // check jwt token
            if (jwtUtil.isExpired(onlineNotification.getToken())){
                throw new Exception("expired json token");
            }

            Set<String> allowedActions = new HashSet<>(Arrays.asList("join", "leave", "exists"));
            if (!allowedActions.contains(onlineNotification.getAction())) {
                throw new Exception("invalid action");
            }

            response.put("action", onlineNotification.getAction());
            response.put("username", jwtUtil.extractClaims(onlineNotification.getToken()).get("username"));

            return response;
        } catch (Exception exception) {
            throw new RuntimeException("failed to execute online notification");
        }
    }

    @MessageExceptionHandler // goes here on failed DTO bind
    public void handleException(Exception exception, Principal principal) {
        System.out.println(principal + " caused exception: " + exception.getMessage());
    }
}
