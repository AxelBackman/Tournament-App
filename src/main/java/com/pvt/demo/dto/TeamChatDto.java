package com.pvt.demo.dto;

import java.time.LocalDateTime;

public class TeamChatDto {

    public Long id;
    public String message;
    public LocalDateTime timestamp;
    public Long senderId;
    public String senderName;

    public TeamChatDto() {

    }
}
