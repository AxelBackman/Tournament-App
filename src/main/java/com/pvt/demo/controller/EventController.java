package com.pvt.demo.controller;

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
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/events")
@CrossOrigin
public class EventController {
    @Autowired
    private EventInstanceRepository eventInstanceRepository;
    
    @Autowired
    private RecurringEventRepository recurringEventRepository;

    @GetMapping("/allReccurring")
    public String getAllRecurringEvents() {
        return recurringEventRepository.findAll().toString();
    }

    @GetMapping("/allInstances")
    public String getAllEventInstances() {
        return eventInstanceRepository.findAll().toString();
    }

    @GetMapping("/addRecurring/{name}/{description}")
    public String addRecurringEvent(@PathVariable String name, @PathVariable String description) {
        RecurringEvent event = new RecurringEvent();
        event.setName(name);
        event.setDescription(description);
        recurringEventRepository.save(event);
        return "Recurring event added: " + event.getName();
    }

    @GetMapping("/addInstance/{parentId}")
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
