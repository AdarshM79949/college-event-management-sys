package com.college.eventmanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackDto {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Minimum rating is 1 star")
    @Max(value = 5, message = "Maximum rating is 5 stars")
    private Integer rating;

    private String comment;
}
