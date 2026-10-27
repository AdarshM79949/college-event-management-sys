package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.RegistrationRequestDto;
import com.college.eventmanagement.dto.RegistrationResponseDto;
import com.college.eventmanagement.model.User;

import java.util.List;

public interface RegistrationService {
    RegistrationResponseDto registerForEvent(RegistrationRequestDto dto, User student);
    void cancelRegistration(Long eventId, User student);
    List<RegistrationResponseDto> getMyRegistrations(User student);
    List<RegistrationResponseDto> getEventParticipants(Long eventId, User requester);
    RegistrationResponseDto verifyAndCheckInAttendee(String qrToken);
}
