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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EncounterService {

    private final EncounterRepository encounterRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    // Valid observation types and their accepted ranges (matching model service)
    private static final Map<String, double[]> OBSERVATION_RANGES = Map.of(
            "age", new double[]{0, 150},
            "systolic_bp", new double[]{50, 300},
            "cholesterol", new double[]{50, 500},
            "glucose", new double[]{20, 500},
            "bmi", new double[]{10.0, 80.0}
    );

    public EncounterService(EncounterRepository encounterRepository,
                            PatientRepository patientRepository,
                            UserRepository userRepository) {
        this.encounterRepository = encounterRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    private void validateObservations(List<ObservationDto> observations) {
        if (observations == null || observations.isEmpty()) {
            return;
        }
        for (ObservationDto obs : observations) {
            String type = obs.getType().toLowerCase();
            if (!OBSERVATION_RANGES.containsKey(type)) {
                throw new IllegalArgumentException(
                        "Invalid observation type: '" + obs.getType()
                                + "'. Valid types are: " + String.join(", ", OBSERVATION_RANGES.keySet()));
            }
            double[] range = OBSERVATION_RANGES.get(type);
            if (obs.getValue() < range[0] || obs.getValue() > range[1]) {
                throw new IllegalArgumentException(
                        "Observation '" + obs.getType() + "' value " + obs.getValue()
                                + " is out of range. Accepted range: " + range[0] + " - " + range[1]);
            }
        }
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

        validateObservations(dto.getObservations());

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
