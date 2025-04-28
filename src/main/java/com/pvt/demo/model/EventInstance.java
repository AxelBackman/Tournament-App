package com.pvt.demo.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class EventInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private boolean randomisedMatching;
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "recurring_event_id")
    private RecurringEvent parentEvent;

    public Integer getId() {
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

    public boolean isRandomisedMatching() {
        return randomisedMatching;
    }

    public void setRandomisedMatching(boolean randomisedMatching) {
        this.randomisedMatching = randomisedMatching;
    }

    public RecurringEvent getParentEvent() {
        return parentEvent;
    }

    public void setParentEvent(RecurringEvent parentEvent) {
        this.parentEvent = parentEvent;
    }

    public void method(String type) {
        // Funktionalitet
    }
}
