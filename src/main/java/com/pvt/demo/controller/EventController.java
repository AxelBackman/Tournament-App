package com.pvt.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/events")
@CrossOrigin
public class EventController {
    @Autowired
    private EventInstanceRepository eventInstanceRepository;
    
    @Autowired
    private RecurringEventRepository recurringEventRepository;

    @GetMapping("/allrecurring")
    public List<RecurringEvent> getAllRecurringEvents() {
        return recurringEventRepository.findAll();
    }

    @GetMapping("/allinstances")
    public List<EventInstance> getAllEventInstances() {
        return eventInstanceRepository.findAll();
    }
    
    @GetMapping("/addrecurring/{name}/{description}")
    public String addRecurringEvent(@PathVariable String name, @PathVariable String description) {
        RecurringEvent event = new RecurringEvent();
        event.setName(name);
        event.setDescription(description);
        recurringEventRepository.save(event);
        return "Recurring event added: " + event.getName();
    }

    @GetMapping("/addinstance/{parentId}")
    public String addEventInstance(@PathVariable Integer parentId) {
        RecurringEvent parentEvent = recurringEventRepository.findById(parentId).orElse(null);
        if (parentEvent == null) {
            return "Parent event not found";
        }
        EventInstance instance = new EventInstance();
        instance.setParentEvent(parentEvent);
        eventInstanceRepository.save(instance);
        return "Event instance added of: " + parentEvent.getName();
    }
}
