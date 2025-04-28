package com.pvt.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pvt.demo.model.RecurringEvent;

public interface RecurringEventRepository extends JpaRepository<RecurringEvent, Integer> {}
