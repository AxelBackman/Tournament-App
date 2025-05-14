package com.pvt.demo.util;

import com.pvt.demo.model.Match;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;

public class BracketGenerator {
    public List<Match> generateFirstRound(EventInstance event) {
        List<Team> teams = event.getTeams();
        if (teams == null || teams.size() % 2 != 0) {
            throw new IllegalArgumentException("Number of teams must be even and not null to create brackets.");
        }

        Collections.shuffle(teams);
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < teams.size(); i += 2) {
            Team team1 = teams.get(i);
            Team team2 = teams.get(i + 1);
            Match match = new Match(team1, team2, event, 1, LocalDateTime.now(), event.getLocation());
            matches.add(match);
        }
        return matches;
    }
}