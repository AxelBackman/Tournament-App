package com.pvt.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Organisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String adress;
    
    @Column(length = 1000)
    private String description; // fyll på mer info om organisationer, typer av event? beskrivning av föreningen?

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("organisation")
    private List<User> members = new ArrayList<>();

    @OneToMany(mappedBy = "organisation", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("organisation")
    private List<RecurringEvent> recurringEvents = new ArrayList<>(); // koppling mot organisationens alla events - skapa attribut för Organisation join cascade i recurringEvent


    public Organisation() {}

    public Organisation(String name, String adress, String description) {
        this.name = name;
        this.adress = adress;
        this.description = description;
    }

    // GETTERS & SETTERS

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

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<RecurringEvent> getRecurringEvents() {
        return recurringEvents;
    }

    public void setRecurringEvents(List<RecurringEvent> recurringEvents) {
        this.recurringEvents = recurringEvents;
    }

    public void addMember(User user) {
        this.members.add(user);
    }

    public void addRecurringEvent(RecurringEvent event) {
        this.recurringEvents.add(event);
        // event.setOrganisation(this); //skapa setOrganisation i recurring
    }



    
}
