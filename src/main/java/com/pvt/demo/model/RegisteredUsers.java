package com.pvt.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RegisteredUsers {

    // skall innehålla event + anmälda users

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private EventInstance eventInstance;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "one_time_event_id")
    private OneTimeEvent oneTimeEvent;

    private boolean coming; // = if TRUE = coming, if FALSE = interested


    public RegisteredUsers(){}

    public RegisteredUsers(EventInstance eventInstance, User user, boolean coming){
        this.user = user;
        this.eventInstance = eventInstance;
        this.coming = coming;
    
    }

    public RegisteredUsers(OneTimeEvent oneTimeEvent, User user, boolean coming) {
        this.oneTimeEvent = oneTimeEvent;
        this.user = user;
        this.coming = coming;
    }

    public User getUser(){
        return this.user;
    }

    public EventInstance getEventInstance(){
        return this.eventInstance;
    }

    // if true, then the person is coming - returns true
    public boolean getComing(){
        return this.coming;
    }

    // if false, then the person is only interested - returns true
    public boolean getInterested(){
        return !this.coming;
    }

    /* 
     * if User sets from interested straight to coming we only change boolean.
     * nicen up with controls and return message?
    */
    public void setNewStatus(){
       this.coming = !this.coming; 
    }




    // other setters?

    
}
