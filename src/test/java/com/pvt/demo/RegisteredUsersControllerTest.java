package com.pvt.demo;

import com.pvt.demo.controller.RegisteredUsersController;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RegisteredUsers;
import com.pvt.demo.model.RegistrationStatus;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RegisteredUsersRepository;
import com.pvt.demo.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.lang.reflect.Field;
import java.time.LocalDateTime;

@WebMvcTest(RegisteredUsersController.class)
public class RegisteredUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisteredUsersRepository registeredUsersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public RegisteredUsersRepository registeredUsersRepository() {
            return Mockito.mock(RegisteredUsersRepository.class);
        }

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public EventInstanceRepository eventInstanceRepository() {
            return Mockito.mock(EventInstanceRepository.class);
        }
    }

    @Test
    void testRegisterUserToEvent_success() throws Exception {
        User user = new User();
        user.setName("Alice");

        EventInstance event = new EventInstance();
        setField(event, "id", 100L); // 🛠 Sätt id med reflection

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventInstanceRepository.findById(100L)).thenReturn(Optional.of(event));
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L)).thenReturn(null);
        when(registeredUsersRepository.save(any(RegisteredUsers.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/registeredusers/register/1/100/COMING"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Alice registered for event 100 with status: COMING"));
    }

    // 🔧 Hjälpmetod för att sätta privata fält
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testUpdateComingStatus_success_toInterested() throws Exception {
        RegisteredUsers reg = Mockito.mock(RegisteredUsers.class);
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L)).thenReturn(reg);

        mockMvc.perform(patch("/registeredusers/updatecoming/1/100/INTERESTED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coming status updated to INTERESTED for user 1 in event 100"));
    }


    @Test
    void testRegisterUserToEvent_alreadyRegistered() throws Exception {
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L))
                .thenReturn(new RegisteredUsers());

        mockMvc.perform(post("/registeredusers/register/1/100/COMING"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User is already registered for this event"));
    }

    @Test
    void testDeleteUserFromEvent_success() throws Exception {
        List<RegisteredUsers> mockRegistrations = List.of(new RegisteredUsers());

        when(registeredUsersRepository.findAllByUserIdAndEventInstanceId(1L, 100L))
            .thenReturn(mockRegistrations);

       mockMvc.perform(delete("/registeredusers/delete/1/100"))
            .andExpect(status().isOk())
            .andExpect(content().string("User 1 unregistered from event 100"));
    }

    @Test
    void testDeleteUserFromEvent_notFound() throws Exception {
        when(registeredUsersRepository.findAllByUserIdAndEventInstanceId(1L, 100L))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(delete("/registeredusers/delete/1/100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User is not registered for this event"));
    }

    @Test
    void testGetAllRegisteredUsers() throws Exception {
        // Mock User 1
        User mockUser1 = Mockito.mock(User.class);
        when(mockUser1.getId()).thenReturn(1L);
        when(mockUser1.getName()).thenReturn("Alice");

        // Mock User 2
        User mockUser2 = Mockito.mock(User.class);
        when(mockUser2.getId()).thenReturn(2L);
        when(mockUser2.getName()).thenReturn("Bob");

        // Mock EventInstance
        EventInstance mockEvent = Mockito.mock(EventInstance.class);
        when(mockEvent.getId()).thenReturn(100L);
        when(mockEvent.getTitle()).thenReturn("Test Event");

        // Mock RegisteredUsers 1
        RegisteredUsers reg1 = Mockito.mock(RegisteredUsers.class);
        when(reg1.getUser()).thenReturn(mockUser1);
        when(reg1.getEventInstance()).thenReturn(mockEvent);
        when(reg1.getStatus()).thenReturn(RegistrationStatus.COMING);

        // Mock RegisteredUsers 2
        RegisteredUsers reg2 = Mockito.mock(RegisteredUsers.class);
        when(reg2.getUser()).thenReturn(mockUser2);
        when(reg2.getEventInstance()).thenReturn(mockEvent);
        when(reg2.getStatus()).thenReturn(RegistrationStatus.INTERESTED);

        // Stub repository call
        when(registeredUsersRepository.findByEventInstanceId(100L))
                .thenReturn(List.of(reg1, reg2));

        mockMvc.perform(get("/registeredusers/allregistered/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].userName").value("Alice"))
                .andExpect(jsonPath("$[0].status").value("COMING"))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].userName").value("Bob"))
                .andExpect(jsonPath("$[1].status").value("INTERESTED"));
    }

    @Test
    void testGetAllRegisteredUser() throws Exception {
        // Mock User
        User user = Mockito.mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Test User");

        // Mock EventInstance
        EventInstance event1 = Mockito.mock(EventInstance.class);
        when(event1.getId()).thenReturn(10L);
        when(event1.getTitle()).thenReturn("Test Event 1");

        EventInstance event2 = Mockito.mock(EventInstance.class);
        when(event2.getId()).thenReturn(20L);
        when(event2.getTitle()).thenReturn("Test Event 2");

        // Mock RegisteredUsers
        RegisteredUsers reg1 = Mockito.mock(RegisteredUsers.class);
        when(reg1.getUser()).thenReturn(user);
        when(reg1.getEventInstance()).thenReturn(event1);
        when(reg1.getStatus()).thenReturn(RegistrationStatus.COMING);

        RegisteredUsers reg2 = Mockito.mock(RegisteredUsers.class);
        when(reg2.getUser()).thenReturn(user);
        when(reg2.getEventInstance()).thenReturn(event2);
        when(reg2.getStatus()).thenReturn(RegistrationStatus.INTERESTED);

        // Mock repo
        when(registeredUsersRepository.findByUserId(1L))
                .thenReturn(List.of(reg1, reg2));

        mockMvc.perform(get("/registeredusers/allregistereduser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].userName").value("Test User"))
                .andExpect(jsonPath("$[0].eventInstanceId").value(10))
                .andExpect(jsonPath("$[0].eventName").value("Test Event 1"))
                .andExpect(jsonPath("$[0].status").value("COMING"))
                .andExpect(jsonPath("$[1].eventInstanceId").value(20))
                .andExpect(jsonPath("$[1].eventName").value("Test Event 2"))
                .andExpect(jsonPath("$[1].status").value("INTERESTED"));
    }

    @Test
    void testUpdateComingStatus_success() throws Exception {
        RegisteredUsers reg = Mockito.mock(RegisteredUsers.class);
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L)).thenReturn(reg);

        mockMvc.perform(patch("/registeredusers/updatecoming/1/100/INTERESTED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coming status updated to INTERESTED for user 1 in event 100"));
    }

    
}
