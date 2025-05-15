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
    private List<Game> matches = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    @OneToOne(mappedBy = "tournament", cascade = CascadeType.ALL)
    private EventInstance eventInstance;

    private boolean brackets; // if true, skapa brackets, annars något annat - vidare utveckla

    public Tournament() {}

    public Tournament(boolean brackets, EventInstance eventInstance){ // skapa olika konstruktorer för olika spel? free for all, scoreboards, eller brackets osv
        this.brackets = brackets;
        this.eventInstance = eventInstance;

    }

    public boolean getBrackets(){return brackets;}

    public List<Team> getTeams(){return teams;}
    public void setTeams (List<Team> teams){ this.teams = teams; }

    public int getTeamSize() {return teamSize; } 
    public void setTeamSize(int teamSize) {this.teamSize = teamSize; }
    
    public EventInstance getEventInstnace() { return eventInstance; }
    public void setEventInstance(EventInstance eventInstance) { this.eventInstance = eventInstance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



}
