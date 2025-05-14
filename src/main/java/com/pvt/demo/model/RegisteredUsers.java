package com.pvt.demo.model;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RegisteredUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_instance_id")
    private EventInstance eventInstance;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;

    public RegisteredUsers(){}

    public RegisteredUsers(EventInstance eventInstance, User user, RegistrationStatus status){
        this.user = user;
        this.eventInstance = eventInstance;
        this.status = status; // COMING eller INTERESTED
    }

    public User getUser(){
        return this.user;
    }

    public EventInstance getEventInstance(){
        return this.eventInstance;
    }
    
    public RegistrationStatus getStatus(){ return this.status; }
    public boolean isComing() { return this.status == RegistrationStatus.COMING; }
    public boolean isInterested() { return this.status == RegistrationStatus.INTERESTED; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

}
