package com.pvt.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.RegisteredUsers;
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
@CrossOrigin
public class RegisteredUsersController {
    @Autowired 
    private RegisteredUsersRepository registeredUsersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @PostMapping("/register/{userId}/{eventId}/{coming}")
    public String RegisterUserToEvent(@PathVariable Long userId,
    @PathVariable Long eventId,
    @PathVariable boolean coming){
        var user = userRepository.findById(userId).orElse(null);
        var event = eventInstanceRepository.findById(eventId).orElse(null);
        if (user == null || event == null) {
            return "User or Event not found";
        }
        if (registeredUsersRepository.findByUserIdAndEventInstanceId(userId, eventId) != null) {
            return "User is already registered for this event";
        }

        RegisteredUsers registeredUser = new RegisteredUsers(event, user, coming);
        registeredUsersRepository.save(registeredUser);
        
        return "User " + user.getName() + " registered for event " + event.getId() + " with coming status: " + (coming ? "Coming" : "Interested"); 
    }

    @DeleteMapping("/delete/{userId}/{eventId}")
    public String deleteUserFromEvent(@PathVariable Long userId,
    @PathVariable Long eventId) {
        var registeredUser = registeredUsersRepository.findByUserIdAndEventInstanceId(userId, eventId);
        if (registeredUser == null) {
            return "User is not registered for this event";
        }
        registeredUsersRepository.delete(registeredUser);
        return "User " + userId + " unregistered from event " + eventId;
    }

    @GetMapping("/allregistered/{eventId}")
    public Iterable<RegisteredUsers> getAllRegisteredUsers(@PathVariable Long eventId) {
        return registeredUsersRepository.findByEventInstanceId(eventId);
    }

    @GetMapping("/allregistereduser/{userId}")
    public Iterable<RegisteredUsers> getAllRegisteredUser(@PathVariable Long userId) {
        return registeredUsersRepository.findByUserId(userId);
    }

    @PatchMapping("/updatecoming/{userId}/{eventId}/{coming}")
    public String updateComingStatus(@PathVariable Long userId,
    @PathVariable Long eventId, @PathVariable boolean coming) {
        var registeredUser = registeredUsersRepository.findByUserIdAndEventInstanceId(userId, eventId);
        if (registeredUser == null) {
            return "User is not registered for this event";
        }
        registeredUser.setNewStatus();
        registeredUsersRepository.save(registeredUser);
        return "Coming status updated to " + (coming ? "Coming" : "Interested") + " for user " + userId + " in event " + eventId;
    }
    
}
