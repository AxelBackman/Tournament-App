package com.pvt.demo.controller;

import java.util.List;

import com.pvt.demo.model.GameGroup;

public class ResponseGameGroupDto {
    public List<ResponseGameDto> games;

    public ResponseGameGroupDto(GameGroup group) {
        this.games = group.getGames().stream().map(ResponseGameDto::new).toList();
    }
}
