package com.pvt.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.Organisation;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.OrganisationRepository;
import com.pvt.demo.repository.RecurringEventRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/recurringevents")
@CrossOrigin()
public class RecurringEventController {
    
    @Autowired
    private RecurringEventRepository recurringEventRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @GetMapping
    public List<RecurringEvent> getAllRecurringEvents() {
        return recurringEventRepository.findAll();
    }
    
    @PostMapping("/addrecurring/{name}/{description}/{organisationId}")
    public String addRecurringEvent(
        @PathVariable String name, 
        @PathVariable String description,
        @PathVariable Long organisationId
    ) {
        Optional<Organisation> organisationOpt = organisationRepository.findById(organisationId);
        if (organisationOpt.isEmpty()) {
            return "Organisation not found";
        }

        Organisation organisation = organisationOpt.get();

        // Skapa ett nytt återkommande event och associera det med organisationen
        RecurringEvent event = new RecurringEvent(name, description);
        event.setOrganisation(organisation);
        recurringEventRepository.save(event);

        return "Recurring event added: " + event.getName();
    }

     @PutMapping("/updaterecurring/{id}/{name}/{description}/{organisationId}")
    public String updateRecurringEvent(
        @PathVariable Long id, 
        @PathVariable String name, 
        @PathVariable String description, 
        @PathVariable Long organisationId
    ) {
        Optional<RecurringEvent> recurringEventOpt = recurringEventRepository.findById(id);
        if (recurringEventOpt.isEmpty()) {
            return "Recurring event not found";
        }

        Optional<Organisation> organisationOpt = organisationRepository.findById(organisationId);
        if (organisationOpt.isEmpty()) {
            return "Organisation not found";
        }

        RecurringEvent recurringEvent = recurringEventOpt.get();
        Organisation organisation = organisationOpt.get();

        // Uppdatera de relevanta fälten
        recurringEvent.setName(name);
        recurringEvent.setDescription(description);
        recurringEvent.setOrganisation(organisation);

        recurringEventRepository.save(recurringEvent);

        return "Recurring event updated: " + recurringEvent.getName();
    }

    @DeleteMapping("/deleterecurring/{id}")
    public String deleteRecurringEvent(@PathVariable Long id) {
        RecurringEvent event = recurringEventRepository.findById(id).orElse(null);
        if (event == null) {
            return "Recurring event not found";
        }
        recurringEventRepository.delete(event);
        return "Recurring event deleted: " + event.getName();
    }


}
