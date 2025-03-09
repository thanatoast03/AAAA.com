// ReportedMessageDTO.java
public class ReportedMessageDTO {
    private Long id;
    private String creatorUsername;  // Username of the creator of the message
    private String messageText;      // The actual message text
    private String reporterUsername; // Username of the person who reported the message
    private String reportedAt;       // The date/time when the message was reported

    // Constructor
    public ReportedMessageDTO(Long id, String creatorUsername, String messageText, String reporterUsername, String reportedAt) {
        this.id = id;
        this.creatorUsername = creatorUsername;
        this.messageText = messageText;
        this.reporterUsername = reporterUsername;
        this.reportedAt = reportedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }

    public String getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(String reportedAt) {
        this.reportedAt = reportedAt;
    }
}