package com.pvt.demo.controller;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.pvt.demo.dto.ErrorDto;
import com.pvt.demo.dto.TeamChatDto;
import com.pvt.demo.dto.TeamChatStompDto;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.TeamChat;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.TeamChatRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Controller
public class TeamChatWebSocketController {

    @Autowired
    private TeamChatRepository teamChatRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @MessageMapping("/teamchat/send/{teamId}")
    @SendTo("/topic/teamchat/{teamId}")
    public TeamChatDto  sendMessage(@DestinationVariable Long teamId, TeamChatStompDto dto) {
        try {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new RuntimeException("Team not found"));

            User sender = userRepository.findById(dto.senderId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            team.getMembers().size();

            Collection<User> members = team.getMembers();
            if (members.stream().noneMatch(u -> u.getId().equals(dto.senderId))) {
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
        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
            return null; // Rethrow the exception to indicate failure
        }
    }
}