package com.pvt.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.services.EventInstanceService;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.CloseableThreadContext.Instance;
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
    private EventInstanceService eventInstanceService;

    @Autowired
    private RecurringEventRepository recurringEventRepository;

    // Skapa ny eventInstance utan koppling till RecurringEvent
    @PostMapping("/addSoloEvent/{description}/{startTime}/{endTime}/{location}/{teamSize}")
    public String CreateSoloEventInstance(
        @PathVariable String description, 
        @PathVariable String startTime, 
        @PathVariable String endTime,
        @PathVariable String location,
        @PathVariable int teamSize
    ) {
        
        // Konvertera startTime och endTime från String till LocalDateTime
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        
        EventInstance instance = eventInstanceService.addSoloInstance(description, start, end, location, teamSize);
        return "Solo EventInstance created with ID: " + instance.getId();   
    }

    //Skapa eventInstance med koppling till RecurringEvent
    @PostMapping("/addWithRecurring/{description}/{startTime}/{endTime}/{location}/{teamSize}/{recurringEventId}")
    public String createInstanceWithParent(
         @PathVariable String description,
        @PathVariable String startTime,
        @PathVariable String endTime,
        @PathVariable String location,
        @PathVariable int teamSize,
        @PathVariable Long recurringEventId
    ) {
        
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        RecurringEvent parentEvent = recurringEventRepository.findById(recurringEventId).orElse(null);
        if (parentEvent == null) {
            return "RecurringEvent med ID " + recurringEventId + " hittades inte.";
        }
        EventInstance instance = eventInstanceService.addInstanceWithParent(
        parentEvent, description, start, end, location, teamSize
    );
        
        return "EventInstance created with parent event: " + instance.getParentEvent().getName();
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
    @PutMapping("/update/{id}/{description}/{startTime}/{endTime}/{location}/{teamSize}")
    public String updateEventInstance(
        @PathVariable Long id,
        @PathVariable String description,
        @PathVariable String startTime,
        @PathVariable String endTime,
        @PathVariable String location,
        @PathVariable int teamSize) {

        // Hämta den existerande eventinstansen annars returnera misslyckande
        EventInstance instance = eventInstanceRepository.findById(id).orElse(null);
        if (instance == null) return "EventInstance not found";

        // Uppdatera de relevanta fälten (konvertera från String till LocalDateTime)
        instance.setDescription(description);
        instance.setStartTime(LocalDateTime.parse(startTime));
        instance.setEndTime(LocalDateTime.parse(endTime));
        instance.setLocation(location);
        instance.setTeamSize(teamSize);

        // Spara den uppdaterade eventinstansen
        eventInstanceRepository.save(instance);
        return "Event instance updated successfully with ID: " + instance.getId();
    }

}
