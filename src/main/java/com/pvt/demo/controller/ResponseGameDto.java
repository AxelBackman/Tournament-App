package com.pvt.demo.controller;

import com.pvt.demo.model.Game;

public class ResponseGameDto {
    public Long id;
    public String teamOneName;
    public String teamTwoName;
    public Long winnerTeamId;

    public ResponseGameDto(Game game) {
        this.id = game.getId();
        this.teamOneName = game.getTeamOne().getName();
        this.teamTwoName = game.getTeamTwo().getName();
        this.winnerTeamId = game.getWinner() != null ? game.getWinner().getId() : null;
    }
}
