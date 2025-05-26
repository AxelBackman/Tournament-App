package com.pvt.demo.dto;

import com.pvt.demo.model.Game;
import com.pvt.demo.model.Team;

public class ResponseGameDto {
    public Long id;
    public Long teamOneId;
    public Long teamTwoId;
    public String teamOneName;
    public String teamTwoName;
    public Long winnerTeamId;

    public ResponseGameDto(Game game) {
        this.id = game.getId();

        this.teamOneId = game.getTeamOne() != null ? game.getTeamOne().getId() : null;
        this.teamTwoId = game.getTeamTwo() != null ? game.getTeamTwo().getId() : null;

        Team teamOne = game.getTeamOne();
        Team teamTwo = game.getTeamTwo();

        this.teamOneName = teamOne != null ? teamOne.getName() : null;
        this.teamTwoName = teamTwo != null ? teamTwo.getName() : null;
        this.winnerTeamId = game.getWinner() != null ? game.getWinner().getId() : null;
    }
}