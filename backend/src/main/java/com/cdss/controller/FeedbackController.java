package com.cdss.controller;

import com.cdss.dto.FeedbackDto;
import com.cdss.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<FeedbackDto> createFeedback(@Valid @RequestBody FeedbackDto dto,
                                                       Authentication auth) {
        return ResponseEntity.ok(feedbackService.createFeedback(dto, auth.getName()));
    }

    @GetMapping("/suggestion/{suggestionId}")
    public ResponseEntity<List<FeedbackDto>> getFeedbackBySuggestion(@PathVariable Long suggestionId) {
        return ResponseEntity.ok(feedbackService.getFeedbackBySuggestion(suggestionId));
    }

    @GetMapping
    public ResponseEntity<List<FeedbackDto>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }
}
