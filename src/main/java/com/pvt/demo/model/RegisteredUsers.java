package com.pvt.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class RegisteredUsers {

    // skall innehålla event + anmälda users

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private EventInstance eventInstance;


    private String comingOrInterested; // = coming OR interested


    public RegisteredUsers(){}

    public RegisteredUsers(EventInstance eventInstance, User user, String comingOrInterested){
        this.user = user;
        this.eventInstance = eventInstance;
        this.comingOrInterested = comingOrInterested;
    
    }

    public User getUser(){
        return this.user;
    }

    public EventInstance getEventInstance(){
        return this.eventInstance;
    }

    public String getComingOrInterested(){
        return this.comingOrInterested;
    }

    // setters?

    
}
