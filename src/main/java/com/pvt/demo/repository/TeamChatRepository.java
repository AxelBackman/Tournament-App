package com.pvt.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pvt.demo.model.TeamChat;
import com.pvt.demo.model.Team;

@Repository
public interface TeamChatRepository extends JpaRepository<TeamChat, Long> {
    //Hämta alla meddelanden och sortera på äldst först
    List<TeamChat> findByTeamOrderByTimestampAsc(Team team);
}
