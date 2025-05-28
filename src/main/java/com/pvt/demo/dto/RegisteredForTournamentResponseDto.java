package com.pvt.demo.dto;

public class RegisteredForTournamentResponseDto {
    private Long id;
    private Long tournamentId;
    private Long userId;
    private String userName;

    public RegisteredForTournamentResponseDto(Long id, Long tournamentId, Long userId, String userName) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

}
