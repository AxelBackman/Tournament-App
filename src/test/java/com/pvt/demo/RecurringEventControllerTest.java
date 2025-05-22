package com.pvt.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.demo.controller.RecurringEventController;
import com.pvt.demo.dto.RecurringEventDto;
import com.pvt.demo.model.Organisation;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.OrganisationRepository;
import com.pvt.demo.repository.RecurringEventRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecurringEventController.class)
public class RecurringEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecurringEventRepository recurringEventRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public RecurringEventRepository recurringEventRepository() {
            return Mockito.mock(RecurringEventRepository.class);
        }

        @Bean
        public OrganisationRepository organisationRepository() {
            return Mockito.mock(OrganisationRepository.class);
        }
    }

    @Test
    public void testGetAllRecurringEvents() throws Exception {
        RecurringEvent event = new RecurringEvent("Event Name", "Event Desc", new Organisation());
        Mockito.when(recurringEventRepository.findAll()).thenReturn(List.of(event));

        mockMvc.perform(get("/recurringevents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Event Desc"));
    }

    @Test
    public void testAddRecurringEvent_success() throws Exception {
        Organisation organisation = new Organisation();
        ReflectionTestUtils.setField(organisation, "id", 1L);

        RecurringEventDto dto = new RecurringEventDto();
        dto.name = "New Rec Event";
        dto.description = "Rec Event Description";
        dto.organisationId = 1L;

        Mockito.when(organisationRepository.findById(1L)).thenReturn(Optional.of(organisation));
        Mockito.when(recurringEventRepository.save(any(RecurringEvent.class))).thenReturn(new RecurringEvent());

        mockMvc.perform(post("/recurringevents/addrecurring")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Recurring event 'New Rec Event' added")));
    }

    @Test
    public void testAddRecurringEvent_organisationNotFound() throws Exception {
        RecurringEventDto dto = new RecurringEventDto();
        dto.name = "Failed Rec Event";
        dto.description = "Rec Event Description";
        dto.organisationId = 999L;

        Mockito.when(organisationRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/recurringevents/addrecurring")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Organisation with ID 999 not found")));
    }

    @Test
    public void testUpdateRecurringEvent_success() throws Exception {
        Organisation org = new Organisation();
        ReflectionTestUtils.setField(org, "id", 1L);

        RecurringEvent event = new RecurringEvent("Old Name", "Old Desc", org);
        ReflectionTestUtils.setField(event, "id", 5L);

        RecurringEventDto dto = new RecurringEventDto();
        dto.name = "Updated Name";
        dto.description = "Updated Desc";
        dto.organisationId = 1L;

        Mockito.when(recurringEventRepository.findById(5L)).thenReturn(Optional.of(event));
        Mockito.when(organisationRepository.findById(1L)).thenReturn(Optional.of(org));
        Mockito.when(recurringEventRepository.save(any(RecurringEvent.class))).thenReturn(event);

        mockMvc.perform(put("/recurringevents/updaterecurring/5")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Recurring event with ID 5 updated successfully")));
    }

    @Test
    public void testDeleteRecurringEvent_success() throws Exception {
        Organisation org = new Organisation();
        RecurringEvent event = new RecurringEvent("Event to delete", "Some desc", org);
        ReflectionTestUtils.setField(event, "id", 10L);

        Mockito.when(recurringEventRepository.findById(10L)).thenReturn(Optional.of(event));

        mockMvc.perform(delete("/recurringevents/deleterecurring/10"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Recurring event deleted: Event to delete")));
    }

    @Test
    public void testDeleteRecurringEvent_notFound() throws Exception {
        Mockito.when(recurringEventRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/recurringevents/deleterecurring/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Recurring event not found")));
    }
}