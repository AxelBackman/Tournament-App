package com.pvt.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/eventinstances")
@CrossOrigin() 
public class EventInstanceController {

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @Autowired
    private RecurringEventRepository recurringEventRepository;

    // Skapa ny eventInstance kopplad till en RecurringEvent
    @PostMapping("/add/{recurringEventId}/{description}/{startTime}/{endTime}/{location}/{teamSize}")
    public String CreateEventInstance(
        @PathVariable Long recurringEventId,
        @PathVariable String description, 
        @PathVariable String startTime, 
        @PathVariable String endTime,
        @PathVariable String location,
        @PathVariable int teamSize
    ) {
        Optional<RecurringEvent> recurringEventOpt = recurringEventRepository.findById(recurringEventId); 
        if (recurringEventOpt.isEmpty()) {
            return "Recurring event not found";
        }

        RecurringEvent recurringEvent = recurringEventOpt.get();
        
        // Konvertera startTime och endTime från String till LocalDateTime
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        
        // Skapa en ny EventInstance med den angivna konstruktorn
        EventInstance eventInstance = new EventInstance(recurringEvent, description, start, end, location, teamSize);

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

    // Updatera en instans
    @PutMapping("/update/{id}/{startTime}/{endTime}/{location}/{teamSize}")
public String updateEventInstance(
    @PathVariable Long id,
    @PathVariable String startTime,
    @PathVariable String endTime,
    @PathVariable String location,
    @PathVariable int teamSize
) {
    Optional<EventInstance> eventInstanceOpt = eventInstanceRepository.findById(id);
    if (eventInstanceOpt.isEmpty()) {
        return "EventInstance not found";
    }

    // Hämta den existerande eventinstansen
    EventInstance eventInstance = eventInstanceOpt.get();

    // Konvertera startTime och endTime från String till LocalDateTime
    LocalDateTime start = LocalDateTime.parse(startTime);
    LocalDateTime end = LocalDateTime.parse(endTime);

    // Uppdatera de relevanta fälten
    eventInstance.setStartTime(start);
    eventInstance.setEndTime(end);
    eventInstance.setLocation(location);
    eventInstance.setTeamSize(teamSize);

    // Spara den uppdaterade eventinstansen
    eventInstanceRepository.save(eventInstance);
    return "Event instance updated successfully with ID: " + eventInstance.getId();
}

}
