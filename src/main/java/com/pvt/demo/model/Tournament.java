package com.pvt.demo.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashMap;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int teamSize;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> allGames = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    @OneToOne(mappedBy = "tournament", cascade = CascadeType.ALL)
    private EventInstance eventInstance;

    private Map<Integer, List<Game>> bracketMap;

    public Tournament() {}

    public Tournament(EventInstance eventInstance){ // skapa olika konstruktorer för olika spel? free for all, scoreboards, eller brackets osv
        this.eventInstance = eventInstance;
        bracketMap = generateBracket();
    }

    public Map<Integer, List<Game>> generateBracket(){
        Map<Integer, List<Game>> bracketMap = new HashMap<>();
        int teamCount = teams.size();
        int gameCount = teamCount-1;

        int round = 1;
        int gamesInCurrentRound = teamCount / 2;
        int startIndex = 0;

        for(int i = 0 ; i < gameCount; i++){
            if (i >= startIndex + gamesInCurrentRound){
                startIndex += gamesInCurrentRound;
                gamesInCurrentRound /= 2;
                round++;
            }

            Game game = new Game();
            game.setTournament(this);
            game.setRound(round);
            allGames.add(game);

            bracketMap.computeIfAbsent(round, r -> new ArrayList<>()).add(game);
        }


        int parentIndex = gameCount / 2; // börjar på 4 med 8 lag
        for (int i = 0 ; i + 1 < parentIndex; i += 2){
            Game right = allGames.get(i);
            Game left = allGames.get(i+1);
            Game parent = allGames.get(parentIndex);
            
            left.setParent(parent);
            right.setParent(parent);

            parentIndex++;
        }

        return bracketMap;

    }

    public boolean getBrackets(){return brackets;}

    public List<Team> getTeams(){return teams;}
    public void setTeams (List<Team> teams){ this.teams = teams; }

    public int getTeamSize() {return teamSize; } 
    public void setTeamSize(int teamSize) {this.teamSize = teamSize; }
    
    public EventInstance getEventInstnace() { return eventInstance; }
    public void setEventInstance(EventInstance eventInstance) { this.eventInstance = eventInstance; }

    public Map<Integer, List<Game>> getBracketMap(){ return bracketMap; }
    public void setBracketMap(Map<Integer, List<Game>> bracket) { this.bracketMap = bracket;}

    public List<Game> getAllGames() { return allGames; }
    public void setAllGames(List<Game> games) { this.allGames = games; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



}
