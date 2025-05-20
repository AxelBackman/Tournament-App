package com.pvt.demo.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TeamChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private LocalDateTime timestamp;

    //Användaren som skickade meddelandet
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    //Laget som meddelandet tillhör
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public TeamChat() {

    }

    public TeamChat(String message, User sender, Team team) {
        this.message = message;
        this.sender = sender;
        this.team = team;
        this.timestamp = LocalDateTime.now();
    }

        public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamChat)) return false;
        TeamChat that = (TeamChat) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


