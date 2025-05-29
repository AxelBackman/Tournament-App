package com.pvt.demo.dto;

public class RegisteredForTournamentResponseDto {
    public Long id;
    public Long tournamentId;
    public Long userId;
    public String userName;
    public String status;

    public RegisteredForTournamentResponseDto(Long id, Long tournamentId, Long userId, String userName, String status) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.userName = userName;
        this.status = status;
    }

    

}
