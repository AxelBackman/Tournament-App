package com.pvt.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pvt.demo.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    
}
