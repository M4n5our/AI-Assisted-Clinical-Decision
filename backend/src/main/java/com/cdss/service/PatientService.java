package com.cdss.service;

import com.cdss.dto.PatientDto;
import com.cdss.model.Patient;
import com.cdss.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientDto> getAllPatients() {
        return patientRepository.findAllByOrderByLastNameAsc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public PatientDto getPatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return toDto(patient);
    }

    public PatientDto createPatient(PatientDto dto) {
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        patient.setGender(Patient.Gender.valueOf(dto.getGender().toUpperCase()));
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        return toDto(patientRepository.save(patient));
    }

    public PatientDto updatePatient(Long id, PatientDto dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        patient.setGender(Patient.Gender.valueOf(dto.getGender().toUpperCase()));
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        return toDto(patientRepository.save(patient));
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    public List<PatientDto> searchPatients(String lastName) {
        return patientRepository.findByLastNameContainingIgnoreCase(lastName)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private PatientDto toDto(Patient patient) {
        PatientDto dto = new PatientDto();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setDateOfBirth(patient.getDateOfBirth().toString());
        dto.setGender(patient.getGender().name());
        dto.setPhone(patient.getPhone());
        dto.setEmail(patient.getEmail());
        dto.setCreatedAt(patient.getCreatedAt() != null ? patient.getCreatedAt().toString() : null);
        return dto;
    }
}
