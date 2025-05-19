package com.pvt.demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class GameGroup {
    @Id
    @GeneratedValue
    private Long id;

    private Integer keyValue; // motsvarar Map-nyckeln - round i detta fallet - wrapper type pga de

    @ManyToOne
    @JoinColumn(name = "tournament_id") // foreign key i GameGroup-tabellen
    private Tournament tournament;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Game> games = new ArrayList<>();

    public GameGroup(){}

    public void add(Game game){
        this.games.add(game);
    }

    public List<Game> getGames(){
        return this.games;
    }

    public void setRound(Integer round){
        this.keyValue = round;
    }
    public Integer getRound(){
        return keyValue;
    }
}
