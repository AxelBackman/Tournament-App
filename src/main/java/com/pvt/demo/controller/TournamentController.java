package com.pvt.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.dto.TournamentDto;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.TournamentRepository;
import com.pvt.demo.repository.UserRepository;

@RestController
@RequestMapping("/tournaments")
@CrossOrigin() 
public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    // Hämta tournament via ID
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getOrganisationById(@PathVariable Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        return tournament.map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // setTeams
    //generateBracket

    //Skapande av ett Tournament för test av annat
    @PostMapping
    public ResponseEntity<?> createTournament(@RequestBody TournamentDto dto) {
        try {

            if (dto.eventInstanceId == null) {
                return ResponseEntity.badRequest().body("EventInstanceId is required");
            }

            EventInstance eventInstance = eventInstanceRepository.findById(dto.eventInstanceId)
                    .orElse(null);

            if (eventInstance == null) {
                return ResponseEntity.badRequest().body("EventInstance not found");
            }

            List<Tournament> existingTournaments = tournamentRepository.findByEventInstanceId(eventInstance.getId());
            if (!existingTournaments.isEmpty()) {
                return ResponseEntity.badRequest().body("Det finns redan en tournament: " + existingTournaments.get(0));
            }

            int teamSize = eventInstance.getTeamSize();

            java.util.List<com.pvt.demo.model.User> users = eventInstance.getUsers();

            if (users == null || users.size() == 0) {
                return ResponseEntity.badRequest().body("EventInstance must have users");
            }

            if (users.size() % teamSize != 0) {
                return ResponseEntity.badRequest().body("Number of users must be evenly divisible by team size");
            }

            // Skapa Tournament och generera lag
            Tournament newTournament = new Tournament(eventInstance, teamSize);
            newTournament.setTeams();  // Detta förutsätter att setTeams() använder users och teamSize

            
            // Validera att antal lag är jämnt delbart med 4
            if (newTournament.getTeams() == null || newTournament.getTeams().size() % 4 != 0) {
                return ResponseEntity.badRequest().body("Number of teams must be divisible by 4 to create a bracket");
            }

            newTournament.generateBracket();  // Bara om team-antalet är korrekt

            Tournament saved = tournamentRepository.save(newTournament);
            
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace(); // skriver till terminal
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    } 
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTournament(@PathVariable Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        
        if (tournament.isPresent()) {
            tournamentRepository.deleteById(id);
            return ResponseEntity.ok("Tournament deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tournament not found");
        }
    }

}
