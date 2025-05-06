package com.pvt.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.events.Event;

import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;
import com.pvt.demo.services.EventInstanceService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*") // Allow all origins for testing purposes
public class EventController {
    @Autowired
    private EventInstanceRepository eventInstanceRepository;
    
    @Autowired
    private RecurringEventRepository recurringEventRepository;
    
    @Autowired
    private EventInstanceService eventInstanceService;

    @GetMapping("/allrecurring")
    public List<RecurringEvent> getAllRecurringEvents() {
        return recurringEventRepository.findAll();
    }

    @GetMapping("/allinstances")
    public List<EventInstance> getAllEventInstances() {
        return eventInstanceRepository.findAll();
    }
    
    @PostMapping("/addrecurring/{name}/{description}")
    public String addRecurringEvent(@PathVariable String name, @PathVariable String description) {
        RecurringEvent event = new RecurringEvent();
        event.setName(name);
        event.setDescription(description);
        recurringEventRepository.save(event);
        return "Recurring event added: " + event.getName();
    }

    @PostMapping("/addinstance/{parentId}")
    public String addEventInstance(@PathVariable Long parentId) {
        EventInstance instance = eventInstanceService.addInstance(parentId);;
        return "Event instance added of parent event: " + instance.getParentEvent().getName();
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

    @DeleteMapping("/deleteinstance/{id}")
    public String deleteEventInstance(@PathVariable Long id) {
        EventInstance instance = eventInstanceRepository.findById(id).orElse(null);
        if (instance == null) {
            return "Event instance not found";
        }
        eventInstanceRepository.delete(instance);
        return "Event instance deleted: " + instance.getParentEvent().getName();
    }

    @DeleteMapping("/deletallrecurring")
    public String deleteAllRecurringEvents() {
        recurringEventRepository.deleteAll();
        return "All recurring events deleted";
    }

}
