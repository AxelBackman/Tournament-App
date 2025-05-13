package com.pvt.demo;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.pvt.demo.controller.EventInstanceController;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;
import com.pvt.demo.services.EventInstanceService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.hamcrest.Matchers.containsString;

import java.util.Optional; 


@WebMvcTest(EventInstanceController.class)
public class EventInstanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventInstanceRepository eventInstanceRepository;

    @MockBean
    private EventInstanceService eventInstanceService;

    @MockBean
    private RecurringEventRepository recurringEventRepository;

    @Test
    public void testGetAllInstances() throws Exception {
        EventInstance instance = new EventInstance("Test Title", "Test Desc", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Stockholm", 5);
        Mockito.when(eventInstanceRepository.findAll()).thenReturn(List.of(instance));

        mockMvc.perform(get("/eventinstances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test Desc"));
    }

    @Test
    public void testCreateSoloEventInstance() throws Exception {
        EventInstance mockInstance = new EventInstance("Title", "Desc", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Gbg", 4);
        ReflectionTestUtils.setField(mockInstance, "id", 1L);


        Mockito.when(eventInstanceService.addSoloInstance(anyString(), anyString(), any(), any(), anyString(), anyInt()))
                .thenReturn(mockInstance);

        mockMvc.perform(post("/eventinstances/addSoloEvent/Title/Desc/2025-06-01T10:00/2025-06-01T12:00/Gbg/4"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Solo EventInstance created with ID: 1")));
    }

    @Test
    public void testCreateInstanceWithParent_success() throws Exception {
        RecurringEvent parent = new RecurringEvent();
        ReflectionTestUtils.setField(parent, "id", 100L);
        parent.setName("Weekly Training");

        EventInstance instance = new EventInstance(parent, "Title", "Desc", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "Field", 6);
        ReflectionTestUtils.setField(instance, "id", 2L);

        Mockito.when(recurringEventRepository.findById(100L)).thenReturn(Optional.of(parent));
        Mockito.when(eventInstanceService.addInstanceWithParent(any(), anyString(), anyString(), any(), any(), anyString(), anyInt()))
                .thenReturn(instance);

        mockMvc.perform(post("/eventinstances/addWithRecurring/Title/Desc/2025-06-01T10:00/2025-06-01T12:00/Field/6/100"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("EventInstance created with parent event: Weekly Training")));
    }

    @Test
    public void testDeleteEventInstance() throws Exception {
        Mockito.when(eventInstanceRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/eventinstances/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("EventInstance deleted"));
    }
}
