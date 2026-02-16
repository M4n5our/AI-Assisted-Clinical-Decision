package com.cdss.service;

import com.cdss.dto.ObservationDto;
import com.cdss.dto.PredictionResponse;
import com.cdss.dto.SuggestionDto;
import com.cdss.model.Encounter;
import com.cdss.model.Observation;
import com.cdss.model.Suggestion;
import com.cdss.model.Template;
import com.cdss.repository.EncounterRepository;
import com.cdss.repository.SuggestionRepository;
import com.cdss.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final EncounterRepository encounterRepository;
    private final TemplateRepository templateRepository;
    private final RestTemplate restTemplate;
    private final String modelServiceUrl;

    public SuggestionService(SuggestionRepository suggestionRepository,
                             EncounterRepository encounterRepository,
                             TemplateRepository templateRepository,
                             RestTemplate restTemplate,
                             @Value("${cdss.model-service.url}") String modelServiceUrl) {
        this.suggestionRepository = suggestionRepository;
        this.encounterRepository = encounterRepository;
        this.templateRepository = templateRepository;
        this.restTemplate = restTemplate;
        this.modelServiceUrl = modelServiceUrl;
    }

    public SuggestionDto generateSuggestion(Long encounterId) {
        Encounter encounter = encounterRepository.findById(encounterId)
                .orElseThrow(() -> new RuntimeException("Encounter not found"));

        // Build payload from observations
        Map<String, Object> payload = new HashMap<>();
        for (Observation obs : encounter.getObservations()) {
            switch (obs.getType().toLowerCase()) {
                case "age" -> payload.put("age", (int) obs.getValue());
                case "systolic_bp" -> payload.put("systolic_bp", (int) obs.getValue());
                case "cholesterol" -> payload.put("cholesterol", (int) obs.getValue());
                case "glucose" -> payload.put("glucose", (int) obs.getValue());
                case "bmi" -> payload.put("bmi", obs.getValue());
            }
        }

        // Call model service
        PredictionResponse prediction = restTemplate.postForObject(
                modelServiceUrl + "/predict",
                payload,
                PredictionResponse.class
        );

        if (prediction == null) {
            throw new RuntimeException("Model service returned null response");
        }

        Suggestion suggestion = new Suggestion();
        suggestion.setEncounter(encounter);
        suggestion.setRiskScore(prediction.getRiskScore());
        suggestion.setRiskLevel(prediction.getRiskLevel());
        suggestion.setExplanation(prediction.getExplanation());

        return toDto(suggestionRepository.save(suggestion));
    }

    public List<SuggestionDto> getSuggestionsByEncounter(Long encounterId) {
        return suggestionRepository.findByEncounterIdOrderByCreatedAtDesc(encounterId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SuggestionDto> getAllSuggestions() {
        return suggestionRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private SuggestionDto toDto(Suggestion suggestion) {
        SuggestionDto dto = new SuggestionDto();
        dto.setId(suggestion.getId());
        dto.setEncounterId(suggestion.getEncounter().getId());
        dto.setRiskScore(suggestion.getRiskScore());
        dto.setRiskLevel(suggestion.getRiskLevel());
        dto.setExplanation(suggestion.getExplanation());
        dto.setTemplateId(suggestion.getTemplate() != null ? suggestion.getTemplate().getId() : null);
        dto.setCreatedAt(suggestion.getCreatedAt() != null ? suggestion.getCreatedAt().toString() : null);
        return dto;
    }
}
