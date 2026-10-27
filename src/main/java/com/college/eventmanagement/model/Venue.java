package com.college.eventmanagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "venues")
@Getter
@Setter
@Builder
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String type; // Auditorium, Sports Complex, Lab, Theater

    private Integer capacity;

    private String location;

    @Column(length = 1000)
    private String facilities;

    private String imageUrl;

    public Venue() {}

    public Venue(Long id, String name, String type, Integer capacity, String location, String facilities, String imageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.location = location;
        this.facilities = facilities;
        this.imageUrl = imageUrl;
    }
}
