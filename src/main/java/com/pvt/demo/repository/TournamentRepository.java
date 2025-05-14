package com.pvt.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pvt.demo.model.Tournament;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByEventInstanceId(Long eventInstanceId);
    List<Tournament> findByTeams_Members_Id(Long userId);
    List<Tournament> findByEventInstance_RecurringEvent_Id(Long recurringEventId);
}
