package com.pvt.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pvt.demo.dto.EventInstanceDto;
import com.pvt.demo.dto.EventInstanceResponseDto;
import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.model.Tournament;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;
import com.pvt.demo.repository.TournamentRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private TournamentRepository tournamentRepository;

    // Skapa ny eventInstance med eller utan koppling till RecurringEvent
    @PostMapping("/create")
    public ResponseEntity<String> createEventInstance(@RequestBody EventInstanceDto dto) {
        LocalDateTime start = LocalDateTime.parse(dto.startTime);
        LocalDateTime end = LocalDateTime.parse(dto.endTime);

        RecurringEvent parentEvent = null;
        if (dto.recurringEventId != null) {
            parentEvent = recurringEventRepository.findById(dto.recurringEventId).orElse(null);
            if (parentEvent == null) {
                return ResponseEntity.badRequest().body("Recurring event with ID " + dto.recurringEventId + " not found");
            }
        }

        EventInstance instance = new EventInstance(
            parentEvent,
            dto.title,
            dto.description,
            start,
            end,
            dto.location,
            dto.teamSize
        );

        instance.setImageUrl(dto.imageUrl);

        eventInstanceRepository.save(instance);
        return ResponseEntity.ok("EventInstance created with ID: " + instance.getId());
    }

    private EventInstanceResponseDto toResponseDto(EventInstance instance) {
        EventInstanceResponseDto dto = new EventInstanceResponseDto();
        dto.id = instance.getId();
        dto.startTime = instance.getStartTime();
        dto.endTime = instance.getEndTime();
        dto.title = instance.getTitle();
        dto.description = instance.getDescription();
        dto.location = instance.getLocation();
        dto.teamSize = instance.getTeamSize();
        dto.imageUrl = instance.getImageUrl();
        dto.parentEventId = instance.getParentEventId();

        // TournamentId
        if (instance.getTournament() != null) {
            Long id = instance.getTournament().getId();
            dto.tournamentId = id;
        }

        return dto;
    }

    // Hämta alla
    @GetMapping
    public ResponseEntity<?> getAllInstances() {
        try {
            List<EventInstanceResponseDto> result = eventInstanceRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ett fel inträffade vid hämtning av eventinstanser.");
        }
    }


    // Hämta en specifik
    @GetMapping("/{id}")
    public ResponseEntity<?> getInstanceById(@PathVariable Long id) {
        try {
            Optional<EventInstance> optionalInstance = eventInstanceRepository.findById(id);
            if (optionalInstance.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("EventInstance with ID " + id + " not found.");
            }

            EventInstanceResponseDto dto = toResponseDto(optionalInstance.get());
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ett fel inträffade vid hämtning av eventinstansen.");
        }
    }

     // Radera en instans
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInstance(@PathVariable Long id) {
        try {
            Optional<EventInstance> optionalEventInstance = eventInstanceRepository.findById(id);
            if (optionalEventInstance.isEmpty()) {
                return ResponseEntity.badRequest().body("EventInstance not found");
            }

            EventInstance ei = optionalEventInstance.get();

            // Koppla bort från Tournament om sådan finns
            Tournament tournament = ei.getTournament();
            if (tournament != null) {
                tournament.setEventInstance(null);
                ei.setTournament(null);
                tournamentRepository.save(tournament); // Spara ändringen!
            }

            eventInstanceRepository.delete(ei);
            return ResponseEntity.ok("EventInstance deleted");

        } catch (Exception e) {
            e.printStackTrace(); // Bra under utveckling, ta ev. bort i produktion
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while deleting the event instance: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Updatera en instans
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateEventInstance(@PathVariable Long id, @RequestBody EventInstanceDto dto) {
        
        EventInstance instance = eventInstanceRepository.findById(id).orElse(null);
        if (instance == null) return ResponseEntity.badRequest().body("EventInstance with ID " + id + " not found");

        instance.setTitle(dto.title);
        instance.setDescription(dto.description);
        instance.setStartTime(LocalDateTime.parse(dto.startTime));
        instance.setEndTime(LocalDateTime.parse(dto.endTime));
        instance.setLocation(dto.location);
        instance.setTeamSize(dto.teamSize);
        instance.setImageUrl(dto.imageUrl);

        if (dto.recurringEventId != null) {
            RecurringEvent recurring = recurringEventRepository.findById(dto.recurringEventId).orElse(null);
            if (recurring == null) return ResponseEntity.badRequest().body("Recurring event not found");
            instance.setParentEvent(recurring);
        } else {
            instance.setParentEvent(null);
        }

        eventInstanceRepository.save(instance);
        return ResponseEntity.ok("EventInstance updated with ID: " + id);
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
