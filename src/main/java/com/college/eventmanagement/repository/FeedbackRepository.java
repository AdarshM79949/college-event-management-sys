package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.Feedback;
import com.college.eventmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEvent(Event event);
    boolean existsByEventAndStudent(Event event, User student);
}
