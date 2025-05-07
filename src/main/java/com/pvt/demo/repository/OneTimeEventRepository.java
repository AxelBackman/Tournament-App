package com.pvt.demo.repository;

import com.pvt.demo.model.OneTimeEvent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OneTimeEventRepository extends JpaRepository<OneTimeEvent, Long> {
    //Sök bland alla events efter ett visst namn
    List<OneTimeEvent> findByNameContainingIgnoreCase(String name);
}
    


