package com.pvt.demo.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
public class User {   

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    

    private long id;
    private String name;
    private String email;
    private List<EventInstance> coming;
    private List<EventInstance> interested;

    public User() {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        coming = new ArrayList<>();
        interested = new ArrayList<>();

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
    
}


