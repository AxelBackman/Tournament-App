package com.pvt.demo.model;

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
    @JsonIgnoreProperties({"oneTimeEvents", "recurringEvents", "members"})
    private Organisation organisation;

    public User() {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        

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
    
}


