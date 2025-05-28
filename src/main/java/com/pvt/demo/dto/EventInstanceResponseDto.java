package com.pvt.demo.dto;

import java.time.LocalDateTime;

public class EventInstanceResponseDto {
    public Long id;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public String title;
    public String description;
    public String location;
    public int teamSize;
    public String imageUrl;
    public Long parentEventId;
    public Long  tournamentId;

    public EventInstanceResponseDto() {
        
    }
}
