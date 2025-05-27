package com.pvt.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.demo.controller.TournamentController;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.Game;
import com.pvt.demo.model.GameGroup;
import com.pvt.demo.model.RegisteredUsers;
import com.pvt.demo.model.RegistrationStatus;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.GameGroupRepository;
import com.pvt.demo.repository.GameRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TournamentController.class)
public class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;


    @TestConfiguration
    static class Config {
        @Bean
        public TournamentRepository tournamentRepository() {
            return Mockito.mock(TournamentRepository.class);
        }

        @Bean
        public EventInstanceRepository eventInstanceRepository() {
            return Mockito.mock(EventInstanceRepository.class);
        }

        @Bean
        public GameRepository gameRepository() {
            return Mockito.mock(GameRepository.class);
        }

        @Bean
        public GameGroupRepository gameGroupRepository() {
            return Mockito.mock(GameGroupRepository.class);
        }

        @Bean
        public TeamRepository teamRepository() {
            return Mockito.mock(TeamRepository.class);
        }
    }

    @Test
    public void testGetTournamentById_found() throws Exception {
        EventInstance event = new EventInstance();
        ReflectionTestUtils.setField(event, "id", 100L);

        Tournament tournament = new Tournament(event, 4, 16);
        ReflectionTestUtils.setField(tournament, "id", 1L);

        Mockito.when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        mockMvc.perform(get("/tournaments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamSize").value(4))
                .andExpect(jsonPath("$.maxParticipants").value(16));
    }

    @Test
    public void testGetTournamentById_notFound() throws Exception {
        Mockito.when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tournaments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateTournament_success() throws Exception {
        EventInstance eventInstance = new EventInstance();
        ReflectionTestUtils.setField(eventInstance, "id", 200L);
        eventInstance.setTeamSize(3);

    List<RegisteredUsers> registeredUsersList = new ArrayList<>();

    for (long i = 1; i <= 12; i++) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", i);
        user.setName("User " + i);

        RegisteredUsers ru = new RegisteredUsers(eventInstance, user, RegistrationStatus.COMING);
        registeredUsersList.add(ru);
    }

    eventInstance.setRegisteredUsers(registeredUsersList);

    Tournament savedTournament = new Tournament(eventInstance, 3, 16);
    ReflectionTestUtils.setField(savedTournament, "id", 10L);

    Mockito.when(eventInstanceRepository.findById(200L)).thenReturn(Optional.of(eventInstance));
    Mockito.when(tournamentRepository.save(any(Tournament.class))).thenReturn(savedTournament);

    String json = """
        {
            "eventInstanceId": 200,
            "maxParticipants": 16
        }
        """;

    mockMvc.perform(post("/tournaments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.teamSize").value(3));
    }


    @Test
    public void testCreateTournament_missingEventInstance() throws Exception {
        Tournament invalidTournament = new Tournament();
        invalidTournament.setTeamSize(2); // no eventInstance

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(invalidTournament);

        mockMvc.perform(post("/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateTournament_missingEventInstance_shouldReturnBadRequest() throws Exception {
        String invalidJson = """
            {
                "teamSize": 5,
                "allGames": [],
                "teams": []
            }
            """;

        mockMvc.perform(post("/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteTournament_success() throws Exception {
        // Mock turnering och dess relationer
        Tournament tournament = new Tournament();
        ReflectionTestUtils.setField(tournament, "id", 123L);

        // EventInstance
        EventInstance ei = new EventInstance();
        tournament.setEventInstance(ei);
        ei.setTournament(tournament);

        // Games
        Game game1 = new Game();
        game1.setTournament(tournament);
        Game game2 = new Game();
        game2.setTournament(tournament);
        List<Game> games = new ArrayList<>(List.of(game1, game2));
        tournament.setAllGames(games);

        // GameGroups
        GameGroup gg1 = new GameGroup();
        gg1.setTournament(tournament);
        List<GameGroup> groups = new ArrayList<>(List.of(gg1));
        tournament.setMap(groups);

        // Teams
        Team team1 = new Team("Team1");
        team1.setTournament(tournament);
        List<Team> teams = new ArrayList<>(List.of(team1));
        tournament.setTeams(teams);

        // Ställ in mock-beteende
        when(tournamentRepository.findById(123L)).thenReturn(Optional.of(tournament));

        mockMvc.perform(delete("/tournaments/123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Tournament deleted"));

        // Verifiera att relationer rensas och sparas/borttas
        assertTrue(tournament.getAllGames().isEmpty());
        assertTrue(tournament.getMap().isEmpty());
        assertTrue(tournament.getTeams().isEmpty());
        verify(tournamentRepository).delete(tournament);
        verify(tournamentRepository).findById(123L);
    }

    @Test
    public void testDeleteTournament_notFound() throws Exception {
        when(tournamentRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/tournaments/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tournament not found"));
    }

}
