package com.pvt.demo.controller;


import java.util.Collections;
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
import com.pvt.demo.model.Game;
import com.pvt.demo.model.GameGroup;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.GameGroupRepository;
import com.pvt.demo.repository.GameRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.TournamentRepository;


@RestController
@RequestMapping("/tournaments")
@CrossOrigin() 
public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameGroupRepository gameGroupRepository;

    @Autowired
    private TeamRepository teamRepository;


    // Hämta tournament via ID
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getOrganisationById(@PathVariable Long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);
        return tournament.map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/gamegroups")
    public ResponseEntity<?> getGameGroupsForTournament(@PathVariable Long id) {
        Optional<Tournament> tournamentOpt = tournamentRepository.findById(id);
        if (tournamentOpt.isEmpty()) {
            System.out.println("Tournament med id " + id + " hittades inte!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tournament not found");
        }

        Tournament tournament = tournamentOpt.get();
        System.out.println("Tournament hittad: " + tournament);

        List<ResponseGameGroupDto> groupDtos = tournament.getMap() != null ? tournament.getMap()
                .stream()
                .map(ResponseGameGroupDto::new)
                .toList() : Collections.emptyList();

        System.out.println("GameGroups antal: " + groupDtos.size());
        return ResponseEntity.ok(groupDtos);
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

    

    @PostMapping("/{tournamentId}/games/{gameId}/winner/{teamId}")
    public ResponseEntity<?> setWinner(
            @PathVariable Long tournamentId,
            @PathVariable Long gameId,
            @PathVariable Long teamId) {
        try {
            Optional<Tournament> tournamentOpt = tournamentRepository.findById(tournamentId);
            if (tournamentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tournament not found");
            }
            Tournament tournament = tournamentOpt.get();

            Optional<Game> gameOpt = gameRepository.findById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game not found");
            }
            Game game = gameOpt.get();

            Optional<Team> teamOpt = teamRepository.findById(teamId);
            if (teamOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
            }
            Team team = teamOpt.get();

            // Validera att game och team hör till detta tournament
            if (!tournament.getAllGames().contains(game)) {
                return ResponseEntity.badRequest().body("Game does not belong to Tournament");
            }
            if (!tournament.getTeams().contains(team)) {
                return ResponseEntity.badRequest().body("Team does not belong to Tournament");
            }

            tournament.setWinner(game, team);
            tournamentRepository.save(tournament); 

            return ResponseEntity.ok("Winner set");

        } catch (Exception e) {
            e.printStackTrace(); // för loggning i terminalen
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error: " + e.getMessage());
        }
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTournament(@PathVariable Long id) {
        try {
            Optional<Tournament> optionalTournament = tournamentRepository.findById(id);
            if (optionalTournament.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tournament not found");
            }
            Tournament tournament = optionalTournament.get();

            EventInstance ei = tournament.getEventInstance();
            if (ei != null) {
                ei.setTournament(null);
                tournament.setEventInstance(null);
            }
           
            
            for (Game game : tournament.getAllGames()) {
                game.setTeamOne(null);
                game.setTeamTwo(null);
                game.setWinner(null);
                game.setLeft(null);
                game.setRight(null);
                game.setParent(null);
                game.setGameGroup(null);
                game.setTournament(null);
            }
            gameRepository.saveAll(tournament.getAllGames()); 

           
            for (GameGroup gg : tournament.getMap()) {
                gg.setTournament(null);
            }
            gameGroupRepository.saveAll(tournament.getMap());

            
            for (Team team : tournament.getTeams()) {
                team.setTournament(null);
            }
            teamRepository.saveAll(tournament.getTeams());
           
             
            tournament.getAllGames().clear();
            tournament.getMap().clear();
            tournament.getTeams().clear();

            tournamentRepository.delete(tournament);

            return ResponseEntity.ok("Tournament deleted");
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting tournament: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
