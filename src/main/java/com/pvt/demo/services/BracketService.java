package com.pvt.demo.services;

import com.pvt.demo.model.Match;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.repository.MatchRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class BracketService {
    private final MatchRepository matchRepository;

    public BracketService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> generateBracket(List<Team> teams, EventInstance event) {
        
        if (teams == null || teams.size() < 2) {
            throw new IllegalArgumentException("Teams must not be null and must contain at least two teams.");
        }
        if (teams.size() % 2 != 0) {
            throw new IllegalArgumentException("Number of teams must be even to generate a bracket.");
        }
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null.");
        }

        List<Match> matches = new ArrayList<>();
        int round = 1;

        
        for (int i = 0; i < teams.size(); i += 2) {
            Match match = new Match(
                teams.get(i),
                teams.get(i + 1),
                event,
                round,
                LocalDateTime.now(),
                "Stockholm"
            );
            matches.add(match);
        }

        
        return matchRepository.saveAll(matches);
    }
}