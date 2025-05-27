package com.pvt.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.dto.RegisteredUsersResponseDto;
import com.pvt.demo.model.RegisteredUsers;
import com.pvt.demo.model.RegistrationStatus;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RegisteredUsersRepository;
import com.pvt.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/registeredusers")
@CrossOrigin()
public class RegisteredUsersController {
    @Autowired 
    private RegisteredUsersRepository registeredUsersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @PostMapping("/register/{userId}/{eventId}/{status}")
    public ResponseEntity<String> RegisterUserToEvent(@PathVariable Long userId,
    @PathVariable Long eventId,
    @PathVariable RegistrationStatus status){
        var user = userRepository.findById(userId).orElse(null);
        var event = eventInstanceRepository.findById(eventId).orElse(null);

        if (user == null || event == null) {
            return ResponseEntity.badRequest().body("User or Event not found");
        }
        
        if (registeredUsersRepository.findByUserIdAndEventInstanceId(userId, eventId) != null) {
            return ResponseEntity.badRequest().body("User is already registered for this event");
        }

        RegisteredUsers registeredUser = new RegisteredUsers(event, user, status);
        registeredUsersRepository.save(registeredUser);
        
        return ResponseEntity.ok("User " + user.getName() + " registered for event " + event.getId() + " with status: " + (status.name())); 
    }

    @DeleteMapping("/delete/{userId}/{eventId}")
    public ResponseEntity<String> deleteUserFromEvent(@PathVariable Long userId,
    @PathVariable Long eventId) {
        var registeredUser = registeredUsersRepository.findByUserIdAndEventInstanceId(userId, eventId);
        if (registeredUser == null) {
            return ResponseEntity.badRequest().body("User is not registered for this event");
        }
        registeredUsersRepository.delete(registeredUser);
        return ResponseEntity.ok("User " + userId + " unregistered from event " + eventId);
    }

    @GetMapping("/allregistered/{eventId}")
    public ResponseEntity<?> getAllRegisteredUsers(@PathVariable Long eventId) {
        // Check if event exists
        var eventOpt = eventInstanceRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Event with id " + eventId + " not found");
        }

        // Get registered users
        var registeredUsers = registeredUsersRepository.findByEventInstanceId(eventId);
        List<RegisteredUsersResponseDto> responseList = new ArrayList<>();

        for (RegisteredUsers regUser : registeredUsers) {
            var user = regUser.getUser();
            var event = regUser.getEventInstance();

            responseList.add(new RegisteredUsersResponseDto(
                user.getId(),
                user.getName(),
                event.getId(),
                event.getTitle(),
                regUser.getStatus().name()
            ));
        }

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/allregistereduser/{userId}")
    public Iterable<RegisteredUsers> getAllRegisteredUser(@PathVariable Long userId) {
        return registeredUsersRepository.findByUserId(userId);
    }

    @PatchMapping("/updatecoming/{userId}/{eventId}/{status}")
    public ResponseEntity<String> updateComingStatus(@PathVariable Long userId,
    @PathVariable Long eventId, 
    @PathVariable RegistrationStatus status) {
        var registeredUser = registeredUsersRepository.findByUserIdAndEventInstanceId(userId, eventId);
        if (registeredUser == null) {
            return ResponseEntity.badRequest().body("User is not registered for this event");
        }

        registeredUser.setStatus(status);
        registeredUsersRepository.save(registeredUser);

        return ResponseEntity.ok("Coming status updated to " + status.name() + " for user " + userId + " in event " + eventId);
    }
    
}
