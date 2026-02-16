package com.cdss.service;

import com.cdss.dto.EncounterDto;
import com.cdss.dto.ObservationDto;
import com.cdss.model.Encounter;
import com.cdss.model.Observation;
import com.cdss.model.Patient;
import com.cdss.model.User;
import com.cdss.repository.EncounterRepository;
import com.cdss.repository.PatientRepository;
import com.cdss.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EncounterService {

    private final EncounterRepository encounterRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public EncounterService(EncounterRepository encounterRepository,
                            PatientRepository patientRepository,
                            UserRepository userRepository) {
        this.encounterRepository = encounterRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public List<EncounterDto> getEncountersByPatient(Long patientId) {
        return encounterRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public EncounterDto getEncounter(Long id) {
        Encounter encounter = encounterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Encounter not found"));
        return toDto(encounter);
    }

    @Transactional
    public EncounterDto createEncounter(EncounterDto dto, String username) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setUser(user);
        encounter.setNotes(dto.getNotes());

        if (dto.getObservations() != null) {
            for (ObservationDto obsDto : dto.getObservations()) {
                Observation obs = new Observation();
                obs.setType(obsDto.getType());
                obs.setValue(obsDto.getValue());
                obs.setUnit(obsDto.getUnit());
                obs.setEncounter(encounter);
                encounter.getObservations().add(obs);
            }
        }

        return toDto(encounterRepository.save(encounter));
    }

    private EncounterDto toDto(Encounter encounter) {
        EncounterDto dto = new EncounterDto();
        dto.setId(encounter.getId());
        dto.setPatientId(encounter.getPatient().getId());
        dto.setPatientName(encounter.getPatient().getFirstName() + " " + encounter.getPatient().getLastName());
        dto.setNotes(encounter.getNotes());
        dto.setCreatedAt(encounter.getCreatedAt() != null ? encounter.getCreatedAt().toString() : null);

        List<ObservationDto> obsDtos = new ArrayList<>();
        if (encounter.getObservations() != null) {
            for (Observation obs : encounter.getObservations()) {
                ObservationDto obsDto = new ObservationDto();
                obsDto.setId(obs.getId());
                obsDto.setType(obs.getType());
                obsDto.setValue(obs.getValue());
                obsDto.setUnit(obs.getUnit());
                obsDtos.add(obsDto);
            }
        }
        dto.setObservations(obsDtos);
        return dto;
    }
}
