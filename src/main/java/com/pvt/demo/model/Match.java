package com.pvt.demo.model;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Match {
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
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @Column(nullable = true)
    private int round;

    @Column(nullable = true)
    private Team winner;

    @Column(nullable = true)
    @OneToOne(mappedBy = "leftMatch_id", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("leftMatch")
    private Match leftMatch; // representerar barn noder - så round 1 har inga barn // if null, then = leaf
    
    @Column(nullable = true)
    @OneToOne(mappedBy = "rightMatch_id", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("rightMatch")
    private Match rightMatch; // if null, then = leaf

    @Column(nullable = true)
    @OneToOne(mappedBy = "parentMatch_id", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("parentMatch")
    private Match parentMatch; // if null, then = root

    public Match() {}
   
    public Match(Team teamOne, Team teamTwo, Tournament tournament, int round) {
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.tournament = tournament;
        this.round = round;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Team getTeamA() { return teamOne; }
    public void setTeamA(Team teamA) { this.teamOne = teamA; }

    public Team getTeamB() { return teamTwo; }
    public void setTeamB(Team teamB) { this.teamTwo = teamB; }

    public Tournament getTournament() { return tournament; }
    public void setTournament(Tournament tournament) { this.tournament = tournament; }

    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }

    public Team getWinner () { return winner; }
    public void setWinner(Team winner) {this.winner = winner; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}