package com.cdss.controller;

import com.cdss.dto.PatientRequest;
import com.cdss.model.Suggestion;
import com.cdss.service.CdssService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CdssController {

    private final CdssService cdssService;

    public CdssController(CdssService cdssService) {
        this.cdssService = cdssService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Suggestion> generate(@Valid @RequestBody PatientRequest request) {
        Suggestion suggestion = cdssService.generateSuggestion(request);
        return ResponseEntity.ok(suggestion);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend is running");
    }
}
