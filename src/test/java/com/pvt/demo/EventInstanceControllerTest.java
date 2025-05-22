package com.pvt.demo;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.demo.controller.EventInstanceController;
import com.pvt.demo.dto.EventInstanceDto;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;
import com.pvt.demo.repository.UserRepository;
import com.pvt.demo.services.EventInstanceService;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.containsString;

import java.util.Optional; 


@WebMvcTest(EventInstanceController.class)
public class EventInstanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @Autowired
    private RecurringEventRepository recurringEventRepository;

    @Autowired
    private UserRepository userRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public EventInstanceRepository eventInstanceRepository() {
            return Mockito.mock(EventInstanceRepository.class);
        }

        @Bean
        public EventInstanceService eventInstanceService() {
            return Mockito.mock(EventInstanceService.class);
        }

        @Bean
        public RecurringEventRepository recurringEventRepository() {
            return Mockito.mock(RecurringEventRepository.class);
        }

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }

    @Test
    public void testGetAllInstances() throws Exception {
        EventInstance instance = new EventInstance("Test Title", "Test Desc", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Stockholm", 3);
        Mockito.when(eventInstanceRepository.findAll()).thenReturn(List.of(instance));

        mockMvc.perform(get("/eventinstances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test Desc"));
    }

    @Test
    public void testCreateSoloEventInstance() throws Exception {
        EventInstanceDto dto = new EventInstanceDto();
        dto.title = "Title";
        dto.description = "Desc";
        dto.startTime = "2025-06-01T10:00";
        dto.endTime = "2025-06-01T12:00";
        dto.location = "Gbg";
        dto.teamSize = 3;
        dto.userId = 1L;

        User mockUser = Mockito.mock(User.class);
        ReflectionTestUtils.setField(mockUser, "id", 1L);
        Mockito.when(mockUser.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        EventInstance mockInstance = new EventInstance(dto.title, dto.description, LocalDateTime.parse(dto.startTime), LocalDateTime.parse(dto.endTime), dto.location, dto.teamSize);
        ReflectionTestUtils.setField(mockInstance, "id", 1L);

       Mockito.when(eventInstanceRepository.save(any(EventInstance.class)))
       .thenAnswer(invocation -> {
           EventInstance saved = invocation.getArgument(0);
           ReflectionTestUtils.setField(saved, "id", 1L);
           return saved;
       });

        mockMvc.perform(post("/eventinstances/create")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("EventInstance created with ID: 1")));
    }

   @Test
    public void testCreateInstanceWithParent_success() throws Exception {
        RecurringEvent parent = new RecurringEvent();
        ReflectionTestUtils.setField(parent, "id", 100L);
        parent.setName("Weekly Training");

        EventInstanceDto dto = new EventInstanceDto();
        dto.title = "Title";
        dto.description = "Desc";
        dto.startTime = "2025-06-01T10:00";
        dto.endTime = "2025-06-01T12:00";
        dto.location = "Field";
        dto.recurringEventId = 100L;
        dto.teamSize = 3;
        dto.userId = 1L;
        
        User adminUser = Mockito.mock(User.class); // ✔ korrekt mock
        ReflectionTestUtils.setField(adminUser, "id", 1L);
        Mockito.when(adminUser.isAdmin()).thenReturn(true); // ✔ fungerar nu

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        Mockito.when(recurringEventRepository.findById(100L)).thenReturn(Optional.of(parent));

        Mockito.when(eventInstanceRepository.save(any(EventInstance.class)))
            .thenAnswer(invocation -> {
                EventInstance saved = invocation.getArgument(0);
                ReflectionTestUtils.setField(saved, "id", 2L);
                return saved;
            });

        mockMvc.perform(post("/eventinstances/create")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("EventInstance created with ID: 2")));
    }

    @Test
    public void testUpdateEventInstance() throws Exception {
        EventInstance existing = new EventInstance("Old", "Old", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "OldPlace", 4);
        ReflectionTestUtils.setField(existing, "id", 1L);

        EventInstanceDto dto = new EventInstanceDto();
        dto.title = "Updated Title";
        dto.description = "Updated Desc";
        dto.startTime = "2025-06-01T14:00";
        dto.endTime = "2025-06-01T16:00";
        dto.location = "NewPlace";
        dto.teamSize = 4;
        dto.userId = 1L;

        User adminUser = Mockito.mock(User.class);
        ReflectionTestUtils.setField(adminUser, "id", 1L);
        Mockito.when(adminUser.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
        
        Mockito.when(eventInstanceRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(eventInstanceRepository.save(any(EventInstance.class))).thenReturn(existing);

        mockMvc.perform(put("/eventinstances/update/1")
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("EventInstance updated with ID: 1")));
    }
}
