package com.pvt.demo.controller;

import com.pvt.demo.model.Match;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.OneTimeEvent;
import com.pvt.demo.services.BracketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class BracketController {
    private final BracketService bracketService;

    public BracketController(BracketService bracketService) {
        this.bracketService = bracketService;
    }

    @GetMapping("/brackets/test")
    public List<Match> testBracket() {
        
        Team team1 = new Team();
        team1.setName("Team A");
        Team team2 = new Team();
        team2.setName("Team B");
        Team team3 = new Team();
        team3.setName("Team C");
        Team team4 = new Team();
        team4.setName("Team D");

        List<Team> teams = List.of(team1, team2, team3, team4);

        
        OneTimeEvent event = new OneTimeEvent("Test Tournament", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "Stockholm", 2);
        event.setTeams(new java.util.ArrayList<>()); 

        
        return bracketService.generateBracket(teams, event);
    }
}