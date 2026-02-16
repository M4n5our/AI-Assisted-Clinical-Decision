package com.cdss.service;

import com.cdss.dto.FeedbackDto;
import com.cdss.model.Feedback;
import com.cdss.model.Suggestion;
import com.cdss.model.User;
import com.cdss.repository.FeedbackRepository;
import com.cdss.repository.SuggestionRepository;
import com.cdss.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final SuggestionRepository suggestionRepository;
    private final UserRepository userRepository;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           SuggestionRepository suggestionRepository,
                           UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.suggestionRepository = suggestionRepository;
        this.userRepository = userRepository;
    }

    public FeedbackDto createFeedback(FeedbackDto dto, String username) {
        Suggestion suggestion = suggestionRepository.findById(dto.getSuggestionId())
                .orElseThrow(() -> new RuntimeException("Suggestion not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Feedback feedback = new Feedback();
        feedback.setSuggestion(suggestion);
        feedback.setUser(user);
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());

        return toDto(feedbackRepository.save(feedback));
    }

    public List<FeedbackDto> getFeedbackBySuggestion(Long suggestionId) {
        return feedbackRepository.findBySuggestionId(suggestionId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<FeedbackDto> getAllFeedback() {
        return feedbackRepository.findAll()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private FeedbackDto toDto(Feedback feedback) {
        FeedbackDto dto = new FeedbackDto();
        dto.setId(feedback.getId());
        dto.setSuggestionId(feedback.getSuggestion().getId());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setUsername(feedback.getUser().getUsername());
        dto.setCreatedAt(feedback.getCreatedAt() != null ? feedback.getCreatedAt().toString() : null);
        return dto;
    }
}
