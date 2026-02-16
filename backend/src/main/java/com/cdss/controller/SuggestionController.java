package com.cdss.controller;

import com.cdss.dto.SuggestionDto;
import com.cdss.service.SuggestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @PostMapping("/generate/{encounterId}")
    public ResponseEntity<SuggestionDto> generateSuggestion(@PathVariable Long encounterId) {
        return ResponseEntity.ok(suggestionService.generateSuggestion(encounterId));
    }

    @GetMapping("/encounter/{encounterId}")
    public ResponseEntity<List<SuggestionDto>> getSuggestionsByEncounter(@PathVariable Long encounterId) {
        return ResponseEntity.ok(suggestionService.getSuggestionsByEncounter(encounterId));
    }

    @GetMapping
    public ResponseEntity<List<SuggestionDto>> getAllSuggestions() {
        return ResponseEntity.ok(suggestionService.getAllSuggestions());
    }
}
