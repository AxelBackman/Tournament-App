package com.pvt.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teamA_id")
    private Team teamA;

    @ManyToOne
    @JoinColumn(name = "teamB_id")
    private Team teamB;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventInstance event;

    private Integer round;
    private String result;
    private LocalDateTime matchDate;
    private String location;

    public Match() {}

    /**
     * Constructs a new Match with the specified details.
     *
     * @param teamA
     * @param teamB
     * @param event
     * @param round
     * @param matchDate
     * @param location
     */
    public Match(Team teamA, Team teamB, EventInstance event, Integer round, LocalDateTime matchDate, String location) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.event = event;
        this.round = round;
        this.matchDate = matchDate;
        this.location = location;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Team getTeamA() { return teamA; }
    public void setTeamA(Team teamA) { this.teamA = teamA; }

    public Team getTeamB() { return teamB; }
    public void setTeamB(Team teamB) { this.teamB = teamB; }

    public EventInstance getEvent() { return event; }
    public void setEvent(EventInstance event) { this.event = event; }

    public Integer getRound() { return round; }
    public void setRound(Integer round) { this.round = round; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

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