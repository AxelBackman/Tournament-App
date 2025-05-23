package com.pvt.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.dto.TournamentDto;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.TournamentRepository;

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
        if (dto.eventInstanceId == null) {
            return ResponseEntity.badRequest().body("EventInstanceId is required");
        }

        EventInstance eventInstance = eventInstanceRepository.findById(dto.eventInstanceId)
                .orElse(null);

        if (eventInstance == null) {
            return ResponseEntity.badRequest().body("EventInstance not found");
        }

        int teamSize = eventInstance.getTeamSize();  // Hämta teamSize från eventInstance

        Tournament newTournament = new Tournament(eventInstance, teamSize);

        Tournament saved = tournamentRepository.save(newTournament);
        return ResponseEntity.ok(saved);
    }

    
}
