package com.pvt.demo.model;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teamOne_id")
    private Team teamOne;

    @ManyToOne
    @JoinColumn(name = "teamTwo_id")
    private Team teamTwo;

    @ManyToOne
    @JoinColumn(name = "tournament_id") // denna klass äger relationen
    private Tournament tournament;

    @Column(nullable = true)
    private Integer round;

    @ManyToOne
    @JoinColumn(name = "winner_id", nullable = true)
    private Team winner;
    
    @OneToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"left", "right"})
    private Game parent; // if null, then = root

    @OneToOne(mappedBy = "parent")
    @JsonIgnoreProperties("parent")
    private Game left; // if null, then = leaf

    @OneToOne(mappedBy = "parent")
    @JsonIgnoreProperties("parent")
    private Game right; // if null, then = leaf

    @ManyToOne
    @JoinColumn(name = "game_group_id") // foreign key i Game-tabellen
    private GameGroup gameGroup;

    public Game() {}
   
    public Game(Team teamOne, Team teamTwo, Tournament tournament, Integer round) {
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.tournament = tournament;
        this.round = round;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Team getTeamOne() { return teamOne; }
    public void setTeamOne(Team teamOne) { this.teamOne = teamOne; }

    public Team getTeamTwo() { return teamTwo; }
    public void setTeamTwo(Team teamTwo) { this.teamTwo = teamTwo; }

    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public Integer getRound() { return round; }
    public void setRound(Integer round) { this.round = round; }

    public Team getWinner () { return winner; }
    public void setWinner(Team winner) {this.winner = winner; }

    public Game getParent() { return parent; }
    public void setParent(Game game){ this.parent = game; }

    public void setGameGroup(GameGroup gameGroup) { this.gameGroup = gameGroup; }
    public GameGroup getGameGroup() { return gameGroup; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game that = (Game) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}