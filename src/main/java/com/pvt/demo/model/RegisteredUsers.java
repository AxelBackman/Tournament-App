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

    private boolean coming; // = if TRUE = coming, if FALSE = interested


    public RegisteredUsers(){}

    public RegisteredUsers(EventInstance eventInstance, User user, boolean coming){
        this.user = user;
        this.eventInstance = eventInstance;
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
        if (this.coming == true){
            return true;
        }
        return false;
    }

    // if false, then the person is only interested - returns true
    public boolean getInterested(){
        if (this.coming == false){
            return true;
        }
        return false;
    }

    /* 
     * if User sets from interested straight to coming we only change boolean.
     * nicen up with controls and return message?
    */
    public void setNewStatus(){
        if (this.coming == false){
            this.coming = true;
        }
        if (this.coming == true){
            this.coming = false;
        }
    }




    // other setters?

    
}
