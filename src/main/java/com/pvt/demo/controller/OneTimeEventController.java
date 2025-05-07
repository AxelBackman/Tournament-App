package com.pvt.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.OneTimeEvent;
import com.pvt.demo.model.Organisation;
import com.pvt.demo.repository.OneTimeEventRepository;
import com.pvt.demo.repository.OrganisationRepository;
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
    private OrganisationRepository  organisationRepository;

    //Hämta alla OneTimeEvents
    @GetMapping
    public List<OneTimeEvent> getAll() {
        return oneTimeEventRepository.findAll();
    }

    //Skapa nytt OneTimeEvent och koppla till org via ID
    @PostMapping("/create/{orgId}")
    public String createEvent(@PathVariable Long orgId, @RequestBody OneTimeEvent event) {
        Organisation org = organisationRepository.findById(orgId).orElse(null);
        if (org == null) return "Organisation not found";

        event.setOrganisation(org);
        oneTimeEventRepository.save(event);
        return "OneTimeEvent created successfully with ID: " + event.getId();
    }

    //Radera ett OneTimeEvent via ID
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {
        if (!oneTimeEventRepository.existsById(id)) {
            return "Event not found";
        }
        oneTimeEventRepository.deleteById(id);
        return "Event deleted";
    }

    //Uppdatera ett OneTimeEvent via ID
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

    //Sök efter OneTimeEvents baserat på namn (ignore case)
    @GetMapping("/search")
    public List<OneTimeEvent> searchByName(@RequestParam String name) {
    return oneTimeEventRepository.findByNameContainingIgnoreCase(name);
}
    
    

}
