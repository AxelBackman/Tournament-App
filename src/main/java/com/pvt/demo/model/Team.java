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
    private User user;

    @OneToMany
    @JsonBackReference
    @JoinColumn(name = "event_instance_team_size")
    private int teamSize;


    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "event_instance_id")
    private EventInstance eventInstance;

    public Team(){}

    public Team(EventInstance eventInstance, User user, int teamSize){
        this.eventInstance = eventInstance;
        this.user = user;
        members.add(user);
        this.teamSize = teamSize;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public User getUser(Team team){
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public EventInstance getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstance eventInstance) {
        this.eventInstance = eventInstance;
    }
}
