package com.pvt.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.User;
import com.pvt.demo.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*") // Allow all origins for testing purposes
public class UserController {
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/users")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/adduser/{userName}/{email}")
    public String addNewUser(@PathVariable String userName, @PathVariable String email) {
        User user = new User();
        user.setName(userName);
        user.setEmail(email);
        userRepository.save(user);
        return "User '" + userName + "' saved successfully";
    }

    @DeleteMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User with ID " + id + " deleted successfully";
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/deleteall")
    public String deleteAllUsers() {
        userRepository.deleteAll();
        return "All users deleted successfully";
    }
}
