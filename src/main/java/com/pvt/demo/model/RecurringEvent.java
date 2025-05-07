package com.pvt.demo.model;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class RecurringEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "parentEvent", cascade = CascadeType.ALL)
    private List<EventInstance> subEvents = new ArrayList<>();

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    public RecurringEvent(){}

    public RecurringEvent(String name, String description){
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EventInstance> getSubEvents() {
        return subEvents;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Organisation getOrganisation(){
        return organisation;
    }
}
