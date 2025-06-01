package com.pvt.demo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;

@Entity
public class EventInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    private int teamSize;

    @Size(max = 250, message = "Description cannot exceed 200 characters")
    private String description;

    @Size(max = 1500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

   
    
    @OneToOne
    @JoinColumn(name = "tournament_id")
    @JsonIgnore
    private Tournament tournament;

    private String location;
    
    
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "recurring_event_id", nullable = true) //Valfritt om relationen ska finnas
    private RecurringEvent parentEvent;

    @OneToMany(mappedBy = "eventInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegisteredUsers> registeredUsers = new ArrayList<>();

    public EventInstance() {

    }

    //Konstruktor med RecurringEvent
    public EventInstance(RecurringEvent parentEvent, String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location, int teamSize) {
        this.parentEvent = parentEvent;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.teamSize = teamSize;
    }

    //Konstruktor utan RecurringEvent (anropar första konstruktorn)
    public EventInstance(String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location, int teamSize) {
        this(null, title, description, startTime, endTime, location, teamSize);
    }

    public int getTeamSize(){
        return this.teamSize;
    }

    public void setTeamSize(int teamSize){
        this.teamSize = teamSize;
    }

    
    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();

        for (RegisteredUsers user : registeredUsers){
            if(user.getStatus() == RegistrationStatus.COMING) {
                users.add(user.getUser());
            }
        }
        return users;
    }

    public void setUsers(List<User> users) {
        List<RegisteredUsers> registeredUsersList = new ArrayList<>();
        for (User user : users) {
            RegisteredUsers registeredUser = new RegisteredUsers(this, user, RegistrationStatus.COMING);
            registeredUsersList.add(registeredUser);
        }
        this.registeredUsers = registeredUsersList;
    }

    public void setRegisteredUsers(List<RegisteredUsers> registeredUsers) {
        this.registeredUsers = registeredUsers;
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

    public void setTournament(Tournament tournament) {
         this.tournament = tournament;
         if (tournament != null && tournament.getEventInstance() != this) {
            tournament.setEventInstance(this);
         } 
        }

    public Tournament getTournament() { return tournament; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

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
