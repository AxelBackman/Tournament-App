package com.pvt.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.dto.EventInstanceDto;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/eventinstances")
@CrossOrigin() 
public class EventInstanceController {

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    @Autowired
    private RecurringEventRepository recurringEventRepository;

    // Skapa ny eventInstance med eller utan koppling till RecurringEvent
    @PostMapping("/create")
    public String createEventInstance(@RequestBody EventInstanceDto dto) {
        LocalDateTime start = LocalDateTime.parse(dto.startTime);
        LocalDateTime end = LocalDateTime.parse(dto.endTime);

        RecurringEvent parentEvent = null;
        if (dto.recurringEventId != null) {
            parentEvent = recurringEventRepository.findById(dto.recurringEventId).orElse(null);
            if (parentEvent == null) {
                return "Recurring event with ID " + dto.recurringEventId + " not found";
            }
        }

        EventInstance instance = new EventInstance(
            parentEvent,
            dto.title,
            dto.description,
            start,
            end,
            dto.location
        );

        eventInstanceRepository.save(instance);
        return "EventInstance created with ID: " + instance.getId();
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
    @PutMapping("/update/{id}")
    public String updateEventInstance(@PathVariable Long id, @RequestBody EventInstanceDto dto) {
        EventInstance instance = eventInstanceRepository.findById(id).orElse(null);
        if (instance == null) return "EventInstance with ID " + id + " not found";

        instance.setTitle(dto.title);
        instance.setDescription(dto.description);
        instance.setStartTime(LocalDateTime.parse(dto.startTime));
        instance.setEndTime(LocalDateTime.parse(dto.endTime));
        instance.setLocation(dto.location);

        if (dto.recurringEventId != null) {
            RecurringEvent recurring = recurringEventRepository.findById(dto.recurringEventId).orElse(null);
            if (recurring == null) return "Recurring event not found";
            instance.setParentEvent(recurring);
        } else {
            instance.setParentEvent(null);
        }

        eventInstanceRepository.save(instance);
        return "EventInstance updated with ID: " + id;
    }

    @GetMapping("/combinedEventsSorted")
    public List<Map<String, Object>> getCombinedEventsSorted() {
        List<EventInstance> allInstances = eventInstanceRepository.findAll();

        // Solo event instances (utan parent)
        List<Map<String, Object>> soloInstances = allInstances.stream()
            .filter(e -> e.getParentEvent() == null)
            .map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "solo");
                map.put("id", e.getId());
                map.put("title", e.getTitle());
                map.put("description", e.getDescription());
                map.put("startTime", e.getStartTime());
                map.put("endTime", e.getEndTime());
                return map;
            })
            .collect(Collectors.toList());

        // RecurringEvents med minst en EventInstance
        List<Map<String, Object>> recurringWithInstances = recurringEventRepository.findAll().stream()
            .filter(r -> !r.getSubEvents().isEmpty())
            .map(r -> {
                // För datum, ta första instansens startTime
                LocalDateTime earliestStart = r.getSubEvents().stream()
                    .map(EventInstance::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.MAX);

                Map<String, Object> map = new HashMap<>();
                map.put("type", "recurring");
                map.put("id", r.getId());
                map.put("name", r.getName());
                map.put("description", r.getDescription());
                map.put("startTime", earliestStart);
                return map;
            })
            .collect(Collectors.toList());

        // Kombinera och sortera
        List<Map<String, Object>> combined = new ArrayList<>();
        combined.addAll(soloInstances);
        combined.addAll(recurringWithInstances);

        combined.sort(Comparator.comparing(e -> (LocalDateTime) e.get("startTime")));

        return combined;
    }

}
