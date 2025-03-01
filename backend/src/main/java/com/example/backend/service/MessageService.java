package com.example.backend.service;

import com.example.backend.DTO.MessageRequest;
import com.example.backend.model.Account;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;
import com.example.backend.model.Message;
import org.owasp.encoder.Encode;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public Map<String, String> handleMessage(MessageRequest message, Principal principal) throws Exception {
        if (message.getAction().equals("send")) {
            return handleSendMessage(message, principal);
        } else if (message.getAction().equals("delete")) {
            return handleDeleteMessage(message, principal);
        } else if (message.getAction().equals("report")) {
            return handleReportMessage(message, principal);
        } else {
            throw new Exception("invalid message request");
        }
    }

    private Map<String, String> handleSendMessage(MessageRequest message, Principal principal) throws Exception {
        String username = principal.getName();
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm"); // format date
        String dateString = formatter.format(currentDate);

        // initialize message object logic
        Message savedMessage = new Message();

        // find account logic
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isPresent()) {
            savedMessage.setSender(account.get());
        } else {
            throw new Exception("tried to send message with invalid account");
        }

        savedMessage.setNumReported(0); // set to 0 on initialization
        savedMessage.setTime(dateString);

        // escape malicious text logic
        String text = Encode.forHtml(message.getContent());
        savedMessage.setText(text);

        messageRepository.saveAndFlush(savedMessage);

        // return logic
        // ! we will have to do something about images in the future
        Map<String, String> response = new HashMap<>();
        response.put("text", text);
        response.put("name", username);
        response.put("time", dateString);

        return response;
    }

    private Map<String, String> handleDeleteMessage(MessageRequest message, Principal principal) {
        // todo: not implemented
        return null;
    }

    private Map<String, String> handleReportMessage(MessageRequest message, Principal principal) {
        // todo: not implemented
        return null;
    }
}
