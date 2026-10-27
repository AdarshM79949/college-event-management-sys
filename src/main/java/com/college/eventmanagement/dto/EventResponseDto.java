package com.college.eventmanagement.dto;

import com.college.eventmanagement.model.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EventResponseDto {
    private Long id;
    private String title;
    private String tagline;
    private String description;
    private String category;
    private String department;
    private LocalDate eventDate;
    private String eventTime;
    private Long venueId;
    private String venueName;
    private String bannerPath;
    private LocalDate registrationDeadline;
    private Integer maxSeats;
    private Integer registeredCount;
    private String priceType;
    private Double price;
    private EventStatus status;
    private String adminComment;
    private String organizerName;

    public EventResponseDto() {}

    public EventResponseDto(Long id, String title, String tagline, String description, String category,
                            String department, LocalDate eventDate, String eventTime, Long venueId,
                            String venueName, String bannerPath, LocalDate registrationDeadline,
                            Integer maxSeats, Integer registeredCount, String priceType, Double price,
                            EventStatus status, String adminComment, String organizerName) {
        this.id = id;
        this.title = title;
        this.tagline = tagline;
        this.description = description;
        this.category = category;
        this.department = department;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.venueId = venueId;
        this.venueName = venueName;
        this.bannerPath = bannerPath;
        this.registrationDeadline = registrationDeadline;
        this.maxSeats = maxSeats;
        this.registeredCount = registeredCount;
        this.priceType = priceType;
        this.price = price;
        this.status = status;
        this.adminComment = adminComment;
        this.organizerName = organizerName;
    }
}
