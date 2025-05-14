package com.pvt.demo.model;

import jakarta.persistence.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.ArrayList;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "tournament_id", nullable = true)
    private Tournament tournament;

    private int teamSize;
    private boolean recurringEvent;

    private String name;



    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    public Team(){}

    public Team(Tournament tournament, User user){
        this.tournament = tournament;
        this.members.add(user);
        this.teamSize = tournament != null ? tournament.getTeamSize() : 0;
        this.creator = members != null ? this.members.get(0) : null;

    }

    public boolean getRecurringEvent(){
        return recurringEvent;
    }

    public void setRecurringEvent(Boolean bool){
        recurringEvent = bool;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void addMember(User user){
        this.members.add(user);
    }
    public int getTeamSize(Team team){
        return teamSize;
    }
    public void setTeamSize(int teamSize){
        this.teamSize = teamSize;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

}
