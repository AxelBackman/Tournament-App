package com.pvt.demo.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;


@Entity
public class User {   

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    
    private String name;
    private String email;
    
    
    @ManyToOne
    @JsonIgnoreProperties({"recurringEvents", "members"})
    private Organisation organisation;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegisteredUsers> registrations;

    public User() {

    }

    public User(String name, String email, Organisation organisation) {
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }    
}


