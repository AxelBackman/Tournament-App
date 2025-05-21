package com.pvt.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.demo.controller.TournamentController;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.repository.EventInstanceRepository;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
    }

    @Test
    public void testGetTournamentById_found() throws Exception {
        EventInstance event = new EventInstance();
        ReflectionTestUtils.setField(event, "id", 100L);

        Tournament tournament = new Tournament(event, 4);
        ReflectionTestUtils.setField(tournament, "id", 1L);

        Mockito.when(tournamentRepository.findById(1L)).thenReturn(Optional.of(tournament));

        mockMvc.perform(get("/tournaments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamSize").value(4));
    }

    @Test
    public void testGetTournamentById_notFound() throws Exception {
        Mockito.when(tournamentRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tournaments/99"))
                .andExpect(status().isNotFound());
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

}
