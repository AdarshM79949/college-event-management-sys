package com.college.eventmanagement.dto;

import lombok.Data;

@Data
public class RegistrationRequestDto {
    private Long eventId;
    private String ticketTier = "Standard Pass";
    private Double pricePaid = 0.0;
}
