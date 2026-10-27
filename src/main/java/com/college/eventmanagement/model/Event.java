package com.college.eventmanagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String tagline;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category; // Tech, Cultural, Sports, Academic, Workshop, Gaming

    private String department;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_time", nullable = false)
    private String eventTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Column(name = "banner_path")
    private String bannerPath;

    @Column(name = "registration_deadline")
    private LocalDate registrationDeadline;

    @Column(name = "max_seats", nullable = false)
    @Builder.Default
    private Integer maxSeats = 100;

    @Column(name = "registered_count")
    @Builder.Default
    private Integer registeredCount = 0;

    @Column(name = "price_type")
    @Builder.Default
    private String priceType = "Free"; // Free / Paid

    @Builder.Default
    private Double price = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EventStatus status = EventStatus.PENDING;

    @Column(name = "admin_comment")
    private String adminComment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public Event() {}

    public Event(Long id, String title, String tagline, String description, String category,
                 String department, LocalDate eventDate, String eventTime, Venue venue,
                 String bannerPath, LocalDate registrationDeadline, Integer maxSeats,
                 Integer registeredCount, String priceType, Double price, EventStatus status,
                 String adminComment, User createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.tagline = tagline;
        this.description = description;
        this.category = category;
        this.department = department;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venue = venue;
        this.bannerPath = bannerPath;
        this.registrationDeadline = registrationDeadline;
        this.maxSeats = maxSeats;
        this.registeredCount = registeredCount;
        this.priceType = priceType;
        this.price = price;
        this.status = status;
        this.adminComment = adminComment;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}
