package com.cdss.controller;

import com.cdss.model.Suggestion;
import com.cdss.service.CdssService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CdssController.class)
class CdssControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CdssService cdssService;

    @Test
    void generate_withValidInput_shouldReturn200() throws Exception {
        Suggestion suggestion = new Suggestion();
        suggestion.setId(1L);
        suggestion.setAge(65);
        suggestion.setSystolicBp(150);
        suggestion.setCholesterol(250);
        suggestion.setGlucose(130);
        suggestion.setBmi(32.0);
        suggestion.setRiskScore(1.0);
        suggestion.setRiskLevel("High");
        suggestion.setExplanation("All factors elevated");

        when(cdssService.generateSuggestion(any())).thenReturn(suggestion);

        mockMvc.perform(post("/api/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"age\":65,\"systolicBp\":150,\"cholesterol\":250,\"glucose\":130,\"bmi\":32.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("High"))
                .andExpect(jsonPath("$.riskScore").value(1.0));
    }

    @Test
    void generate_withMissingFields_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"age\":65}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generate_withOutOfRangeAge_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"age\":200,\"systolicBp\":150,\"cholesterol\":250,\"glucose\":130,\"bmi\":32.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void history_shouldReturnList() throws Exception {
        Suggestion s = new Suggestion();
        s.setId(1L);
        s.setRiskLevel("Low");
        s.setRiskScore(0.0);
        s.setExplanation("Normal");

        when(cdssService.getHistory()).thenReturn(List.of(s));

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].riskLevel").value("Low"));
    }

    @Test
    void health_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Backend is running"));
    }
}
