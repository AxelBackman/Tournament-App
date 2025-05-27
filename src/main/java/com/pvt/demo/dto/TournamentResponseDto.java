package com.pvt.demo.dto;

public class TournamentResponseDto {
    public Long id;
    public Long eventInstanceId;
    public int teamSize;

    public TournamentResponseDto(Long id, Long eventInstanceId, int teamSize) {
        this.id = id;
        this.eventInstanceId = eventInstanceId;
        this.teamSize = teamSize;
    }
}