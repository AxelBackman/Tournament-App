package com.pvt.demo.dto;

import java.time.LocalDateTime;

public class TournamentResponseDto {
    public Long id;
    public Long eventInstanceId;
    public int teamSize;
    public int maxParticipants;
    public String name;
    public String gameName;
    public LocalDateTime startTime;
    public String location;

    public TournamentResponseDto(Long id, Long eventInstanceId, int teamSize, int maxParticipants, String name, String gameName, LocalDateTime startTime, String location) {
        this.id = id;
        this.eventInstanceId = eventInstanceId;
        this.teamSize = teamSize;
        this.maxParticipants = maxParticipants;
        this.name = name;
        this.gameName = gameName;
        this.startTime = startTime;
        this.location = location;
    }
}