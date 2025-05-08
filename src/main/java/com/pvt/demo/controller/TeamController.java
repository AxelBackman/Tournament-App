package com.pvt.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/teams")
@CrossOrigin()
public class TeamController {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    // Skapa ett team
    @PostMapping("/create/{eventInstanceId}/{userId}")
    public String createTeam(@PathVariable Long eventInstanceId, @PathVariable Long userId) {
        EventInstance eventInstance = eventInstanceRepository.findById(eventInstanceId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (eventInstance == null || user == null) {
            return "Event instance not found";
        }

        Team team = new Team();
        team.setEventInstance(eventInstance);
        team.setMembers(new ArrayList<>());
        team.getMembers().add(user);

        teamRepository.save(team);

        return "Team created successfully" + team.getId();
    }

    // Lägg till en medlem i teamet
    @PatchMapping("/addMember/{teamId}/{userId}")
    public String addMember(@PathVariable Long teamId, @PathVariable Long userId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (team == null || user == null) {
            return "Team or user not found";
        }

        if (!team.getMembers().contains(user)) {
            team.getMembers().add(user);
            teamRepository.save(team);
            return "User added to team successfully";
        } else {
            return "User already in team";
        }
       
    }
    
    // Hämta alla teams
    @GetMapping
    public Iterable<Team> getAll() {
        return teamRepository.findAll();
    }

    // Hämta teamns för en viss EventInstance
    @GetMapping("/event/{eventInstanceId}")
    public List<Team> getTeamsByEventInstance(@PathVariable Long eventInstanceId) {
        EventInstance eventInstance = eventInstanceRepository.findById(eventInstanceId).orElse(null);
        if (eventInstance == null) {
            return null;
        }
        return teamRepository.findByEventInstanceId(eventInstanceId);
    }

}
