package com.pvt.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pvt.demo.model.EventInstance;

public interface EventInstanceRepository extends JpaRepository<EventInstance, Long> {}
