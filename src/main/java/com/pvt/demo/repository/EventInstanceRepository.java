package com.pvt.demo.repository;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pvt.demo.model.EventInstance;

public interface EventInstanceRepository extends JpaRepository<EventInstance, Long> {
    List<EventInstance> findByStartTimeBetweenAndTournamentCreatedFalse(LocalDateTime startTime, LocalDateTime end);
}
