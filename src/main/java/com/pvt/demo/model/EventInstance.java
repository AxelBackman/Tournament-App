package com.pvt.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class EventInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String title;
   
    
    @OneToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;


    private String location;
    
    @Column(length = 1000)
    private String description;
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "recurring_event_id", nullable = true) //Valfritt om relationen ska finnas
    private RecurringEvent parentEvent;

    @OneToMany(mappedBy = "eventInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegisteredUsers> registeredUsers = new ArrayList<>();

    public EventInstance() {

    }

    //Konstruktor med RecurringEvent
    public EventInstance(RecurringEvent parentEvent, String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location ) {
        
        this.parentEvent = parentEvent;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    //Konstruktor utan RecurringEvent (anropar första konstruktorn)
    public EventInstance(String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location) {
        this(null, title, description, startTime, endTime, location);
    }
    
    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public RecurringEvent getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(RecurringEvent parentEvent) {
        this.parentEvent = parentEvent;
    }

    public Long getParentEventId() {
        if (parentEvent != null) {
            return parentEvent.getId();
        } else {
            return null;
        }
    }

     public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTournament(Tournament tournament) { this.tournament = tournament; }
    public Tournament getTournament() { return tournament; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventInstance that = (EventInstance) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
