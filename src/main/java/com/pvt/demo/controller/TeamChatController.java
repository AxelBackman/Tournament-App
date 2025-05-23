package com.pvt.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.dto.TeamChatDto;
import com.pvt.demo.model.Team;
import com.pvt.demo.model.TeamChat;
import com.pvt.demo.model.User;
import com.pvt.demo.repository.TeamChatRepository;
import com.pvt.demo.repository.TeamRepository;
import com.pvt.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/teams/{teamId}/chat")
@CrossOrigin()
public class TeamChatController {

    @Autowired
    private TeamChatRepository teamChatRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getLatestMessages(@PathVariable Long teamId) {
        //Hämta laget, om det inte finns kasta fel
        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            return ResponseEntity.badRequest().body("Team with ID " + teamId + " not found");
        }
        //Hämta alla meddelanden
        List<TeamChat> messages = teamChatRepository.findByTeamOrderByTimestampAsc(team);

        //Till DTO
        List<TeamChatDto> dtos = messages.stream()
            .map(chat -> {
                TeamChatDto dto = new TeamChatDto();
                dto.id = chat.getId();
                dto.message = chat.getMessage();
                dto.timestamp = chat.getTimestamp();
                dto.senderId = chat.getSender().getId();
                dto.senderName = chat.getSender().getName();
                return dto;
            })
            .toList();
        
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @PathVariable Long teamId,
            @RequestParam Long userId,
            @RequestBody String messageText) {

        Team team = teamRepository.findById(teamId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        if (team == null || user == null) {
            return ResponseEntity.badRequest().body("Team or User not found");
        }

        //För att skicka meddelande måste user vara medlem i laget
        if (!team.getMembers().contains(user)) {
            return ResponseEntity.badRequest().body("User is not a member of this team.");
        }

        TeamChat message = new TeamChat(messageText, user, team);
        TeamChat saved = teamChatRepository.save(message);

        TeamChatDto dto = new TeamChatDto();
        dto.id = saved.getId();
        dto.message = saved.getMessage();
        dto.timestamp = saved.getTimestamp();
        dto.senderId = user.getId();
        dto.senderName = user.getName();
        
        return ResponseEntity.ok(dto);
    }
}

    
    
