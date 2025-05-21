package com.pvt.demo.repository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pvt.demo.model.EventInstance;

public interface EventInstanceRepository extends JpaRepository<EventInstance, Long> {
    List<EventInstance> findByStartDateAndTournamentCreatedFalse(LocalDate startTime);
}
