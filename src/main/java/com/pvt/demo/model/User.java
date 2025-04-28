package com.pvt.demo.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
public class User {   

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    

    private long id;
    private String name;
    private String email;
    private List<EventInstance> comingEvents;
    private List<EventInstance> interestedEvents;

    public User() {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        comingEvents = new ArrayList<>();
        interestedEvents = new ArrayList<>();

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<EventInstance> getComing(){
        return comingEvents;
    }

    public List<EventInstance> getInterested(){
        return interestedEvents;
    }

    public void addComingEvent(EventInstance eventInstance){
        this.comingEvents.add(eventInstance);
    }

    public void addInterestedEvent(EventInstance eventInstance){
        this.interestedEvents.add(eventInstance);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}


