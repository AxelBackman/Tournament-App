package com.pvt.demo.services;


import com.pvt.demo.model.EventInstance;
import com.pvt.demo.model.RecurringEvent;
import com.pvt.demo.repository.EventInstanceRepository;
import com.pvt.demo.repository.RecurringEventRepository;
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
}
