package com.pvt.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.Organisation;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.OrganisationRepository;
import com.pvt.demo.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/users")
@CrossOrigin() 
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;
    
    @GetMapping
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

     @PostMapping("/adduser/{userName}/{email}/{organisationId}")
    public String addNewUser(@PathVariable String userName, @PathVariable String email, @PathVariable Long organisationId) {
        // Hämta organisationen baserat på ID
        Organisation organisation = organisationRepository.findById(organisationId).orElse(null);
        
        if (organisation != null) {
            User user = new User(userName, email, organisation); // Skapa användare med organisation
            userRepository.save(user);
            return "User '" + userName + "' saved successfully";
        } else {
            return "Organisation with ID " + organisationId + " not found";
        }
    }

    @PutMapping("/updateuser/{id}/{userName}/{email}/{organisationId}")
    public String updateUser(@PathVariable Long id, @PathVariable String userName, @PathVariable String email, @PathVariable Long organisationId) {
        User user = userRepository.findById(id).orElse(null);
        
        if (user != null) {
            Organisation organisation = organisationRepository.findById(organisationId).orElse(null);
            if (organisation != null) {
                user.setName(userName); // Uppdatera namn
                user.setEmail(email);   // Uppdatera email
                user.setOrganisation(organisation); // Koppla till ny organisation
                userRepository.save(user); // Spara uppdaterad användare
                return "User with ID " + id + " updated successfully";
            } else {
                return "Organisation with ID " + organisationId + " not found";
            }
        } else {
            return "User with ID " + id + " not found";
        }
    }

    @DeleteMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User with ID " + id + " deleted successfully";
    }

    @DeleteMapping("/deleteall")
    public String deleteAllUsers() {
        userRepository.deleteAll();
        return "All users deleted successfully";
    }
}
