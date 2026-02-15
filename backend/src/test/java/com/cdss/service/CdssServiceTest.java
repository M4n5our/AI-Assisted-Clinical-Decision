package com.cdss.service;

import com.cdss.dto.PatientRequest;
import com.cdss.dto.PredictionResponse;
import com.cdss.model.Suggestion;
import com.cdss.repository.SuggestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CdssServiceTest {

    @Mock
    private SuggestionRepository suggestionRepository;

    @Mock
    private RestTemplate restTemplate;

    private CdssService cdssService;

    @BeforeEach
    void setUp() {
        cdssService = new CdssService(
                suggestionRepository,
                restTemplate,
                "http://localhost:8000"
        );
    }

    @Test
    void generateSuggestion_shouldCallModelServiceAndSave() {
        PatientRequest request = new PatientRequest();
        request.setAge(65);
        request.setSystolicBp(150);
        request.setCholesterol(250);
        request.setGlucose(130);
        request.setBmi(32.0);

        PredictionResponse prediction = new PredictionResponse();
        prediction.setRiskScore(1.0);
        prediction.setRiskLevel("High");
        prediction.setExplanation("All factors elevated");

        when(restTemplate.postForObject(
                eq("http://localhost:8000/predict"),
                any(),
                eq(PredictionResponse.class)
        )).thenReturn(prediction);

        when(suggestionRepository.save(any(Suggestion.class)))
                .thenAnswer(invocation -> {
                    Suggestion s = invocation.getArgument(0);
                    s.setId(1L);
                    return s;
                });

        Suggestion result = cdssService.generateSuggestion(request);

        assertNotNull(result);
        assertEquals(65, result.getAge());
        assertEquals(150, result.getSystolicBp());
        assertEquals(1.0, result.getRiskScore());
        assertEquals("High", result.getRiskLevel());
        verify(restTemplate).postForObject(anyString(), any(), eq(PredictionResponse.class));
        verify(suggestionRepository).save(any(Suggestion.class));
    }

    @Test
    void generateSuggestion_shouldThrowWhenModelReturnsNull() {
        PatientRequest request = new PatientRequest();
        request.setAge(30);
        request.setSystolicBp(120);
        request.setCholesterol(200);
        request.setGlucose(90);
        request.setBmi(22.0);

        when(restTemplate.postForObject(anyString(), any(), eq(PredictionResponse.class)))
                .thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> cdssService.generateSuggestion(request));
    }

    @Test
    void getHistory_shouldReturnSuggestionsOrderedByDate() {
        Suggestion s1 = new Suggestion();
        s1.setId(1L);
        s1.setRiskLevel("Low");
        Suggestion s2 = new Suggestion();
        s2.setId(2L);
        s2.setRiskLevel("High");

        when(suggestionRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(s2, s1));

        List<Suggestion> result = cdssService.getHistory();

        assertEquals(2, result.size());
        assertEquals("High", result.get(0).getRiskLevel());
        assertEquals("Low", result.get(1).getRiskLevel());
    }
}
