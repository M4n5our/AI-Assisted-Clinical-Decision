package com.cdss.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class EncounterDto {

    private Long id;

    @NotNull
    private Long patientId;

    private String patientName;
    private String notes;
    private List<ObservationDto> observations;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<ObservationDto> getObservations() { return observations; }
    public void setObservations(List<ObservationDto> observations) { this.observations = observations; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
