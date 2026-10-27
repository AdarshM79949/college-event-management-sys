package com.college.eventmanagement.dto;

import com.college.eventmanagement.model.RegistrationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegistrationResponseDto {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String studentName;
    private String rollNo;
    private String email;
    private String ticketTier;
    private Double pricePaid;
    private RegistrationStatus status;
    private String qrToken;
    private String qrCodeBase64;
    private boolean checkedIn;
    private LocalDateTime registeredAt;

    public RegistrationResponseDto() {}

    public RegistrationResponseDto(Long id, Long eventId, String eventTitle, String studentName,
                                   String rollNo, String email, String ticketTier, Double pricePaid,
                                   RegistrationStatus status, String qrToken, String qrCodeBase64,
                                   boolean checkedIn, LocalDateTime registeredAt) {
        this.id = id;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.email = email;
        this.ticketTier = ticketTier;
        this.pricePaid = pricePaid;
        this.status = status;
        this.qrToken = qrToken;
        this.qrCodeBase64 = qrCodeBase64;
        this.checkedIn = checkedIn;
        this.registeredAt = registeredAt;
    }
}
