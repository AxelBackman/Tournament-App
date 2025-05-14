package com.pvt.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.Match; 
import com.pvt.demo.model.OneTimeEvent;
import com.pvt.demo.model.Organisation;
import com.pvt.demo.repository.MatchRepository; 
import com.pvt.demo.repository.OneTimeEventRepository;
import com.pvt.demo.repository.OrganisationRepository;
import com.pvt.demo.util.BracketGenerator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/onetimeevents")
@CrossOrigin
public class OneTimeEventController {

    @Autowired
    private OneTimeEventRepository oneTimeEventRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private MatchRepository matchRepository; 

    
    @GetMapping
    public List<OneTimeEvent> getAll() {
        return oneTimeEventRepository.findAll();
    }

    
    @PostMapping("/create/{orgId}")
    public String createEvent(@PathVariable Long orgId, @RequestBody OneTimeEvent event) {
        Organisation org = organisationRepository.findById(orgId).orElse(null);
        if (org == null) return "Organization not found";

        event.setOrganisation(org);
        oneTimeEventRepository.save(event);
        return "OneTimeEvent created successfully with ID: " + event.getId();
    }

    
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {
        if (!oneTimeEventRepository.existsById(id)) {
            return "Event not found";
        }
        oneTimeEventRepository.deleteById(id);
        return "Event deleted";
    }

    
    @PutMapping("/update/{id}")
    public String updateEvent(@PathVariable Long id, @RequestBody OneTimeEvent updatedEvent) {
        return oneTimeEventRepository.findById(id).map(event -> {
            event.setName(updatedEvent.getName());
            event.setStartTime(updatedEvent.getStartTime());
            event.setEndTime(updatedEvent.getEndTime());
            event.setLocation(updatedEvent.getLocation());
            event.setTeamSize(updatedEvent.getTeamSize());
            oneTimeEventRepository.save(event);
            return "Event updated successfully";
        }).orElse("Event not found");
    }

    
    @GetMapping("/search")
    public List<OneTimeEvent> searchByName(@RequestParam String name) {
        return oneTimeEventRepository.findByNameContainingIgnoreCase(name);
    }

    
    @GetMapping("/brackets/{eventId}")
    public List<Match> getBrackets(@PathVariable Long eventId) {
        OneTimeEvent event = oneTimeEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        return event.getMatches();
    }

    
    @PostMapping("/brackets/generate/{eventId}")
    public List<Match> generateBrackets(@PathVariable Long eventId) {
        OneTimeEvent event = oneTimeEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        BracketGenerator generator = new BracketGenerator();
        List<Match> matches = generator.generateFirstRound(event);

        event.setMatches(matches);
        oneTimeEventRepository.save(event);

        return matches;
    }
}