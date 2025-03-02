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
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public Map<String, String> handleMessage(MessageRequest message, Principal principal) throws Exception {
        switch (message.getAction()) {
            case "send":
                return handleSendMessage(message, principal);
            case "delete":
                return handleDeleteMessage(message, principal);
            case "report":
                return handleReportMessage(message, principal);
            default:
                throw new Exception("Invalid message request");
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

        Message dbMessage = messageRepository.saveAndFlush(savedMessage);

        // return logic
        // ! we will have to do something about images in the future
        Map<String, String> response = new HashMap<>();
        response.put("text", text);
        response.put("name", username);
        response.put("time", dateString);
        response.put("id", dbMessage.getId().toString());

        return response;
    }

    private Map<String, String> handleDeleteMessage(MessageRequest message, Principal principal) {
        Map<String, String> response = new HashMap<>();

        try {
            Long id = Long.parseLong(message.getContent());
            System.out.println("succeeded in parsing id");

            // essentially, if person requesting the deletion is the same person who created message, they can delete
            accountRepository.findByUsername(principal.getName()) // search for principal in account DB
                .ifPresent(account -> { // if principal found
                    System.out.println("found account of requester");
                    messageRepository.findById(id).ifPresent(m -> { // find message by ID
                        System.out.println("found message by id");
                        if (m.getSender().getId().equals(account.getId())) { // if the sender is the person who created the message
                            System.out.println("the sender was the same as the requester");
                            Long idDeleted = m.getId();
                            messageRepository.delete(m); // they can delete the message
                            System.out.println("successfully deleted message");
                            response.put("id", idDeleted.toString()); //! let clients know which was deleted
                        }
                    });
                }
            );

        } catch (NumberFormatException e) {
            System.err.println("someone tried putting a non-id for deleting a message");
        }

        return response;
    }

    private Map<String, String> handleReportMessage(MessageRequest message, Principal principal) {
        // todo: not implemented
        return null;
    }
}
