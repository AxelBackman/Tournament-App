package com.pvt.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pvt.demo.model.TeamChat;
import com.pvt.demo.model.Team;

@Repository
public interface TeamChatRepository extends JpaRepository<TeamChat, Long> {
    //Hämta 10 senaste meddelandena för ett visst lag
    List<TeamChat> findTop10ByTeamOrderByTimestampDesc(Team team);
}
