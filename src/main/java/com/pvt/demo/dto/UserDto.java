package com.pvt.demo.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDto {
    
    @Size(min = 2, max = 20, message = "Username must be min 2 and max 20 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9_åäöÅÄÖ\\- ]+$", message = "Username contains invalid characters")
    public String name;
    
    public String email;
    public Long organisationId;
    public boolean isAdmin;

    public UserDto() {
        
    }

}


