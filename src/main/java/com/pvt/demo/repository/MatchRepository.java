package com.pvt.demo.repository;

import com.pvt.demo.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Game, Long> {
}