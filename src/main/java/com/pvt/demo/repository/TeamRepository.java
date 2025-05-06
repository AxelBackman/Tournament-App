package com.pvt.demo.repository;

import com.pvt.demo.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByEventInstanceId(Long eventinstanceId);
}
