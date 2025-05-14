package com.pvt.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pvt.demo.controller.OrganisationController;
import com.pvt.demo.dto.OrganisationDto;
import com.pvt.demo.model.Organisation;
import com.pvt.demo.repository.OrganisationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganisationController.class)
public class OrganisationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrganisationRepository organisationRepository;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public OrganisationRepository organisationRepository() {
            return Mockito.mock(OrganisationRepository.class);
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetAllOrganisations() throws Exception {
        Organisation org = new Organisation();
        org.setName("Chess Club");
        org.setAdress("123 Street");
        org.setDescription("A club for chess lovers");

        Mockito.when(organisationRepository.findAll())
               .thenReturn(List.of(org));

        mockMvc.perform(get("/organisations"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("Chess Club"))
               .andExpect(jsonPath("$[0].adress").value("123 Street"))
               .andExpect(jsonPath("$[0].description").value("A club for chess lovers"));
    }

    @Test
    public void testGetOrganisationById_found() throws Exception {
        Organisation org = new Organisation();
        org.setName("Basket Club");
        Mockito.when(organisationRepository.findById(1L))
               .thenReturn(Optional.of(org));

        mockMvc.perform(get("/organisations/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Basket Club"));
    }

    @Test
    public void testGetOrganisationById_notFound() throws Exception {
        Mockito.when(organisationRepository.findById(999L))
               .thenReturn(Optional.empty());

        mockMvc.perform(get("/organisations/999"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateOrganisation() throws Exception {
        OrganisationDto dto = new OrganisationDto();
        dto.name = "Football Club";
        dto.adress = "Stadium Road";
        dto.description = "Football for all ages";

        Organisation savedOrg = new Organisation();
        savedOrg.setName(dto.name);
        savedOrg.setAdress(dto.adress);
        savedOrg.setDescription(dto.description);

        Mockito.when(organisationRepository.save(any(Organisation.class)))
               .thenReturn(savedOrg);

        mockMvc.perform(post("/organisations/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Football Club"))
               .andExpect(jsonPath("$.adress").value("Stadium Road"))
               .andExpect(jsonPath("$.description").value("Football for all ages"));
    }

    @Test
    public void testUpdateOrganisation_success() throws Exception {
        Organisation existing = new Organisation();
        existing.setName("Old Name");
        existing.setAdress("Old Address");
        existing.setDescription("Old Desc");

        OrganisationDto dto = new OrganisationDto();
        dto.name = "New Name";
        dto.adress = "New Address";
        dto.description = "New Desc";

        Mockito.when(organisationRepository.findById(5L))
               .thenReturn(Optional.of(existing));
        Mockito.when(organisationRepository.save(any(Organisation.class)))
               .thenReturn(existing);

        mockMvc.perform(put("/organisations/update/5")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("New Name"))
               .andExpect(jsonPath("$.adress").value("New Address"))
               .andExpect(jsonPath("$.description").value("New Desc"));
    }

    @Test
    public void testUpdateOrganisation_notFound() throws Exception {
        OrganisationDto dto = new OrganisationDto();
        dto.name = "New Name";
        dto.adress = "New Address";
        dto.description = "New Desc";

        Mockito.when(organisationRepository.findById(999L))
               .thenReturn(Optional.empty());

        mockMvc.perform(put("/organisations/update/999")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteOrganisation_found() throws Exception {
        Mockito.when(organisationRepository.existsById(1L))
               .thenReturn(true);

        mockMvc.perform(delete("/organisations/1"))
               .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteOrganisation_notFound() throws Exception {
        Mockito.when(organisationRepository.existsById(999L))
               .thenReturn(false);

        mockMvc.perform(delete("/organisations/999"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchByName() throws Exception {
        Organisation org = new Organisation();
        org.setName("Gaming Club");

        Mockito.when(organisationRepository.findByNameContainingIgnoreCase("game"))
               .thenReturn(List.of(org));

        mockMvc.perform(get("/organisations/search")
                .param("name", "game"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("Gaming Club"));
    }
}