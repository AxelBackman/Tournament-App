package com.pvt.demo.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.dto.TeamDto;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.TournamentRepository;
import com.pvt.demo.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/teams")
@CrossOrigin()
public class TeamController {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    // Skapa ett team
    @PostMapping("/create")
    public ResponseEntity<String> createTeam(@RequestBody TeamDto teamDto) {
       

        Tournament tournament = tournamentRepository.findById(teamDto.tournamentId).orElse(null);
        User user = userRepository.findById(teamDto.userId).orElse(null);

        if (tournament == null || user == null) {
            return ResponseEntity.badRequest().body("Event instance or user not found");
        }

        // Skapa team med hjälp av konstruktorn
        Team team = new Team(tournament, user, teamDto.name);

        // Spara teamet i databasen
        teamRepository.save(team);

        return ResponseEntity.ok("Team '" + team.getName() + "' created successfully with ID " + team.getId());
    }

    // Lägg till en medlem i teamet
    @PatchMapping("/addMember/{teamId}/{userId}")
    public ResponseEntity<String> addMember(@PathVariable Long teamId, @PathVariable Long userId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (team == null || user == null) {
            return ResponseEntity.badRequest().body("Team or user not found");
        }

        if (team.getMembers().size() >= team.getTournament().getTeamSize()) {
            return ResponseEntity.badRequest().body("Team is already full (max " + team.getTournament().getTeamSize() + " members)");
        }

        if (!team.getMembers().contains(user)) {
            team.getMembers().add(user);
            teamRepository.save(team);
            return ResponseEntity.ok("User added to team successfully");
        } else {
            return ResponseEntity.badRequest().body("User already in team");
        }
       
    }
    
    // Hämta alla teams
    @GetMapping
    public Iterable<Team> getAll() {
        return teamRepository.findAll();
    }

    // Hämta teamns för en viss EventInstance
    @GetMapping("/event/{tournamentId}")
    public ResponseEntity<List<Team>> getTeamsByEventInstance(@PathVariable Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        if (tournament == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Team> teams = teamRepository.findByTournamentId(tournamentId);
        return ResponseEntity.ok(teams);
    }

    //Hämta members efter teamId
    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<User>> getTeamMembers(@PathVariable Long teamId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(team.getMembers());
    }

}
