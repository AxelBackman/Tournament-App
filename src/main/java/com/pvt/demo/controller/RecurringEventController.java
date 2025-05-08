package com.pvt.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.RecurringEventRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/recurringevents")
@CrossOrigin()
public class RecurringEventController {
    
    @Autowired
    private RecurringEventRepository recurringEventRepository;

    @GetMapping("/allrecurring")
    public List<RecurringEvent> getAllRecurringEvents() {
        return recurringEventRepository.findAll();
    }
    
    @PostMapping("/addrecurring/{name}/{description}")
    public String addRecurringEvent(@PathVariable String name, @PathVariable String description) {
        RecurringEvent event = new RecurringEvent();
        event.setName(name);
        event.setDescription(description);
        recurringEventRepository.save(event);
        return "Recurring event added: " + event.getName();
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

    @DeleteMapping("/deletallrecurring")
    public String deleteAllRecurringEvents() {
        recurringEventRepository.deleteAll();
        return "All recurring events deleted";
    }

}
