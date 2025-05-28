package com.pvt.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pvt.demo.model.RegisteredForTournament;

public interface RegisteredForTournamentRepository extends JpaRepository<RegisteredForTournament, Long> {
    List<RegisteredForTournament> findByTournamentId(Long tournamentId);
    RegisteredForTournament findByUserIdAndTournamentId(Long userId, Long tournamentId);
    long countByTournamentId(Long tournamentId);
    
}
