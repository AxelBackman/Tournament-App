package com.pvt.demo.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();

    private List<Team> teams;
    private boolean brackets; // if true, skapa brackets, annars något annat - vidare utveckla

    public Tournament() {}

    public Tournament(List<Team> teams, boolean brackets){ // skapa olika konstruktorer för olika spel? free for all, scoreboards, eller brackets osv
        this.teams = teams;
        this.brackets = brackets;

    }




    public boolean getBrackets(){return brackets;}

    public List<Team> getTeams(){return teams;}



}
