package com.pvt.demo.services;

import org.springframework.stereotype.Service;

import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.Tournament;

@Service
public class TournamentService {

    public void createTournamentForEvent(EventInstance eventInstance){


        new Tournament(eventInstance, eventInstance.getTeamSize(), "grejer");
        
    }


    
}
