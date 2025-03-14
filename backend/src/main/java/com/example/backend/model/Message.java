package com.example.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // cant have empty message
    @Size(min = 1, max = 2000)
    private String text;

    @ManyToOne
    @JoinColumn(nullable = false, name = "sender_id") // foreign key sender links to the Account primary id
    private Account sender; // i think get this from the token

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportedMessage> reportedMessages = new ArrayList<>();

    @Column(nullable = false)
    private String time;

    @PositiveOrZero
    private int numReported; // should be initialized to 0

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNumReported() {
        return numReported;
    }

    public void setNumReported(int numReported) {
        this.numReported = numReported;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public List<ReportedMessage> getReportedMessages() {
        return reportedMessages;
    }

    public void addReportedMessage(ReportedMessage reportedMessage) {
        reportedMessages.add(reportedMessage);
        reportedMessage.setMessage(this);
    }

    public void removeReportedMessage(ReportedMessage reportedMessage) {
        reportedMessages.remove(reportedMessage);
        reportedMessage.setMessage(null);
    }
}
