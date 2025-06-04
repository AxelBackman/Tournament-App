package com.pvt.demo;

import com.pvt.demo.controller.TeamChatWebSocketController;
import com.pvt.demo.dto.TeamChatDto;
import com.pvt.demo.dto.TeamChatStompDto;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.TeamChat;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.TeamChatRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TeamChatWebSocketControllerTest {

    private TeamChatWebSocketController controller;
    private TeamChatRepository teamChatRepository;
    private TeamRepository teamRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        teamChatRepository = mock(TeamChatRepository.class);
        teamRepository = mock(TeamRepository.class);
        userRepository = mock(UserRepository.class);

        controller = new TeamChatWebSocketController();

        ReflectionTestUtils.setField(controller, "teamChatRepository", teamChatRepository);
        ReflectionTestUtils.setField(controller, "teamRepository", teamRepository);
        ReflectionTestUtils.setField(controller, "userRepository", userRepository);
    }

    @Test
    void sendMessage_shouldReturnCorrectDto() {
        Long teamId = 1L;
        Long userId = 2L;

        TeamChatStompDto inputDto = new TeamChatStompDto();
        inputDto.senderId = userId;
        inputDto.message = "Hej laget!";

        User mockUser = new User();
        ReflectionTestUtils.setField(mockUser, "id", userId);
        mockUser.setName("Anna");

        Team mockTeam = new Team();
        mockTeam.setId(teamId);
        mockTeam.setMembers(Collections.singletonList(mockUser));

        TeamChat mockChat = new TeamChat("Hej laget!", mockUser, mockTeam);
        
        ReflectionTestUtils.setField(mockChat, "id", 10L);
        mockChat.setTimestamp(LocalDateTime.of(2024, 5, 20, 15, 30));

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(teamChatRepository.save(any(TeamChat.class))).thenReturn(mockChat);

        TeamChatDto result = (TeamChatDto) controller.sendMessage(teamId, inputDto);

        assertNotNull(result);
        assertEquals(10L, result.id);
        assertEquals("Hej laget!", result.message);
        assertEquals(mockChat.getTimestamp(), result.timestamp);
        assertEquals(userId, result.senderId);
        assertEquals("Anna", result.senderName);

        ArgumentCaptor<TeamChat> captor = ArgumentCaptor.forClass(TeamChat.class);
        verify(teamChatRepository).save(captor.capture());

        TeamChat savedChat = captor.getValue();
        assertEquals("Hej laget!", savedChat.getMessage());
        assertEquals(mockUser, savedChat.getSender());
        assertEquals(mockTeam, savedChat.getTeam());
    }
}
