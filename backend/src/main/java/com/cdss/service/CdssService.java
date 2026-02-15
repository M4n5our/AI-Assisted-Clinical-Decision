package com.cdss.service;

import com.cdss.dto.PatientRequest;
import com.cdss.dto.PredictionResponse;
import com.cdss.model.Suggestion;
import com.cdss.repository.SuggestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CdssService {

    private final SuggestionRepository suggestionRepository;
    private final RestTemplate restTemplate;
    private final String modelServiceUrl;

    public CdssService(
            SuggestionRepository suggestionRepository,
            RestTemplate restTemplate,
            @Value("${cdss.model-service.url}") String modelServiceUrl) {
        this.suggestionRepository = suggestionRepository;
        this.restTemplate = restTemplate;
        this.modelServiceUrl = modelServiceUrl;
    }

    public Suggestion generateSuggestion(PatientRequest request) {
        // Build payload for model service
        Map<String, Object> payload = new HashMap<>();
        payload.put("age", request.getAge());
        payload.put("systolic_bp", request.getSystolicBp());
        payload.put("cholesterol", request.getCholesterol());
        payload.put("glucose", request.getGlucose());
        payload.put("bmi", request.getBmi());

        // Call FastAPI model service
        PredictionResponse prediction = restTemplate.postForObject(
                modelServiceUrl + "/predict",
                payload,
                PredictionResponse.class
        );

        if (prediction == null) {
            throw new RuntimeException("Model service returned null response");
        }

        // Map to entity and save
        Suggestion suggestion = new Suggestion();
        suggestion.setAge(request.getAge());
        suggestion.setSystolicBp(request.getSystolicBp());
        suggestion.setCholesterol(request.getCholesterol());
        suggestion.setGlucose(request.getGlucose());
        suggestion.setBmi(request.getBmi());
        suggestion.setRiskScore(prediction.getRiskScore());
        suggestion.setRiskLevel(prediction.getRiskLevel());
        suggestion.setExplanation(prediction.getExplanation());

        return suggestionRepository.save(suggestion);
    }

    public List<Suggestion> getHistory() {
        return suggestionRepository.findAllByOrderByCreatedAtDesc();
    }
}
