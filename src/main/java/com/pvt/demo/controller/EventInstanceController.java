package com.pvt.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/eventinstances")
@CrossOrigin(origins = "*")
public class EventInstanceController {

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @Autowired
    private RecurringEventRepository recurringEventRepository;

    // Skapa ny eventInstance kopplad till en RecurringEvent
    @PostMapping("/add/{recurringEventId}")
    public String CreateEventInstance(@PathVariable Long recurringEventId, @RequestBody EventInstance eventInstance) {
        Optional<RecurringEvent> recurringEventOpt = recurringEventRepository.findById(recurringEventId); 
        if (recurringEventOpt.isEmpty()) {
            return "Recurring event not found";
        }
        
        RecurringEvent recurringEvent = recurringEventOpt.get();
        eventInstance.setParentEvent(recurringEvent);
        eventInstanceRepository.save(eventInstance);
        return "Event instance created successfully with ID: " + eventInstance.getId();
    }

    // Hämta alla
    @GetMapping
    public List<EventInstance> getAllInstances() {
        return eventInstanceRepository.findAll();
    }

    // Hämta en specifik
    @GetMapping("/{id}")
    public EventInstance getInstanceById(@PathVariable Long id) {
        return eventInstanceRepository.findById(id).orElse(null);
    }

     // Radera en instans
    @DeleteMapping("/{id}")
    public String deleteInstance(@PathVariable Long id) {
        if (!eventInstanceRepository.existsById(id)) {
            return "EventInstance not found";
        }
        eventInstanceRepository.deleteById(id);
        return "EventInstance deleted";
    }
    
}
