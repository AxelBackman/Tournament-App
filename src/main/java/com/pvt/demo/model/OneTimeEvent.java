package com.pvt.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;


@Entity
public class OneTimeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private String name; 
    private LocalDateTime startTime; 
    private LocalDateTime endTime; 
    private String location; 
    private int teamSize; 

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation org; 

    @OneToMany(mappedBy = "oneTimeEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>(); 

    @OneToMany(mappedBy = "oneTimeEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegisteredUsers> registeredUsers = new ArrayList<>(); 

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>(); 

    
    public OneTimeEvent() {}

    /**
     * Constructs a new OneTimeEvent with the specified details.
     *
     * @param name 
     * @param startTime 
     * @param endTime 
     * @param location 
     * @param teamSize 
     */
    public OneTimeEvent(String name, LocalDateTime startTime, LocalDateTime endTime, String location, int teamSize) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.teamSize = teamSize;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // Added setter for ID

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }

    public Organisation getOrganisation() { return org; }
    public void setOrganisation(Organisation org) { this.org = org; }

    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams; }

    public List<RegisteredUsers> getRegisteredUsers() { return registeredUsers; }
    public void setRegisteredUsers(List<RegisteredUsers> registeredUsers) { this.registeredUsers = registeredUsers; }

    public List<Match> getMatches() { return matches; }
    public void setMatches(List<Match> matches) { this.matches = matches; }
}