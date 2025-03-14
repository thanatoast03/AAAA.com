package com.example.backend.service;

import com.example.backend.DTO.*;
import com.example.backend.model.Account;
import com.example.backend.model.Message;
import com.example.backend.model.ReportedMessage;
import com.example.backend.repository.AccountRepository;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.ReportedMessageRepository;
import org.owasp.encoder.Encode;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    private final ReportedMessageRepository reportedMessageRepository;
    private final AccountService accountService;

    public MessageService(AccountService accountService, MessageRepository messageRepository, AccountRepository accountRepository, ReportedMessageRepository reportedMessageRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
        this.reportedMessageRepository = reportedMessageRepository;
        this.accountService = accountService;
    }

    public Map<String, String> handleMessage(MessageRequest message, Principal principal) throws Exception {
        return switch (message.getAction()) {
            case "send" -> handleSendMessage(message, principal);
            case "delete" -> handleDeleteMessage(message, principal);
            default -> throw new Exception("Invalid message request");
        };
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
                    messageRepository.findById(id).ifPresent(m -> { // find message by ID
                        if (m.getSender().getId().equals(account.getId()) || account.getRole().equals("admin")) { // if the sender is the person who created the message OR admin
                            System.out.println("role of requester: " + account.getRole());
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

    public void handleReportMessage(MessageReportRequest message) {
        messageRepository.findById(message.getMessageId()).ifPresentOrElse(m -> { // get message by id
            Account loggedInUser = accountService.getLoggedInUser(); // get logged in user (won't get here if they are not logged in)
            if (m.getSender().getId().equals(loggedInUser.getId())) {
                throw new RuntimeException("can't report your own message");
            } // if it got this far, sender different from reporter

            // check if they have already reported
            if (reportedMessageRepository.existsByReporterAndMessage(loggedInUser, m)) {
                throw new RuntimeException("message has already been reported");
            }

            ReportedMessage reportedMessage = new ReportedMessage();
            reportedMessage.setMessage(m);
            reportedMessage.setReporter(loggedInUser);
            reportedMessage.setCreator(m.getSender());

            reportedMessageRepository.saveAndFlush(reportedMessage);
            m.setNumReported(m.getNumReported() + 1);// incrementing number of reports by 1
            messageRepository.save(m);
        }, () -> { // if it never found the message by id, throw error
            throw new RuntimeException("couldn't find message with that ID");
        });
    }

    public List<MessageDTO> getMessageHistory(MessageHistoryRequest messageHistoryRequest) {
        List<MessageDTO> messages;
        // get the top 100 messages before the id
        System.out.println("message id: " + messageHistoryRequest.getMessage_id());
        if (messageHistoryRequest.getMessage_id() == null) { // if first request, give last 100 messages
            messages = messageRepository.findLast100Messages(PageRequest.of(0, 100));
        } else {
            messages = messageRepository.findTop100BeforeMessageId(messageHistoryRequest.getMessage_id(), PageRequest.of(0, 100));
        }

        Collections.reverse(messages); // preserve ordering w/ new message logic
        return messages;
    }

    public List<ReportedMessageDTO> getReportedMessages() {
        List<ReportedMessage> reportedMessages = reportedMessageRepository.findAll(); //Get all reported messages
        return reportedMessages.stream().map(report -> {
            String creatorUsername = report.getMessage().getSender().getUsername();
            String reporterUsername = report.getCreator().getUsername();
            String messageText = report.getMessage().getText();
            String reportedAt = report.getReportedAt().toString();
            Long messageId = report.getMessage().getId();
            return new ReportedMessageDTO(report.getId(), messageId, creatorUsername, messageText, reporterUsername, reportedAt);
        }).collect(Collectors.toList());
    }

    public Map<String, Object> compileReportedMessages(List<ReportedMessageDTO> reportedMessages) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Integer> reportedUsers = new HashMap<>();
        Map<Long, Integer> reportedMessageOccurrences = new HashMap<>();

        // count unique usernames and add them to map
        for (ReportedMessageDTO reportedMessage : reportedMessages) {
            String username = reportedMessage.getCreatorUsername();
            reportedUsers.put(username, reportedUsers.getOrDefault(username, 0) + 1);

            Long messageId = reportedMessage.getMessageId();
            reportedMessageOccurrences.put(messageId, reportedMessageOccurrences.getOrDefault(messageId, 0) + 1);
        }

        response.put("reported_messages", reportedMessages);
        response.put("reported_users", reportedUsers);
        response.put("reported_message_occurrences", reportedMessageOccurrences);

        return response;
    }

    public List<MessageDTO> getUserMessages(UserMessagesRequest messagesRequest) {
        Account account = accountRepository.findByUsername(messagesRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("couldn't find user with that username"));
        return messageRepository.findAllBySender(account);
    }
}

