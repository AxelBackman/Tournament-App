package com.pvt.demo.services;


import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventInstanceService {

    @Autowired
    private RecurringEventRepository recurringEventRepository;

    @Autowired
    private EventInstanceRepository eventInstanceRepository;

    // Skapar en EventInstance kopplad till en RecurringEvent
    public EventInstance addInstance(Long parentId) {
        RecurringEvent parentEvent = recurringEventRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent event with ID " + parentId + " not found"));

        EventInstance instance = new EventInstance();
        instance.setParentEvent(parentEvent);
        return eventInstanceRepository.save(instance);
    }

    //Skapar EventInstance utan koppling till RecurringEvent
    public EventInstance addSoloInstance(String title, String description, LocalDateTime start, LocalDateTime end, String location, int teamSize) {
        EventInstance instance = new EventInstance(title, description, start, end, location);
        return eventInstanceRepository.save(instance);
    }

    public EventInstance addInstanceWithParent(RecurringEvent parentEvent, String title, String description, LocalDateTime start, LocalDateTime end, String location, int teamSize) {
        EventInstance instance = new EventInstance(parentEvent, title, description, start, end, location);
        return eventInstanceRepository.save(instance);
    }
}
