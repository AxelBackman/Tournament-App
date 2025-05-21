package com.pvt.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.services.TournamentService;

public class TournamentScheduler {

    @Autowired
    private EventInstanceRepository eventRepository;

    @Autowired
    private TournamentService tournamentService;

    @Scheduled(cron = "0 0 2 * * ?") // varje natt kl 02:00
    public void createTournamentsForTomorrow() {
        LocalDateTime startOfDay = LocalDate.now().plusDays(1).atStartOfDay(); // 00:00
        LocalDateTime endOfDay = startOfDay.plusDays(1); // Nästa dag 00:00

        List<EventInstance> events = eventRepository.findByStartDateBetweenAndTournamentCreatedFalse(startOfDay, endOfDay);

        for (EventInstance eventInstance : events) {
            if (eventInstance.getTournament() != null) {
                tournamentService.createTournamentForEvent(eventInstance);
                eventRepository.save(eventInstance);
            }
            
        }
    }
    
}
