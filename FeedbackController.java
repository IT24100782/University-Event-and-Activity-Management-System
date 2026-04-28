package com.unievent.controller;

import com.unievent.entity.Feedback;
import com.unievent.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@RequestBody com.unievent.dto.FeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.submitFeedbackRequest(request));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Feedback>> getFeedbackByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByEvent(eventId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Feedback>> getFeedbackByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByStudent(studentId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllCompletedEventsFeedback());
    }

    @GetMapping("/eligible/{userId}")
    public ResponseEntity<List<Feedback>> getEligibleFeedback(@PathVariable Long userId) {
        return ResponseEntity.ok(feedbackService.getEligibleFeedback(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.ok().build();
    }

    // ============ ADMIN ENDPOINTS ============

    /**
     * Get feedback count for an event
     */
    @GetMapping("/admin/event/{eventId}/count")
    public ResponseEntity<Long> getFeedbackCount(@PathVariable Long eventId) {
        return ResponseEntity.ok(feedbackService.getFeedbackCountByEvent(eventId));
    }

    /**
     * Get average rating for an event
     */
    @GetMapping("/admin/event/{eventId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long eventId) {
        return ResponseEntity.ok(feedbackService.getAverageRatingByEvent(eventId));
    }

    /**
     * Get feedback analytics for an event (comprehensive)
     */
    @GetMapping("/admin/event/{eventId}/analytics")
    public ResponseEntity<Map<String, Object>> getEventAnalytics(@PathVariable Long eventId) {
        return ResponseEntity.ok(feedbackService.getEventAnalytics(eventId));
    }

    /**
     * Get all feedbacks for completed events
     */
    @GetMapping("/admin/completed-events")
    public ResponseEntity<List<Feedback>> getAllCompletedEventsFeedback() {
        return ResponseEntity.ok(feedbackService.getAllCompletedEventsFeedback());
    }
}
