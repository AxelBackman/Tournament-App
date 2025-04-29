package com.pvt.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.RegisteredUsers;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RegisteredUsersRepository;
import com.pvt.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/register/{userId}/{eventId}/{coming}")
    public String RegisterUserToEvent(@PathVariable Long userId,
    @PathVariable Long eventId,
    @PathVariable boolean coming){
        var user = userRepository.findById(userId.intValue()).orElse(null);
        var event = eventInstanceRepository.findById(eventId.intValue()).orElse(null);
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
}
