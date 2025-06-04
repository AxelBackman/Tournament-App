package com.pvt.demo.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Comparator;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = true)
    @JsonIgnore
    private Tournament tournament;

    private int teamSize;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    // Lista representererar "Team-Chat"
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TeamChat> messages = new ArrayList<>();

    public Team(){}

    public Team(Tournament tournament, User user, String name){
        this.tournament = tournament;
        members = new ArrayList<>();
        this.members.add(user);
        this.creator = members != null ? this.members.get(0) : null;
        this.name = name;
    }

    public Team(String name){
        members = new ArrayList<>();
        this.name = name;
    }

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

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void addMember(User user){
        this.members.add(user);
    }
    public int getTeamSize(){
        return teamSize;
    }
    public void setTeamSize(int teamSize){
        this.teamSize = teamSize;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public User getCreator() { return creator; }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<TeamChat> getMessages() {
        return messages;
    }

    public void addMessage(TeamChat message) {
        message.setTeam(this);
        messages.add(message);
    }

    //Hämta N meddelanden och sorterar dessa på senaste först
    public List<TeamChat> getLatestMessages(int count) {
        List<TeamChat> sortedMessages = new ArrayList<>(messages);

        sortedMessages.sort(new Comparator<TeamChat>() {
            @Override
            public int compare(TeamChat message1, TeamChat message2) {
                return message2.getTimestamp().compareTo(message1.getTimestamp());
            }
        });

        List<TeamChat> latestMessages = new ArrayList<>();

        for (int i = 0; i < count && i < sortedMessages.size(); i++) {
            latestMessages.add(sortedMessages.get(i));
        }

        return latestMessages;    
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team that = (Team) o;
        if (id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    

}
