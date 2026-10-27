package com.college.eventmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventRequestDto {

    @NotBlank(message = "Event title is required")
    private String title;

    private String tagline;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    private String department;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    @NotBlank(message = "Event time is required")
    private String eventTime;

    private Long venueId;

    private String bannerPath;

    private LocalDate registrationDeadline;

    private Integer maxSeats = 100;

    private String priceType = "Free";

    private Double price = 0.0;
}
