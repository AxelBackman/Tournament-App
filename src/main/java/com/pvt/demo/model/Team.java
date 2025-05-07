package com.pvt.demo.model;

import jakarta.persistence.*;

import java.util.List;

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
    private List<User> members;


    @ManyToOne
    private EventInstance eventInstance;

    @ManyToOne
    private OneTimeEvent oneTimeEvent;

    public Team(){}

    public Team(EventInstance eventInstance, List<User> members){
        this.eventInstance = eventInstance;
        this.members = members;
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
