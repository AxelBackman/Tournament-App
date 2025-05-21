package com.pvt.demo.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.pvt.demo.dto.TeamChatDto;
import com.pvt.demo.dto.TeamChatStompDto;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.TeamChat;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.TeamChatRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.UserRepository;

@Controller
public class TeamChatWebSocketController {

    @Autowired
    private TeamChatRepository teamChatRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/teamchat/send/{teamId}")
    @SendTo("/topic/teamchat/{teamId}")
    public TeamChatDto sendMessage(@DestinationVariable Long teamId, TeamChatStompDto dto) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        User sender = userRepository.findById(dto.senderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!team.getMembers().contains(sender)) {
            throw new RuntimeException("User is not a member of this team.");
        }

        TeamChat message = new TeamChat(dto.message, sender, team);
        message.setTimestamp(LocalDateTime.now());

        TeamChat saved = teamChatRepository.save(message);

        TeamChatDto response = new TeamChatDto();
        response.id = saved.getId();
        response.message = saved.getMessage();
        response.timestamp = saved.getTimestamp();
        response.senderId = sender.getId();
        response.senderName = sender.getName();
        return response;
    }
}