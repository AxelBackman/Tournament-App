package com.pvt.demo.controller;

import com.pvt.demo.model.Game;
import com.pvt.demo.model.Team;

public class ResponseGameDto {
    public Long id;
    public String teamOneName;
    public String teamTwoName;
    public Long winnerTeamId;

    public ResponseGameDto(Game game) {
        this.id = game.getId();

        Team teamOne = game.getTeamOne();
        Team teamTwo = game.getTeamTwo();

        this.teamOneName = teamOne != null ? teamOne.getName() : null;
        this.teamTwoName = teamTwo != null ? teamTwo.getName() : null;
        this.winnerTeamId = game.getWinner() != null ? game.getWinner().getId() : null;
    }
}