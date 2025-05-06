package com.pvt.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pvt.demo.model.Organisation;

import java.util.List;
import java.util.Optional;



public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    Optional<Organisation> findByName(String name);

    List<Organisation> findByNameContainingIgnoreCase(String name);

    List<Organisation> findByMembers_Id(Long userId);

    List<Organisation> findByRecurringEvents_Id(Long eventId);

    List<Organisation> findByAdressContainingIgnoreCase(String adress);

    List<Organisation> findByDescriptionContainingIgnoreCase(String description);
}
