package com.pvt.demo.dto;

import java.time.LocalDateTime;

public class TournamentDto {

    public Long eventInstanceId;
    public int maxParticipants;
    public String name;
    public String gameName;
    public LocalDateTime startTime;


    public TournamentDto() {}

    public TournamentDto(Long eventInstanceId) {
        this.eventInstanceId = eventInstanceId;
    }
}
