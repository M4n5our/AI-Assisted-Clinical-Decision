package com.cdss.controller;

import com.cdss.dto.EncounterDto;
import com.cdss.service.EncounterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encounters")
public class EncounterController {

    private final EncounterService encounterService;

    public EncounterController(EncounterService encounterService) {
        this.encounterService = encounterService;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<EncounterDto>> getEncountersByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(encounterService.getEncountersByPatient(patientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncounterDto> getEncounter(@PathVariable Long id) {
        return ResponseEntity.ok(encounterService.getEncounter(id));
    }

    @PostMapping
    public ResponseEntity<EncounterDto> createEncounter(@Valid @RequestBody EncounterDto dto,
                                                         Authentication auth) {
        return ResponseEntity.ok(encounterService.createEncounter(dto, auth.getName()));
    }
}
