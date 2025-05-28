package com.pvt.demo.dto;


public class UserResponseDto {
    public Long id;
    public String name;
    public String email;
    public Long organisationId;
    public String organisationName;
    public boolean isAdmin;

    public UserResponseDto(Long id, String name, String email, Long organisationId, String organisationName, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.isAdmin = isAdmin;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Long getOrganisationId() { return organisationId; }
    public String getOrganisationName() { return organisationName; }
    public boolean isAdmin() { return isAdmin; }
}