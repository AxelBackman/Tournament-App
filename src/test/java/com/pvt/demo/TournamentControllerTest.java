package com.pvt.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.demo.controller.TournamentController;
import com.pvt.demo.dto.TournamentDto;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RegisteredForTournament;
import com.pvt.demo.model.RegistrationStatus;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.GameGroupRepository;
import com.pvt.demo.repository.GameRepository;
import com.pvt.demo.repository.RegisteredForTournamentRepository;
import com.pvt.demo.repository.RegisteredUsersRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.TournamentRepository;
import com.pvt.demo.repository.UserRepository;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TournamentController.class)
@Import(TournamentControllerTest.Config.class)
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

        @Bean
        public RegisteredForTournamentRepository registeredForTournamentRepository() {
            return Mockito.mock(RegisteredForTournamentRepository.class);
        }

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public RegisteredUsersRepository registeredUsersRepository() {
            return Mockito.mock(RegisteredUsersRepository.class);
        }
    }

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @Autowired
    private RegisteredForTournamentRepository registeredForTournamentRepository;

    @Autowired
    private UserRepository userRepository;

    Tournament mockTournament;
    EventInstance mockEventInstance;
    User mockUser;

    @BeforeEach
    void setup() {
        mockUser = createUser(2L, "Mock User", "mockuser@example.com", true);

        mockEventInstance = createEventInstance(
            10L,
            "Mock Event",
            "Description for mock event",
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().plusHours(2),
            "Mock Location",
            2
        );

        mockTournament = createTournament(
            1L,
            "Mock Tournament",
            "Mock Game",
            LocalDateTime.now(),
            mockEventInstance,
            2,
            16,
            List.of(mockUser)
        );
    }

    private User createUser(Long id, String name, String email, boolean isAdmin) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        user.setName(name);
        user.setEmail(email);
        user.setAdmin(isAdmin); 
        return user;
    }

    private EventInstance createEventInstance(Long id, String title, String description,
                                          LocalDateTime start, LocalDateTime end, 
                                          String location, int teamSize) {
        EventInstance ei = new EventInstance();
        ReflectionTestUtils.setField(ei, "id", id);
        ei.setTitle(title);
        ei.setDescription(description);
        ei.setStartTime(start);
        ei.setEndTime(end);
        ei.setLocation(location);
        ei.setTeamSize(teamSize);
        return ei;
    }

    private Tournament createTournament(Long id, String name, String gameName, LocalDateTime startTime,
                                    EventInstance ei, int teamSize, int maxParticipants, List<User> users) {
        Tournament t = new Tournament(name, gameName, startTime, ei, teamSize, maxParticipants);
        ReflectionTestUtils.setField(t, "id", id);
        t.setAllGames(new ArrayList<>());
        t.setMap(new ArrayList<>());
        t.setTeams(new ArrayList<>());

        List<RegisteredForTournament> registrations = new ArrayList<>();
        for (User user : users) {
            RegisteredForTournament reg = new RegisteredForTournament(user, t, RegistrationStatus.COMING);
            registrations.add(reg);
        }
        ReflectionTestUtils.setField(t, "registeredUsers", registrations);

        return t;
    }


    @Test
    void getAllTournaments_shouldReturnOk() throws Exception {
        when(tournamentRepository.findAll()).thenReturn(List.of(mockTournament));

        mockMvc.perform(get("/tournaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockTournament.getId()));
    }

    @Test
    void getTournamentById_shouldReturnTournament() throws Exception {
        Tournament tournament = new Tournament("Test", "Game", LocalDateTime.now(), null, 2, 16);
        ReflectionTestUtils.setField(tournament, "id", 1L);

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        mockMvc.perform(get("/tournaments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createTournament_shouldCreateAndReturnTournament() throws Exception {
        EventInstance ei = new EventInstance();
        ReflectionTestUtils.setField(ei, "id", 10L);
        ei.setStartTime(LocalDateTime.now().minusHours(1));
        ei.setEndTime(LocalDateTime.now().plusHours(1));
        ei.setTeamSize(2);

        TournamentDto dto = new TournamentDto();
        dto.name = "Test";
        dto.gameName = "Game";
        dto.startTime = LocalDateTime.now();
        dto.eventInstanceId = 10L;
        dto.maxParticipants = 16;

        Tournament tournament = new Tournament(dto.name, dto.gameName, dto.startTime, ei, 2, dto.maxParticipants);
        ReflectionTestUtils.setField(tournament, "id", 1L);

        when(eventInstanceRepository.findById(10L)).thenReturn(Optional.of(ei));
        when(tournamentRepository.findByEventInstanceId(10L)).thenReturn(List.of());
        when(tournamentRepository.save(Mockito.any())).thenReturn(tournament);

        mockMvc.perform(post("/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void registerUserToTournament_shouldRegisterSuccessfully() throws Exception {
        long tournamentId = 1L;
        long userId = 2L;

        Tournament tournament = new Tournament("Test", "Game", LocalDateTime.now(), new EventInstance(), 2, 16);
        ReflectionTestUtils.setField(tournament, "id", tournamentId);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(registeredForTournamentRepository.findByUserIdAndTournamentId(userId, tournamentId)).thenReturn(null);
        when(registeredForTournamentRepository.countByTournamentId(tournamentId)).thenReturn(0L);

        mockMvc.perform(post("/tournaments/register/1/2/COMING"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("User registered")));
    }

    @Test
    void deleteTournament_shouldReturnOkIfExists() throws Exception {
        Tournament tournament = new Tournament("Test", "Game", LocalDateTime.now(), new EventInstance(), 2, 16);
        ReflectionTestUtils.setField(tournament, "id", 1L);
        tournament.setAllGames(new ArrayList<>());
        tournament.setMap(new ArrayList<>());
        tournament.setTeams(new ArrayList<>());

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        mockMvc.perform(delete("/tournaments/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Tournament deleted")));
    }
}
