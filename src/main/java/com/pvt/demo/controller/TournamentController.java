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

import com.pvt.demo.dto.ResponseGameGroupDto;
import com.pvt.demo.dto.TournamentDto;
import com.pvt.demo.dto.TournamentResponseDto;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.Game;
import com.pvt.demo.model.GameGroup;
import com.pvt.demo.model.RegisteredForTournament;
import com.pvt.demo.model.RegistrationStatus;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.GameGroupRepository;
import com.pvt.demo.repository.GameRepository;
import com.pvt.demo.repository.RegisteredForTournamentRepository;
import com.pvt.demo.repository.TeamRepository;
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

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameGroupRepository gameGroupRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired 
    private RegisteredForTournamentRepository registeredForTournamentRepository;

    @Autowired
    private UserRepository userRepository;

    // Hämta tournament via ID
     // Hämta alla tournaments
    @GetMapping
    public ResponseEntity<List<TournamentResponseDto>> getAllTournaments() {
        try {
            List<Tournament> tournaments = tournamentRepository.findAll();
            List<TournamentResponseDto> dtos = tournaments.stream()
                .map(t -> new TournamentResponseDto(
                    t.getId(),
                    t.getEventInstance() != null ? t.getEventInstance().getId() : null,
                    t.getTeamSize()
                ))
                .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Hämta tournament via ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTournamentById(@PathVariable Long id) {
        try {
            Optional<Tournament> tournamentOpt = tournamentRepository.findById(id);
            if (tournamentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tournament not found");
            }
            Tournament t = tournamentOpt.get();
            TournamentResponseDto dto = new TournamentResponseDto(
                t.getId(),
                t.getEventInstance() != null ? t.getEventInstance().getId() : null,
                t.getTeamSize()
            );
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/gamegroups")
    public ResponseEntity<?> getGameGroupsForTournament(@PathVariable Long id) {
        try {
            Optional<Tournament> tournamentOpt = tournamentRepository.findById(id);
            if (tournamentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Tournament med id " + id + " hittades inte.");
            }

            Tournament tournament = tournamentOpt.get();

            if (tournament.getMap() == null) {
                return ResponseEntity.ok("Tournament hittades men innehåller ingen grupp (map är null).");
            }

            List<ResponseGameGroupDto> groupDtos = tournament.getMap().stream()
                    .map(ResponseGameGroupDto::new)
                    .toList();

            return ResponseEntity.ok(groupDtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internt fel vid hämtning av gamegroups: " + e.getClass().getSimpleName() + " – " + e.getMessage());
        }
    }

    //Registrera sig på tournaments
    @PostMapping("/register/{tournamentId}/{userId}/{status}")
    public ResponseEntity<?> registerUserToTournament(
        @PathVariable Long tournamentId,
        @PathVariable Long userId,
        @PathVariable RegistrationStatus status) {

        Optional<Tournament> tournamentOpt = tournamentRepository.findById(tournamentId);
        if (tournamentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tournament not found");
        }

        Optional<com.pvt.demo.model.User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (registeredForTournamentRepository.findByUserIdAndTournamentId(userId, tournamentId) != null) {
            return ResponseEntity.badRequest().body("User already registered for this tournament");
        }

        long currentCount = registeredForTournamentRepository.countByTournamentId(tournamentId);
        Tournament tournament = tournamentOpt.get();
        if (currentCount >= tournament.getMaxParticipants()) {
            return ResponseEntity.badRequest().body("Tournament is full");
        }

        RegisteredForTournament registration = new RegisteredForTournament(userOpt.get(), tournament, status);
        registeredForTournamentRepository.save(registration);

        return ResponseEntity.ok("User registered to tournament with status: " + status);
    }
    

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

            Tournament newTournament = new Tournament(eventInstance, teamSize, dto.maxParticipants);
            Tournament saved = tournamentRepository.save(newTournament);
            
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace(); // skriver till terminal
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }

    @PostMapping("/{tournamentId}/brackets")
    public ResponseEntity<?> registerTeamsAndGenerateBracket(@PathVariable Long tournamentId) {
        try {
            Optional<Tournament> tournamentOpt = tournamentRepository.findById(tournamentId);
            if (tournamentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tournament not found");
            }
            Tournament tournament = tournamentOpt.get();

            EventInstance eventInstance = tournament.getEventInstance();
            List<com.pvt.demo.model.User> users = eventInstance.getUsers();

            if (users == null || users.isEmpty()) {
                return ResponseEntity.badRequest().body("EventInstance must have users");
            }

            int teamSize = tournament.getTeamSize();
            if (users.size() % teamSize != 0) {
                return ResponseEntity.badRequest().body("Number of users must be evenly divisible by team size");
            }

            tournament.setTeams(); // skapar lag baserat på users och teamSize

            if (tournament.getTeams() == null || tournament.getTeams().size() % 4 != 0) {
                return ResponseEntity.badRequest().body("Number of teams must be divisible by 4 to create a bracket");
            }

            tournament.generateBracket();

            Tournament saved = tournamentRepository.save(tournament);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            e.printStackTrace();
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
            if (!team.equals(game.getTeamOne()) && !team.equals(game.getTeamTwo())) {
                return ResponseEntity.badRequest().body("Team is not participating in the selected game");
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
