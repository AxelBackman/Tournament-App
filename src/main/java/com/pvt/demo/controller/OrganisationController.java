package com.pvt.demo.controller;

import com.pvt.demo.model.Organisation;
import com.pvt.demo.repository.OrganisationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/organisations")
public class OrganisationController {

    @Autowired
    private OrganisationRepository organisationRepository;

    // Hämta alla organisationer
    @GetMapping
    public List<Organisation> getAllOrganisations() {
        return organisationRepository.findAll();
    }

    // Hämta organisation via ID
    @GetMapping("/{id}")
    public ResponseEntity<Organisation> getOrganisationById(@PathVariable Long id) {
        Optional<Organisation> organisation = organisationRepository.findById(id);
        return organisation.map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Skapa ny organisation
    @PostMapping
    public Organisation createOrganisation(@RequestBody Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    // Uppdatera en organisation
    @PutMapping("/{id}")
    public ResponseEntity<Organisation> updateOrganisation(@PathVariable Long id, @RequestBody Organisation updatedOrg) {
        return organisationRepository.findById(id)
            .map(existingOrg -> {
                existingOrg.setName(updatedOrg.getName());
                existingOrg.setAdress(updatedOrg.getAdress());
                existingOrg.setDescription(updatedOrg.getDescription());
                return ResponseEntity.ok(organisationRepository.save(existingOrg));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Radera en organisation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        if (organisationRepository.existsById(id)) {
            organisationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Sök organisationer på namn
    @GetMapping("/search")
    public List<Organisation> searchByName(@RequestParam String name) {
        return organisationRepository.findByNameContainingIgnoreCase(name);
    }
}
