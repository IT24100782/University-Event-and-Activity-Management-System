package com.unievent.repository;

import com.unievent.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEventId(Long eventId);
    List<Feedback> findByStudentId(Long studentId);

    // Analytics queries
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.event.id = ?1")
    long countFeedbackByEvent(Long eventId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.event.id = ?1")
    Double getAverageRatingByEvent(Long eventId);

    @Query("SELECT f FROM Feedback f WHERE f.event.id IN (SELECT e.id FROM Event e WHERE e.status = 'COMPLETED')")
    List<Feedback> findFeedbackForCompletedEvents();

    @Query("SELECT f FROM Feedback f WHERE f.event.id IN " +
           "(SELECT e.id FROM Event e WHERE e.status = 'COMPLETED' AND " +
           "(e.registrationRequired = false OR " +
           "EXISTS (SELECT r FROM Registration r WHERE r.event.id = e.id AND r.student.id = ?1 AND r.status = 'CONFIRMED')))")
    List<Feedback> findEligibleFeedback(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Feedback f WHERE f.event.id = ?1")
    void deleteByEventId(Long eventId);
}
