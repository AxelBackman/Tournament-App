package com.pvt.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pvt.demo.model.GameGroup;

public interface GameGroupRepository extends JpaRepository<GameGroup, Long> {

    
}