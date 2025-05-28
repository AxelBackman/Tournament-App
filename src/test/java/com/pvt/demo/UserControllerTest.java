package com.pvt.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.demo.controller.UserController;
import com.pvt.demo.dto.UserDto;
import com.pvt.demo.model.Organisation;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.OrganisationRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

  
    @TestConfiguration
    static class Config {
        @Bean public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean public OrganisationRepository organisationRepository() {
            return Mockito.mock(OrganisationRepository.class);
        }

        @Bean public TeamRepository teamRepository() {
            return Mockito.mock(TeamRepository.class);
        }
    }

    @Test
    public void testGetUserById_found() throws Exception {
        Organisation org = new Organisation("Org", "Addr", "Desc");
        ReflectionTestUtils.setField(org, "id", 1L);
        User user = new User("Dave", "dave@example.com", org, true);
        ReflectionTestUtils.setField(user, "id", 42L);

        Mockito.when(userRepository.findById(42L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dave"))
                .andExpect(jsonPath("$.email").value("dave@example.com"))
                .andExpect(jsonPath("$.organisationId").value(1))
                .andExpect(jsonPath("$.organisationName").value("Org"))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    public void testGetUserById_notFound() throws Exception {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with ID 999 not found"));
    }

    @Test
    public void testGetUserByEmail_found() throws Exception {
        Organisation org = new Organisation("Org", "Addr", "Desc");
        ReflectionTestUtils.setField(org, "id", 1L);
        User user = new User("Eve", "eve@example.com", org, false);
        ReflectionTestUtils.setField(user, "id", 100L);

        Mockito.when(userRepository.findByEmail("eve@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/email/eve@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Eve"))
                .andExpect(jsonPath("$.email").value("eve@example.com"))
                .andExpect(jsonPath("$.organisationId").value(1))
                .andExpect(jsonPath("$.organisationName").value("Org"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    public void testGetUserByEmail_notFound() throws Exception {
        Mockito.when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/email/unknown@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User with email unknown@example.com not found"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        Organisation org = new Organisation("Org", "Addr", "Desc");
        User user = new User("Alice", "alice@example.com", org, false);
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    public void testAddNewUser_success() throws Exception {
        Organisation org = new Organisation("Org", "Addr", "Desc");
        ReflectionTestUtils.setField(org, "id", 1L);

        UserDto dto = new UserDto();
        dto.name = "Bob";
        dto.email = "bob@example.com";
        dto.organisationId = 1L;

        Mockito.when(organisationRepository.findById(1L)).thenReturn(Optional.of(org));
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/users/adduser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User 'Bob' saved successfully"));
    }

    @Test
    public void testUpdateUser_success() throws Exception {
        Organisation org = new Organisation("Org", "Addr", "Desc");
        ReflectionTestUtils.setField(org, "id", 1L);
        User user = new User("Old", "old@mail.com", org, false);
        ReflectionTestUtils.setField(user, "id", 10L);

        Organisation newOrg = new Organisation("NewOrg", "NewAddr", "NewDesc");
        ReflectionTestUtils.setField(newOrg, "id", 2L);

        UserDto dto = new UserDto();
        dto.name = "New Name";
        dto.email = "new@example.com";
        dto.organisationId = 2L;

        Mockito.when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        Mockito.when(organisationRepository.findById(2L)).thenReturn(Optional.of(newOrg));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(put("/users/updateuser/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User with ID 10 updated successfully"));
    }

    @Test
    public void testDeleteUser_success() throws Exception {
        Organisation org = new Organisation("Org", "Addr", "Desc");
        User user = new User("Charlie", "charlie@mail.com", org, false);
        ReflectionTestUtils.setField(user, "id", 5L);

        // Team team = new Team();
        // team.setName("My Team");
        // team.setMembers(List.copyOf(Set.of(user)));

        Mockito.when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        // Mockito.when(teamRepository.findByMembers_Id(5L)).thenReturn(List.of(team));

        mockMvc.perform(delete("/users/deleteuser/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("User with ID 5 deleted successfully"));
    }

    @Test
    public void testDeleteAllUsers() throws Exception {
        mockMvc.perform(delete("/users/deleteall"))
                .andExpect(status().isOk())
                .andExpect(content().string("All users deleted successfully"));
    }
}