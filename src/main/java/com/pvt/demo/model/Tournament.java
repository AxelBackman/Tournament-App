package com.pvt.demo.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.ArrayList;
import java.util.Collections;

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

    private boolean created = false;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> allGames = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    // Tar inte bort eventinstance när turneringen tas bort
    @OneToOne(mappedBy = "tournament")
    @JsonBackReference
    private EventInstance eventInstance;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameGroup> map = new ArrayList<>(); // är en egen gjord map
    
    public Tournament() {}

    public Tournament(EventInstance eventInstance, int teamSize){ // skapa olika konstruktorer för olika spel? free for all, scoreboards, eller brackets osv
        this.eventInstance = eventInstance;
        this.teamSize = teamSize;
        created = true;

        if (eventInstance != null) {
            eventInstance.setTournament(this);
        }
    }

    public Tournament(EventInstance eventInstance, int teamSize, String grejer){ // samma konstruktor som ovan men särskiljer sig för mickes chat tjofräs
        this.eventInstance = eventInstance;
        this.teamSize = teamSize;
        created = true;

        if ( eventInstance != null && eventInstance.getUsers() != null ) {
            eventInstance.setTournament(this);
            this.setTeams();
            this.generateBracket();
        }


    }

    public void generateBracket() { //hårdkodad single elimination - måste vara lag av 4 potens
        int teamCount = teams.size();
        int totalRounds = (int) (Math.log(teamCount) / Math.log(2)); // logaritm, 8 lag -> 3 rundor

        // Skapa alla GameGroups (en per rond)
        for (int round = 1; round <= totalRounds; round++) {
            GameGroup group = new GameGroup();
            group.setRound(round);
            map.add(group);
        }
    
        // Lägg till matcher för varje rond
        for (int round = 1; round <= totalRounds; round++) {
            GameGroup group = map.get(round - 1);
            int numGames = (int) Math.pow(2, totalRounds - round); 
    
            for (int i = 0; i < numGames; i++) {
                Game game = new Game();
                group.add(game);
            }
        }
    
        // Fördela lag till matcher i första ronden
        GameGroup firstRound = map.get(0);
        List<Game> firstRoundGames = firstRound.getGames();
    
        for (int i = 0; i < teams.size(); i += 2) {
            Game game = firstRoundGames.get(i / 2);
            game.setTeamOne(teams.get(i));
            game.setTeamTwo(teams.get(i + 1));
        }
    
        // Koppla matcher till nästa rond (setParent)
        for (int r = 0; r < totalRounds - 1; r++) {
            List<Game> currentRoundGames = map.get(r).getGames();
            List<Game> nextRoundGames = map.get(r + 1).getGames();
    
            for (int i = 0; i < currentRoundGames.size(); i++) {
                Game child = currentRoundGames.get(i);
                Game parent = nextRoundGames.get(i / 2);
                child.setParent(parent);
            }
        }
    }

    public void setWinner(Game game, Team team){
        if (game.getTeamOne() != team && game.getTeamTwo() != team){
            System.err.println("Laget spelar ej i matchen");
        }        

        game.setWinner(team);

        Game nextGame = game.getParent();

        if (nextGame == null){ // alltså finalen nu
            return;
        }

        if (nextGame.getTeamOne() == null){
            nextGame.setTeamOne(team);
        } else if(nextGame.getTeamTwo() == null){
            nextGame.setTeamTwo(team);
        } else { // nästa match har redan två lag
            return;
        }
    }

    public void setTeams(){ // sätter lagen baserat på getUsers
        List<User> registeredUsers = eventInstance.getUsers();
        Collections.shuffle(registeredUsers); // shufflar så alla blir random

        Team currentTeam = new Team("team1");
        int amount = 2;
        for (User user : registeredUsers) {
            currentTeam.addMember(user);

            if (currentTeam.getMembers().size() == teamSize) {
                //Sätter första medlemmen i laget till "lagledare"
                currentTeam.setCreator(currentTeam.getMembers().get(0));
                currentTeam.setTournament(this);
                teams.add(currentTeam);
                String name = "team" + amount;
                currentTeam = new Team(name); // börja nytt lag
                amount++;
            }
        }
    }

    public void setTournamentCreated(){
        this.created = true;
    }
    public boolean getTournamentCreated(){
        return created;
    }

    public List<Team> getTeams(){return teams;}
    public void setTeams (List<Team> teams){ this.teams = teams; }

    public int getTeamSize() {return teamSize; } 
    public void setTeamSize(int teamSize) {this.teamSize = teamSize; }
    
    public EventInstance getEventInstance() { return eventInstance; }

    public void setEventInstance(EventInstance eventInstance) { 
        this.eventInstance = eventInstance; 
        if (eventInstance != null && eventInstance.getTournament() != this) {
            eventInstance.setTournament(this);
        }
    }

    public List<Game> getAllGames() { return allGames; }
    public void setAllGames(List<Game> games) { this.allGames = games; }

    public List<GameGroup> getMap() { return map; }

    public void setMap(List<GameGroup> map) { this.map = map; }

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
