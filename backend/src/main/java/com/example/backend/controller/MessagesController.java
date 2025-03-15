package com.example.backend.controller;

import com.example.backend.DTO.*;
import com.example.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessagesController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/report")
    public ResponseEntity<Map<String, String>> reportMessage(@Validated @RequestBody MessageReportRequest request, BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }

            messageService.handleReportMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/history")
    public ResponseEntity<List<MessageDTO>> messageHistory(@Validated @RequestBody MessageHistoryRequest request, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }
            List<MessageDTO> response = messageService.getMessageHistory(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/reported")
    public ResponseEntity<Map<String, Object>> getReportedMessages() {
        try {
            // todo: NEEDS ADMIN CHECK
            List<ReportedMessageDTO> reportedMessages = messageService.getReportedMessages();
            Map<String, Object> response = messageService.compileReportedMessages(reportedMessages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error fetching reported messages: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/user/history")
    public ResponseEntity<List<MessageDTO>> getUserMessages(@Validated @RequestBody UserMessagesRequest request, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }
            List<MessageDTO> response = messageService.getUserMessages(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteMessage(@Validated @RequestBody DeleteMessageRequest request, BindingResult bindingResult, @RequestHeader("Authorization") String authHeader){
        Map<String, String> response = new HashMap<>();
        try {
            if (bindingResult.hasErrors()) {
                throw new Exception(bindingResult.getAllErrors().get(0).getDefaultMessage());
            }

            Long messageDeletedId = messageService.httpDeleteMessage(request, authHeader);

            response.put("success","true");
            response.put("id", messageDeletedId.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success","false");
            response.put("message",e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
    

