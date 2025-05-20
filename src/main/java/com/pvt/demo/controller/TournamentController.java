package com.pvt.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.pvt.demo.model.Tournament;
import com.pvt.demo.repository.TournamentRepository;

public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository;

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
    public Tournament createTournament(@RequestBody Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    
}
