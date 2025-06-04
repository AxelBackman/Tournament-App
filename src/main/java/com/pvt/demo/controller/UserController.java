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
import com.pvt.demo.dto.UserResponseDto;

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

    

    private UserResponseDto toResponseDto(User user) {
        try {
            return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getOrganisation().getId(),
                user.getOrganisation().getName(),
                user.isAdmin()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error converting User to UserResponseDto: " + e.getMessage(), e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponseDto> usersDto = userRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
            return ResponseEntity.ok(usersDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching users: " + e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(toResponseDto(userOpt.get()));
            } else {
                return ResponseEntity.badRequest().body("User with ID " + id + " not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user by ID: " + e.getMessage());
        }
    }

   @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(toResponseDto(userOpt.get()));
            } else {
                return ResponseEntity.badRequest().body("User with email " + email + " not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user by email: " + e.getMessage());
        }
    }

    @PostMapping("/adduser")
    public ResponseEntity<String> addNewUser(@Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }

        if (userRepository.findByName(userDto.name).isPresent()) {
            return ResponseEntity.badRequest().body("A user with the name '" + userDto.name + "' already exists.");
        }
        
        Organisation organisation = organisationRepository.findById(userDto.organisationId).orElse(null);

        if (organisation != null) {
            User user = new User(userDto.name, userDto.email, organisation, userDto.isAdmin); 
            userRepository.save(user);
            return ResponseEntity.ok("User '" + user.getName() + "' saved successfully");
        } else {
            return ResponseEntity.badRequest().body("Organisation with ID " + userDto.organisationId + " not found");
        }
    }

    @PutMapping("/updateuser/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }
        
        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            Optional<User> existingUser = userRepository.findByName(userDto.name);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                return ResponseEntity.badRequest().body("A user with the name '" + userDto.name + "' already exists.");
            }

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
                teamRepository.save(team); 
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
