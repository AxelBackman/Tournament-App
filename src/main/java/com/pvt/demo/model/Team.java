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
    @JoinColumn(name = "one_time_event_id", nullable = true)
    private OneTimeEvent oneTimeEvent;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "event_instance_id", nullable = true)
    private EventInstance eventInstance;

    private int teamSize;
    private boolean recurringEvent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    public Team(){}

    public Team(EventInstance eventInstance, OneTimeEvent oneTimeEvent, User user){
        this.eventInstance = eventInstance;
        this.members.add(user);
        this.teamSize = eventInstance != null ? eventInstance.getTeamSize() : oneTimeEvent.getTeamSize();
        this.creator = members != null ? this.members.get(0) : null;
        
        if(eventInstance == null){
            recurringEvent = false;
            this.eventInstance = eventInstance;
        } else{
            recurringEvent = true;
            this.oneTimeEvent = oneTimeEvent;
        }
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

    public EventInstance getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstance eventInstance) {
        this.eventInstance = eventInstance;
    }

    public OneTimeEvent getOneTimeEvent() {
        return oneTimeEvent;
    }
    
    public void setOneTimeEvent(OneTimeEvent oneTimeEvent) {
        this.oneTimeEvent = oneTimeEvent;
    }
}
