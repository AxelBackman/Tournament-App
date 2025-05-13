package com.pvt.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.dto.RecurringEventDto;
import com.pvt.demo.model.Organisation;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.OrganisationRepository;
import com.pvt.demo.repository.RecurringEventRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    
    @PostMapping("/addrecurring")
    public String addRecurringEvent(@RequestBody RecurringEventDto dto) {
        Organisation organisation = organisationRepository.findById(dto.organisationId).orElse(null);
        if (organisation != null) {
            RecurringEvent recurringEvent = new RecurringEvent(dto.name, dto.description, organisation);
            recurringEventRepository.save(recurringEvent);
            return "Recurring event '" + dto.name + "' added";
        } else {
            return "Organisation with ID " + dto.organisationId + " not found";
        }
    }

    @PutMapping("/updaterecurring/{id}")
    public String updateRecurringEvent(@PathVariable Long id, @RequestBody RecurringEventDto dto) {
        RecurringEvent event = recurringEventRepository.findById(id).orElse(null);
        Organisation organisation = organisationRepository.findById(dto.organisationId).orElse(null);

        if (event != null && organisation != null) {
            event.setName(dto.name);
            event.setDescription(dto.description);
            event.setOrganisation(organisation);
            recurringEventRepository.save(event);
            return "Recurring event with ID " + id + " updated successfully";
        } else if (event == null) {
            return "Recurring event with ID " + id + " not found";
        } else {
            return "Organisation with ID " + dto.organisationId + " not found";
        }
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
