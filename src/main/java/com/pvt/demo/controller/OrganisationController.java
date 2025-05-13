package com.pvt.demo.controller;

import com.pvt.demo.dto.OrganisationDto;
import com.pvt.demo.model.Organisation;
import com.pvt.demo.repository.OrganisationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/organisations")
@CrossOrigin(origins = "*") // Tillåt alla ursprung för CORS, justera vid behov
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
    @PostMapping("/create")
    public Organisation createOrganisation(@RequestBody OrganisationDto dto) {
        Organisation organisation = new Organisation();
        organisation.setName(dto.name);
        organisation.setAdress(dto.adress);
        organisation.setDescription(dto.description);

        return organisationRepository.save(organisation);
    }


    // Uppdatera en organisation
    @PutMapping("/update/{id}")
    public ResponseEntity<Organisation> updateOrganisation(@PathVariable Long id, @RequestBody OrganisationDto dto) {
        return organisationRepository.findById(id)
            .map(existingOrg -> {
                existingOrg.setName(dto.name);
                existingOrg.setAdress(dto.adress);
                existingOrg.setDescription(dto.description);
                organisationRepository.save(existingOrg);
                return ResponseEntity.ok(existingOrg);
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
