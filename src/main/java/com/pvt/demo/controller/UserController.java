package com.pvt.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.Organisation;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.OrganisationRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.UserRepository;

import jakarta.validation.Valid;

import com.pvt.demo.dto.UserDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/adduser")
    public ResponseEntity<String> addNewUser(@Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Username must be min 2 and max 20 characters long");
        }
        
        // Hämta organisationen baserat på ID
        Organisation organisation = organisationRepository.findById(userDto.organisationId).orElse(null);

        if (organisation != null) {
            User user = new User(userDto.name, userDto.email, organisation, userDto.isAdmin); // Skapa användare med organisation
            userRepository.save(user);
            return ResponseEntity.ok("User '" + user.getName() + "' saved successfully");
        } else {
            return ResponseEntity.badRequest().body("Organisation with ID " + userDto.organisationId + " not found");
        }
    }

    @PutMapping("/updateuser/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Username must be min 2 and max 20 characters long");
        }
        
        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            Organisation organisation = organisationRepository.findById(userDto.organisationId).orElse(null);
            if (organisation != null) {
                user.setName(userDto.name);
                user.setEmail(userDto.email);
                user.setOrganisation(organisation);
                user.setAdmin(userDto.isAdmin);
                userRepository.save(user);
                return ResponseEntity.ok("User with ID " + id + " updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Organisation with ID " + userDto.organisationId + " not found");
            }
        } else {
            return ResponseEntity.badRequest().body("User with ID " + id + " not found");
    }
}

    @DeleteMapping("/deleteuser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        User user = userOpt.get();

        List<Team> teams = teamRepository.findByMembers_Id(id);


        for (Team team : teams) {
            team.getMembers().remove(user);
            if (team.getMembers().isEmpty()) {
                teamRepository.delete(team);
            } else {
                teamRepository.save(team); // Save team without the user
            }
        }
        userRepository.delete(user);
        return ResponseEntity.ok("User with ID " + id + " deleted successfully");
    }




    @DeleteMapping("/deleteall")
    public ResponseEntity<String> deleteAllUsers() {
        userRepository.deleteAll();
        return ResponseEntity.ok("All users deleted successfully");
    }
}
