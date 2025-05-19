package com.pvt.demo.model;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;


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

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameGroup> map = new ArrayList<>(); // är en egen gjord map
    
    public Tournament() {}

    public Tournament(EventInstance eventInstance){ // skapa olika konstruktorer för olika spel? free for all, scoreboards, eller brackets osv
        this.eventInstance = eventInstance;
        setTeams();
        generateBracket();
    }

    private void generateBracket() {
        int teamCount = teams.size();
        int gameCount = teamCount - 1;
    
        int round = 1;
        int gamesInCurrentRound = teamCount / 2;
        int startIndex = 0;
    
        // Skapa games
        for (int i = 0 ; i < gameCount; i++) {
            if (i >= startIndex + gamesInCurrentRound) {
                startIndex += gamesInCurrentRound;
                gamesInCurrentRound /= 2;
                round++;
            }
    
            Game game = new Game();
            game.setTournament(this);
            game.setRound(round);
            allGames.add(game);

            addGameToRound(round, game); // compute If absent kopia nedanför
        }
    
        // Koppla game till sina parentnoder
        int currentParentIndex = teamCount / 2;  // starta från första match i rond 2
        for (int i = 0; i < currentParentIndex; i += 2) {
            int child1 = i;
            int child2 = i + 1;
            int parent = currentParentIndex + (i / 2);
    
            if (parent < allGames.size()) {
                Game left = allGames.get(child1);
                Game right = allGames.get(child2);
                Game parentGame = allGames.get(parent);
                left.setParent(parentGame);
                right.setParent(parentGame);
            }
        }
    
        // Tilldela lag till första rundan - itererar och hittar den som har round = 1, och sedan itererar dess matcher och sätter lag
        int index = 0;
        for (GameGroup gameGroup : map){
            if (gameGroup.getRound() == 1){
                for (Game game : gameGroup.getGames()){
                    game.setTeamOne(teams.get(index));
                    game.setTeamTwo(teams.get(index + 1));
                    index += 2;
                }

            }
        }
    }

    public void addGameToRound(int round, Game game) {
        GameGroup group = map.stream()
            .filter(g -> g.getRound() == round)
            .findFirst()
            .orElseGet(() -> {
                GameGroup newGroup = new GameGroup();
                newGroup.setRound(round);
                map.add(newGroup);
                return newGroup;
        });
    group.add(game);
}

    public void setWinner(Game game, Team team){
        if (game.getTeamOne() != team && game.getTeamTwo() != team){
            System.err.println("Laget spelar ej i matchen");
        }        
        Game nextGame = game.getParent();

        if (nextGame.getTeamOne() == null){
            nextGame.setTeamOne(team);
        } else if(nextGame.getTeamTwo() == null){
            nextGame.setTeamTwo(team);
        } else {
            System.err.println("Fel");
            return;
        }

        game.setWinner(team);
    }

    private void setTeams(){ // sätter lagen baserat på getUsers
        List<User> registeredUsers = eventInstance.getUsers();

        Team currentTeam = new Team("team1");
        int amount = 2;
        for (User user : registeredUsers) {
            currentTeam.addMember(user);

            if (currentTeam.getMembers().size() == teamSize) {
                teams.add(currentTeam);
                String name = "team" + amount;
                currentTeam = new Team(name); // börja nytt lag
                amount++;
            }
        }
    }

    public List<Team> getTeams(){return teams;}
    public void setTeams (List<Team> teams){ this.teams = teams; }

    public int getTeamSize() {return teamSize; } 
    public void setTeamSize(int teamSize) {this.teamSize = teamSize; }
    
    public EventInstance getEventInstnace() { return eventInstance; }
    public void setEventInstance(EventInstance eventInstance) { this.eventInstance = eventInstance; }

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
