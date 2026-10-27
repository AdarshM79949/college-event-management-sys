package com.college.eventmanagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "student_id"})
})
@Getter
@Setter
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "ticket_tier")
    @Builder.Default
    private String ticketTier = "Standard Pass";

    @Column(name = "price_paid")
    @Builder.Default
    private Double pricePaid = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.CONFIRMED;

    @Column(name = "qr_token", unique = true, nullable = false)
    private String qrToken;

    @Column(name = "checked_in")
    @Builder.Default
    private boolean checkedIn = false;

    @Column(name = "registered_at", updatable = false)
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();

    public Registration() {}

    public Registration(Long id, Event event, User student, String ticketTier, Double pricePaid,
                        RegistrationStatus status, String qrToken, boolean checkedIn, LocalDateTime registeredAt) {
        this.id = id;
        this.event = event;
        this.student = student;
        this.ticketTier = ticketTier;
        this.pricePaid = pricePaid;
        this.status = status;
        this.qrToken = qrToken;
        this.checkedIn = checkedIn;
        this.registeredAt = registeredAt;
    }
}
