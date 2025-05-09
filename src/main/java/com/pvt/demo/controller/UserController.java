package com.pvt.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.User;
import com.pvt.demo.repository.UserRepository;


@RestController
@RequestMapping("/users")
@CrossOrigin() 
public class UserController {
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/users")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    //för google oauth
    @GetMapping("/info")
    public Map<String, Object> userInfo(OAuth2AuthenticationToken authentication) {
        // Return the user's attributes as a map
        return authentication.getPrincipal().getAttributes();
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

    @DeleteMapping("/deleteall")
    public String deleteAllUsers() {
        userRepository.deleteAll();
        return "All users deleted successfully";
    }
}
