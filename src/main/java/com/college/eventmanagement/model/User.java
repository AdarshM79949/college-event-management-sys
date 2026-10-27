package com.college.eventmanagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "college_id")
    private String collegeId;

    private String branch;

    private String semester;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "enrollment_number")
    private String enrollmentNumber;

    @Column(name = "is_verified")
    @Builder.Default
    private boolean isVerified = true;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}

    public User(Long id, String name, String email, String password, Role role,
                String collegeId, String branch, String semester, String phoneNumber,
                String enrollmentNumber, boolean isVerified, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.collegeId = collegeId;
        this.branch = branch;
        this.semester = semester;
        this.phoneNumber = phoneNumber;
        this.enrollmentNumber = enrollmentNumber;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }
}
