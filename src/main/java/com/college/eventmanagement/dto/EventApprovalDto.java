package com.college.eventmanagement.dto;

import com.college.eventmanagement.model.EventStatus;
import lombok.Data;

@Data
public class EventApprovalDto {
    private EventStatus status; // APPROVED or REJECTED
    private String adminComment;
}
