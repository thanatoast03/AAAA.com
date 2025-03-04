package com.example.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reportedMessages")
public class ReportedMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private Account reporter;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Account creator;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Account getCreator() {
        return creator;
    }

    public void setCreator(Account creator) {
        this.creator = creator;
    }

    public Account getReporter() {
        return reporter;
    }

    public void setReporter(Account reporter) {
        this.reporter = reporter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
