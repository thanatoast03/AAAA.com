package com.example.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@Controller
public class WebSocketController {

    @MessageMapping("/message")
    @SendTo("/topic/chat")
    public Map<String, Object> sendMessage(@Payload Map<String, Object> message, Principal principal) {
        String username = principal.getName();
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String dateString = formatter.format(currentDate);

        // ! we will have to do something about images in the future
        Map<String, Object> response = new HashMap<>();
        response.put("text", message.get("content"));
        response.put("name", username);
        response.put("time", dateString);

        // ! needs some database logic later

        return response;
    }
}
