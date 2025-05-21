package com.pvt.demo.model;

import java.time.LocalDate;
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
    public void createTournamentsForTomorrow(){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<EventInstance> events = eventRepository.findByStartDateAndTournamentCreatedFalse(tomorrow);


        for (EventInstance eventInstance : events){
            tournamentService.createTournamentForEvent(eventInstance);
            //eventInstance.setTournamentCreated(true);
            eventRepository.save(eventInstance);
            
        }
    }
    
}
