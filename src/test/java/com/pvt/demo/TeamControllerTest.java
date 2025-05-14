package com.pvt.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.demo.controller.TeamController;
import com.pvt.demo.dto.TeamDto;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.TournamentRepository;
import com.pvt.demo.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public TeamRepository teamRepository() {
            return Mockito.mock(TeamRepository.class);
        }

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public TournamentRepository tournamentRepository() {
            return Mockito.mock(TournamentRepository.class);
        }

        @Bean
        public EventInstanceRepository eventInstanceRepository() {
            return Mockito.mock(EventInstanceRepository.class);
        }
    }

    @Test
    void testCreateTeam_success() throws Exception {
        Tournament tournament = new Tournament();
        User user = new User();
        Team team = new Team(tournament, user, "TeamName");
        team.setId(123L); // mockat ID

        when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        TeamDto dto = new TeamDto();
        dto.name = "TeamName";
        dto.tournamentId = 1L;
        dto.userId = 2L;

        mockMvc.perform(post("/teams/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Team 'TeamName' created successfully with ID 123"));
    }

    @Test
    void testCreateTeam_tournamentOrUserNotFound() throws Exception {
        when(tournamentRepository.findById(1L)).thenReturn(Optional.empty());

        TeamDto dto = new TeamDto();
        dto.name = "TeamName";
        dto.tournamentId = 1L;
        dto.userId = 2L;

        mockMvc.perform(post("/teams/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Event instance or user not found"));
    }

    @Test
    void testAddMember_success() throws Exception {
        Team team = new Team();
        User user = new User();
        team.setMembers(new ArrayList<>());

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        mockMvc.perform(patch("/teams/addMember/10/20"))
                .andExpect(status().isOk())
                .andExpect(content().string("User added to team successfully"));
    }

    @Test
    void testAddMember_alreadyInTeam() throws Exception {
        User user = new User();
        List<User> members = new ArrayList<>();
        members.add(user);

        Team team = new Team();
        team.setMembers(members);

        when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/teams/addMember/10/20"))
                .andExpect(status().isOk())
                .andExpect(content().string("User already in team"));
    }

    @Test
    void testAddMember_teamOrUserNotFound() throws Exception {
        when(teamRepository.findById(10L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/teams/addMember/10/20"))
                .andExpect(status().isOk())
                .andExpect(content().string("Team or user not found"));
    }

    @Test
    void testGetAllTeams() throws Exception {
        when(teamRepository.findAll()).thenReturn(List.of(new Team(), new Team()));

        mockMvc.perform(get("/teams"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTeamsByEventInstance_success() throws Exception {
        EventInstance event = new EventInstance();
        when(eventInstanceRepository.findById(1L)).thenReturn(Optional.of(event));
        when(teamRepository.findByTournamentId(1L)).thenReturn(List.of(new Team()));

        mockMvc.perform(get("/teams/event/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTeamsByEventInstance_notFound() throws Exception {
        when(eventInstanceRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/teams/event/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
