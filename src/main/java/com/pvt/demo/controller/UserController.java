package com.pvt.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;
    
    @GetMapping("/users")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/adduser/{userName}")
    public String addNewUser(@PathVariable String userName) {
        User user = new User();
        user.setName(userName);
        userRepository.save(user);
        return "User '" + userName + "' saved successfully";
    }

    @GetMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
        return "User with ID " + id + " deleted successfully";
    }

    @GetMapping("coming/{userId}/{eventId}")
    public String addComingUser(@PathVariable Integer userId, @PathVariable Integer eventId) {
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "User not found";
        }
        
        EventInstance event = eventInstanceRepository.findById(eventId).orElse(null);
        if (event == null) {
            return "Event not found";
        }
        event.addComingUser(user);
        eventInstanceRepository.save(event);
        user.getComing().add(event);
        userRepository.save(user);
        return "User " + user.getName() + " added to the coming list for event ID " + eventId;
    }

    @GetMapping("interested/{userId}/{eventId}")
    public String addInterestedUser(@PathVariable Integer userId, @PathVariable Integer eventId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "User not found";
        }
        EventInstance event = eventInstanceRepository.findById(eventId).orElse(null);
        if (event == null) {
            return "Event not found";
        }
        event.addInterestedUser(user);
        eventInstanceRepository.save(event);
        user.getInterested().add(event);
        userRepository.save(user);
        return "User " + user.getName() + " added to the interested list for event ID " + eventId;
    }
}
