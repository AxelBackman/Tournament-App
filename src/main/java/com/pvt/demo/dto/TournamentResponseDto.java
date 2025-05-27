package com.pvt.demo.dto;

public class TournamentResponseDto {
    public Long id;
    public Long eventInstanceId;
    public int teamSize;
    public int maxParticipants;

    public TournamentResponseDto(Long id, Long eventInstanceId, int teamSize, int maxParticipants) {
        this.id = id;
        this.eventInstanceId = eventInstanceId;
        this.teamSize = teamSize;
        this.maxParticipants = maxParticipants;
    }
}