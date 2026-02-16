package com.cdss.repository;

import com.cdss.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findBySuggestionId(Long suggestionId);
    List<Feedback> findByUserId(Long userId);
}
