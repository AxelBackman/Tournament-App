package com.pvt.demo.dto;

import java.time.LocalDateTime;

public class TeamChatDto {

    private Long id;
    private String message;
    private LocalDateTime timestamp;
    private Long senderId;
    private String senderName;

    public TeamChatDto() {

    }

    public TeamChatDto(Long id, String message, LocalDateTime timestamp,
    Long senderId, String senderName) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.senderName = senderName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    

}
