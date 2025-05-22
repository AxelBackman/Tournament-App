package com.pvt.demo.model;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;

@Entity
public class RecurringEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "parentEvent", cascade = CascadeType.ALL)
    private List<EventInstance> subEvents = new ArrayList<>();

    @ManyToOne
    @JsonIgnoreProperties({"recurringEvents", "members"})
    private Organisation organisation;

    public RecurringEvent(){}

    public RecurringEvent(String name, String description){
        this.name = name;
        this.description = description;
    }

    public RecurringEvent(String name, String description, Organisation organisation) {
        this.name = name;
        this.description = description;
        this.organisation = organisation;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecurringEvent that = (RecurringEvent) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
