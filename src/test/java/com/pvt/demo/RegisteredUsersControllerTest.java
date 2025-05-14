package com.pvt.demo;

import com.pvt.demo.controller.RegisteredUsersController;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RegisteredUsers;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RegisteredUsersRepository;
import com.pvt.demo.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventInstanceRepository.findById(100L)).thenReturn(Optional.of(event));
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L)).thenReturn(null);
        when(registeredUsersRepository.save(any(RegisteredUsers.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/registeredusers/register/1/100/true"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Alice registered for event 100 with coming status: Coming"));
    }

    @Test
    void testRegisterUserToEvent_alreadyRegistered() throws Exception {
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L))
                .thenReturn(new RegisteredUsers());

        mockMvc.perform(post("/registeredusers/register/1/100/true"))
                .andExpect(status().isOk())
                .andExpect(content().string("User is already registered for this event"));
    }

    @Test
    void testDeleteUserFromEvent_success() throws Exception {
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L))
                .thenReturn(new RegisteredUsers());

        mockMvc.perform(delete("/registeredusers/delete/1/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("User 1 unregistered from event 100"));
    }

    @Test
    void testDeleteUserFromEvent_notFound() throws Exception {
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L)).thenReturn(null);

        mockMvc.perform(delete("/registeredusers/delete/1/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("User is not registered for this event"));
    }

    @Test
    void testGetAllRegisteredUsers() throws Exception {
        when(registeredUsersRepository.findByEventInstanceId(100L))
                .thenReturn(List.of(new RegisteredUsers(), new RegisteredUsers()));

        mockMvc.perform(get("/registeredusers/allregistered/100"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllRegisteredUser() throws Exception {
        when(registeredUsersRepository.findByUserId(1L))
                .thenReturn(List.of(new RegisteredUsers(), new RegisteredUsers()));

        mockMvc.perform(get("/registeredusers/allregistereduser/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateComingStatus_success() throws Exception {
        RegisteredUsers reg = Mockito.mock(RegisteredUsers.class);
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L)).thenReturn(reg);

        mockMvc.perform(patch("/registeredusers/updatecoming/1/100/false"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coming status updated to Interested for user 1 in event 100"));
    }

    @Test
    void testUpdateComingStatus_notRegistered() throws Exception {
        when(registeredUsersRepository.findByUserIdAndEventInstanceId(1L, 100L)).thenReturn(null);

        mockMvc.perform(patch("/registeredusers/updatecoming/1/100/true"))
                .andExpect(status().isOk())
                .andExpect(content().string("User is not registered for this event"));
    }
}
