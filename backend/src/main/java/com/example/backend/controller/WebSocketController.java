package com.example.backend.controller;

import com.example.backend.DTO.MessageRequest;
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
import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JwtUtilService jwtUtil;

    @MessageMapping("/message")
    @SendTo("/topic/chat")
    public Map<String, Object> sendMessage(@Validated @Payload MessageRequest message, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        try {
            // check jwt token
            if (jwtUtil.isExpired(message.getToken())){
                throw new Exception("expired json token");
            }

            Map<String, String> savedMessage = messageService.handleMessage(message, principal);
            response.put("action", message.getAction()); // if it got here, that means that it was a valid action
            response.put("success", "true");
            response.put("message", savedMessage);
        } catch (Exception exception) {
            exception.printStackTrace(); //! don't want to send an error back because it will send to EVERYONE connected
            response.put("success", "false"); //! kinda have to; just dont render if success false
        }

        return response;
    }

    @MessageExceptionHandler // goes here on failed DTO bind
    public void handleException(Exception exception, Principal principal) {
        System.out.println(principal + " caused exception: " + exception.getMessage());
    }
}
