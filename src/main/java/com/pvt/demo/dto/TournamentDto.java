package com.pvt.demo.dto;

public class TournamentDto {

    public Long eventInstanceId;
    public int maxParticipants;

    public TournamentDto() {}

    public TournamentDto(Long eventInstanceId) {
        this.eventInstanceId = eventInstanceId;
    }
}
