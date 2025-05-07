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
    private List<User> members = new ArrayList<>();
    private User user;


    @ManyToOne
    private EventInstance eventInstance;

    @ManyToOne
    private OneTimeEvent oneTimeEvent;

    public Team(){}

    public Team(EventInstance eventInstance, User user){
        this.eventInstance = eventInstance;
        this.user = user;
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
