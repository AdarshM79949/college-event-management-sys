package com.college.eventmanagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "student_id"})
})
@Getter
@Setter
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private Integer rating; // 1 to 5 stars

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public Feedback() {}

    public Feedback(Long id, Event event, User student, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.event = event;
        this.student = student;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}
